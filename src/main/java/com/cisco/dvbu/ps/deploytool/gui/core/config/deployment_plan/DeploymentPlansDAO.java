package com.cisco.dvbu.ps.deploytool.gui.core.config.deployment_plan;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;
import com.cisco.dvbu.ps.deploytool.gui.resources.DeploymentPlanResource;
import com.cisco.dvbu.ps.deploytool.gui.util.ListResult;
import com.cisco.dvbu.ps.deploytool.gui.util.ResultMessage;
import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for deployment profiles. Used by {@link DeploymentPlanResource} servlet.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class DeploymentPlansDAO {

    private static final Logger log = LoggerFactory.getLogger (DeploymentPlansDAO.class);
    private static final String stepDetectionRE = "(?i)^#?\\s*(pass|fail)\\s+.*$";
    private static final String tokenWQualRE = "\"[^\"]*\"";
    private static final String tokenWOQualRE = "(?:[^\"\\s]+)";
    private static final String token = "(" + tokenWQualRE + "|" + tokenWOQualRE + ")";
    private static final String stepParseRE = "#?\\s*" + token + "\\s+" + token + "\\s+" + token + "\\s+" + token + "\\s+" + token + "\\s+" + token + "(?:\\s+" + token + ")?(?:\\s+" + token + ")?(?:\\s+" + token + ")?(?:\\s+" + token + ")?(?:\\s+" + token + ")?(?:\\s+" + token + ")?(?:\\s+" + token + ")?(?:\\s+" + token + ")?";
    private static Pattern stepDetectionPattern;
    private static Pattern stepParsePattern;
    
    static {
        stepDetectionPattern = Pattern.compile (stepDetectionRE); // simple pattern used for detecting lines containing plan steps.
        stepParsePattern = Pattern.compile (stepParseRE); // more elaborate (and slower performing) pattern used to parse the plan step.
    }

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public DeploymentPlansDAO () {
        super ();
    }

    /*
     * Servlet methods
     */

    /**
     * <p>
     * Unlike the other DAO's, this doesn't allow sorting, filtering, or paging of the list of {@link DeploymentPlan} objects. It simply
     * returns a {@link ListResult} object containing the entire list of steps in the order of execution.
     * </p>
     * 
     * @param isSearch     Indicates whether the request is a search request ("true" or "false".)
     * @param numRows      Number of rows to return.
     * @param pageNum      Page number to return (based on numRows.)
     * @param sortIndex    Data element to sort on (as represented by a column in the UI.)
     * @param sortOrder    Sort order ("asc" or "desc".)
     * @param filters      Multi-row filtering (not currently used.)
     * @param searchField  Search field to search on (isSearch = "true".)
     * @param searchString String to search for (isSearch = "true".)
     * @param searchOper   Search operation (equals or "eq", not equals or "ne", etc.)
     * @param param        Parameter name identifier to pass back when called to generate parameter value pick lists.
     * @return             The requested list of items.
     * @see                DeploymentPlanResource
     */
    public ListResult list(
        String id,
        String isSearch,
        int    numRows,
        int    pageNum,
        String sortIndex,
        String sortOrder,
        String filters,
        String searchField,
        String searchString,
        String searchOper,
        String param
    ) {
        DeploymentPlan dp;
        List<ListResult.Row> resultList = new ArrayList<ListResult.Row>();

        log.debug ("id = " + id);
        
        dp = getById (id);

        if (dp == null) {
            log.error ("parsed deployment plan is null.");
            return null;
        }
        
        for (Iterator<DeploymentPlan.Step> i = dp.getSteps().iterator(); i.hasNext(); ) {
            DeploymentPlan.Step s = i.next();
            List<String> tmpCell = new ArrayList<String>();
            
            tmpCell.add (s.getLineNum());
            tmpCell.add ((s.isEnabled()) ? "true" : "false");
            tmpCell.add ((s.isExpectedBehavior()) ? "PASS" : "FAIL");
            tmpCell.add ((s.isExitOnError()) ? "true" : "false");
            tmpCell.add (s.getBatchName());
            tmpCell.add (s.getMethod());
            tmpCell.add (s.getParam0());
            tmpCell.add (s.getParam1());
            tmpCell.add (s.getParam2());
            tmpCell.add (s.getParam3());
            tmpCell.add (s.getParam4());
            tmpCell.add (s.getParam5());
            tmpCell.add (s.getParam6());
            tmpCell.add (s.getParam7());
            tmpCell.add (s.getParam8());
            tmpCell.add (s.getParam9());
            
            resultList.add (new ListResult.Row(s.getLineNum() + "", tmpCell));
        }
        
        return new ListResult (
                       1, 
                       1, 
                       dp.getSteps().size(), 
                       resultList,
                       param
                   );
    }
    /**
     * <p>
     * Searches for a single {@link DeploymentPlan} object given a URL encoded file path.
     * </p>
     * 
     * @param  path  The URL encoded ID to look for.
     * @return       The requested {@link DeploymentPlan} as an object.
     * @see          DeploymentPlanResource
     */
    public DeploymentPlan getById (String path) {
        File f;
        DeploymentPlan dp = null;
        
        if (path == null) {
            log.error ("Error: \"path\" input may not be NULL.");
            return null;
        }
        
        f = new File (path);

        if (! f.exists()) {
            log.error ("Error retrieving file \"" + path + "\": file does not exist.");
            return null;
        }
        
        if (! f.canRead()) {
            log.error ("Error retrieving file \"" + path + "\": file is not readable.");
            return null;
        }
        
        try {
            dp = parsePlanFile (f);
        } catch (Exception e) {
            log.error ("Unable to get contents of file \"" + path + "\": " + e.getMessage());
            return null;
        }
        
        return dp;
    }

    /**
     * <p>
     * Updates a {@link DeploymentPlan} properties file and writes it to disk. Attempts to preserve any comments.
     * </p>
     * 
     * @param  dp  The {@link DeploymentPlan} object to update.
     * @return     A {@link ResultMessage} object containing the results of the edit request.
     * @see        DeploymentPlanResource
     */
    public ResultMessage edit (DeploymentPlan dp) {
        List<ResultMessage.MessageItem> msgList = new ArrayList<ResultMessage.MessageItem>();
        File f;
        File ft = new File (FilesDAO.getPdtHome() + "/" + FilesDAO.fileTypeProperties.get (FilesDAO.FILE_TYPE_DEPLOY_PLAN).getTemplate());
        BufferedReader br = null;
        BufferedWriter bw = null;
        
        log.debug ("checking inputs");
        
        if (dp == null) {
            return new ResultMessage ("error", "Error: deployment plan may not be NULL.", null);
        }
        
        if (dp.getPath() == null) {
            return new ResultMessage ("error", "Error: \"path\" attribute may not be NULL.", null);
        }
        
        f = new File (dp.getPath());

        if (! f.exists()) {
            return new ResultMessage ("error", "Error retrieving file \"" + dp.getPath() + "\": file does not exist.", null);
        }
        
        if (! f.canWrite()) {
            return new ResultMessage ("error", "Error retrieving file \"" + dp.getPath() + "\": file is not writeable.", null);
        }

        if (! ft.exists()) {
            return new ResultMessage ("error", "Error retrieving file \"" + ft.getPath() + "\": file does not exist.", null);
        }
        
        if (! ft.canRead()) {
            return new ResultMessage ("error", "Error retrieving file \"" + ft.getPath() + "\": file is not readable.", null);
        }
        
        // iterate over all the input deployment steps and validate them
        //
        for (int i = 0; i < dp.getSteps().size(); i++) {
            DeploymentPlan.Step s = dp.getSteps().get(i);
            
            msgList.addAll (validateStep (s));
        }
        
        // if there are any validation errors, return
        //
        if (msgList.size() > 0)
            return new ResultMessage ("error", null, msgList);
        
        try {
            String line;
            
            // if user preferences indicate to save backups of edited files, rename the original file.
            //
            if (PreferencesManager.getInstance().getBackupFiles().equals ("true")) {
                File fb = new File (dp.getPath() + ".bak");
                
                log.debug ("Copying \"" + f.getAbsolutePath() + "\" to \"" + fb.getAbsolutePath() + "\".");
                
                // windows doesn't let you copy a file to an existing file's name
                //
                if (fb.exists() && fb.canWrite())
                    FileUtils.deleteQuietly (fb);
    
                FileUtils.copyFile (f, fb);
            }
    
            // open the template file for reading
            //
            log.debug ("opening " + ft.getPath() + " for reading.");
            br = new BufferedReader (new FileReader (ft));
            
            // open the temp file for writing
            //
            log.debug ("opening " + f.getPath() + " for writing.");
            bw = new BufferedWriter (new FileWriter (f, false));
            
            // write out the template portion
            //
            while ((line = br.readLine()) != null) {
                if (line.equals("# empty.dp")) {
                    log.debug ("updating plan name in text to " + StringUtils.basename (f.getPath()));
                    bw.write ("# " + StringUtils.basename (f.getPath()));
                } else {
                    bw.write (line);
                }

                bw.newLine();                
            }
            
            log.debug ("number of steps to write = " + dp.getSteps().size());
            
            for (int i = 0; i < dp.getSteps().size(); i++) {
                DeploymentPlan.Step s = dp.getSteps().get (i);
                
                log.debug ("writing step " + i + " (rowid = " + s.getLineNum() + ")");
                
                if (! s.isEnabled())
                    bw.write ("#");
                
                bw.write ((s.isExpectedBehavior()) ? "PASS" : "FAIL");
                bw.write ("\t");
                bw.write ((s.isExitOnError()) ? "TRUE" : "FALSE");
                bw.write ("\t");
                bw.write (s.getBatchName());
                bw.write ("\t");
                bw.write (s.getMethod());
                bw.write ("\t");
                bw.write ("\"" + s.getParam0() + "\"");
                
                int numParams = countParams (s);
                
                if (numParams >= 2) {
                    bw.write ("\t");
                    bw.write ("\"" + s.getParam1() + "\"");
	                if (numParams >= 3) {
	                    bw.write ("\t");
	                    bw.write ("\"" + s.getParam2() + "\"");
	                    if (numParams >= 4) {
	                        bw.write ("\t");
	                        bw.write ("\"" + s.getParam3() + "\"");
	                        if (numParams >= 5) {
	                            bw.write ("\t");
	                            bw.write ("\"" + s.getParam4() + "\"");
	                            if (numParams >= 6) {
	                                bw.write ("\t");
	                                bw.write ("\"" + s.getParam5() + "\"");
	                                if (numParams >= 7) {
	                                    bw.write ("\t");
	                                    bw.write ("\"" + s.getParam6() + "\"");
	                                    if (numParams >= 8) {
	                                        bw.write ("\t");
	                                        bw.write ("\"" + s.getParam7() + "\"");
	                                        if (numParams >= 9) {
	                                            bw.write ("\t");
	                                            bw.write ("\"" + s.getParam8() + "\"");
	                                            if (numParams >= 10) {
	                                                bw.write ("\t");
	                                                bw.write ("\"" + s.getParam9() + "\"");
	                                            }
	                                        }
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
                }
                
                bw.newLine();
            }
            
            bw.flush();

        } catch (Exception e) {
            return new ResultMessage ("error", "Error: " + e.getMessage(), null);
        } finally {
            try {
                if (bw != null) bw.close();
            } catch (Exception e2) {
                return new ResultMessage ("error", "Error closing temporary file \"" + dp.getPath() + "_tmp\": " + e2.getMessage(), null);
            }

            try {
                if (br != null) br.close();
            } catch (Exception e2) {
                return new ResultMessage ("error", "Error closing template file \"" + ft.getPath() + "\": " + e2.getMessage(), null);
            }

        }
        
        return new ResultMessage ("success", "Deployment plan updated.", null);
    }
    
    /*
     * Private methods
     */
    
    private DeploymentPlan parsePlanFile (File f) throws Exception {
        DeploymentPlan result = new DeploymentPlan();
        List<DeploymentPlan.Step> steps = new ArrayList<DeploymentPlan.Step>();
        BufferedReader br = null;
        String line;
        boolean commentFound = false;
        Matcher m;
        int lineNum = 0;
        
        result.setPath (f.getPath());
        
        br = new BufferedReader (new FileReader (f));
        
        while ((line = br.readLine()) != null) {
            lineNum++;
            line = line.trim();
            commentFound = false;
            
            log.debug ("line #" + lineNum + ": " + line);

            m = stepDetectionPattern.matcher (line);
            
            if (m.matches()) {
                log.debug ("preliminary match on line number " + lineNum);
                m = stepParsePattern.matcher (line);
                
                if (m.matches()) {
                    log.debug ("full match on line number " + lineNum);
                    log.debug ("# of matched groups = " + m.groupCount());

                    DeploymentPlan.Step s = new DeploymentPlan.Step();
                    s.setLineNum (lineNum + "");
                    s.setEnabled (! line.startsWith ("#"));
                    
                    for (int i = 1; i <= 14; i++) {
                        String token = m.group (i);
                        
                        if (token == null) continue;
                        
                        log.debug ("token " + i + " = " + token);

                        // if the token contains the comment character, remove the comment from the token.
                        //
                        if (token.matches(".*#.*")) {
                            token = token.substring (0, token.indexOf ("#"));
                            commentFound = true;
                        }
                        
                        // if after removing the comment from the token and there's nothing left, skip the rest of the tokens
                        //
                        if (token.length() == 0)
                            break;

                        // set the deployment step attribute based on the token number
                        //
                        switch (i) {
                            case 1: 
                                s.setExpectedBehavior (token.replace ("\"", "").equalsIgnoreCase ("PASS"));
                                break;
                            
                            case 2: 
                                s.setExitOnError (token.replace ("\"", "").equalsIgnoreCase ("TRUE"));
                                break;
                            
                            case 3: 
                                s.setBatchName (token.replace ("\"", ""));
                                break;
                            
                            case 4: 
                                s.setMethod (token.replace ("\"", ""));
                                break;
                            
                            case 5: 
                                s.setParam0 (token.replace ("\"", ""));
                                break;
                            
                            case 6: 
                                s.setParam1 (token.replace ("\"", ""));
                                break;
                            
                            case 7: 
                                s.setParam2 (token.replace ("\"", ""));
                                break;
                            
                            case 8: 
                                s.setParam3 (token.replace ("\"", ""));
                                break;
                            
                            case 9: 
                                s.setParam4 (token.replace ("\"", ""));
                                break;
                            
                            case 10: 
                                s.setParam5 (token.replace ("\"", ""));
                                break;
                            
                            case 11: 
                                s.setParam6 (token.replace ("\"", ""));
                                break;
                            
                            case 12: 
                                s.setParam7 (token.replace ("\"", ""));
                                break;
                            
                            case 13: 
                                s.setParam8 (token.replace ("\"", ""));
                                break;
                            
                            case 14: 
                                s.setParam9 (token.replace ("\"", ""));
                                break;
                        }
                        
                        if (commentFound) break;
                            
                    }
                    
                    log.debug ("\n");
                    
                    steps.add (s);
                }
            }
        }
        
        br.close();
        
        result.setSteps (steps);
        
        return result;
    }
    
    // validates an incoming server payload based on the requested operation
    //
    private List<ResultMessage.MessageItem> validateStep (
        DeploymentPlan.Step s
    ) {
        List<ResultMessage.MessageItem> result = new ArrayList<ResultMessage.MessageItem>();
        
        if (s.getMethod() == null || s.getMethod().length() == 0) {
            result.add (new ResultMessage.MessageItem ("method", "Module method may not be empty."));
        }
        
        // probably should have a method name validator here. Need full list of method names.

        return result;
    }
    
    // counts the number of populated parameters so the serializer knows when to stop outputing parameters
    //
    private int countParams (DeploymentPlan.Step s) {
        int result = 0;

        for (int i = 1; i <= 9; i++) {
            switch (i) {
                case 1: if (s.getParam1() != null && s.getParam1().length() > 0) result = 1; break;
                case 2: if (s.getParam2() != null && s.getParam2().length() > 0) result = 2; break;
                case 3: if (s.getParam3() != null && s.getParam3().length() > 0) result = 3; break;
                case 4: if (s.getParam4() != null && s.getParam4().length() > 0) result = 4; break;
                case 5: if (s.getParam5() != null && s.getParam5().length() > 0) result = 5; break;
                case 6: if (s.getParam6() != null && s.getParam6().length() > 0) result = 6; break;
                case 7: if (s.getParam7() != null && s.getParam7().length() > 0) result = 7; break;
                case 8: if (s.getParam8() != null && s.getParam8().length() > 0) result = 8; break;
                case 9: if (s.getParam9() != null && s.getParam9().length() > 0) result = 9; break;
            }
        }
        
        return result;
    }
}
