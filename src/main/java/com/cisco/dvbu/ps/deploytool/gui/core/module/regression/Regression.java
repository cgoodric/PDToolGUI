package com.cisco.dvbu.ps.deploytool.gui.core.module.regression;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for rebind module regression records. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class Regression {


    // public constants
    //
    public static final int TYPE_TEST = 0;
    public static final int TYPE_QUERY = 1;
    public static final int TYPE_SECURITY_USER = 2;
    public static final int TYPE_SECURITY_QUERY = 3;
    public static final int TYPE_SECURITY_PLAN = 4;
    public static final String[] TYPE_LABELS = {
        "Test",
        "Query",
        "Security User",
        "Security Query",
        "Security Plan"
    };

    
    public static final int DELIMITER_NULL = 0;
    public static final int DELIMITER_COMMA_NAME = 1;
    public static final int DELIMITER_COMMA = 2;
    public static final int DELIMITER_PIPE_NAME = 3;
    public static final int DELIMITER_PIPE = 4;
    public static final int DELIMITER_TAB_NAME = 5;
    public static final int DELIMITER_TILDE_NAME = 6;
    public static final int DELIMITER_TILDE = 7;
    public static final String[] DELIMITER_LABELS = {
        "",
        "COMMA",
        ",",
        "PIPE",
        "|",
        "TAB",
        "TILDE",
        "~"
    };
    
    public static final int WS_CONTENT_TYPE_NULL = 0;
    public static final int WS_CONTENT_TYPE_SOAP11 = 1;
    public static final int WS_CONTENT_TYPE_SOAP12 = 2;
    public static final String[] WS_CONTENT_TYPE_LABELS = {
        "",
        "text/xml;charset=UTF-8",
        "application/soap+xml;charset=UTF-8"
    };
    
    public static final int QUERY_TYPE_NULL = 0;
    public static final int QUERY_TYPE_QUERY = 1;
    public static final int QUERY_TYPE_PROCEDURE = 2;
    public static final int QUERY_TYPE_WEB_SERVICE = 3;
    public static final String[] QUERY_TYPE_LABELS = {
        "",
        "QUERY",
        "PROCEDURE",
        "WEB_SERVICE"
    };
    
//    private static final Logger log = LoggerFactory.getLogger (Regression.class);

    // attributes used by the UI
    //
    String operation;
    String origId;
    
    // module specific attributes
    //
    String id;
    int type;
    
    // test type attributes
    //
    String inputFilePath;
    boolean createNewFile = false;
    NewFileParams newFileParams;
    TestRunParams testRunParams;
    CompareFiles compareFiles;
    CompareLogs compareLogs;
    
    // query type attributes
    //
    String datasource;
    String query;
    String durationDelta;
    String wsPath;
    String wsAction;
    boolean wsEncrypt = false;
    int wsContentType = WS_CONTENT_TYPE_NULL;
    
    // security user attributes
    //
    String userName;
    String encryptedPassword;
    String domain;
    
    // security query attributes (will reuse query type attributes)
    //
    int queryType = QUERY_TYPE_NULL;
    String procOutTypes;
    String resourcePath;
    String resourceType;
    
    // security plan attributes
    //
    List<SecurityPlanTest> planTests;
    
    /**
     * <p>
     * Constructor.
     * </p>
     */
    public Regression () {
        super ();
    }

    /**
     * <p>
     * Copy constructor. Used for making/serializing copies, where only the ID is updated.
     * </p>
     */
    public Regression (Regression r) {
        if (r != null) {
            this.operation = r.getOperation();
            this.origId = r.getOrigId();
            this.id = r.getId();
            this.type = r.getType();
            this.inputFilePath = r.getInputFilePath();
            this.createNewFile = r.isCreateNewFile();
            this.newFileParams = (r.getNewFileParams() != null) ? new NewFileParams (r.getNewFileParams()) : null;
            this.testRunParams = (r.getTestRunParams() != null) ? new TestRunParams (r.getTestRunParams()) : null;
            this.compareFiles = (r.getCompareFiles() != null) ? new CompareFiles (r.getCompareFiles()) : null;
            this.compareLogs = (r.getCompareLogs() != null) ? new CompareLogs (r.getCompareLogs()) : null;
            this.datasource = r.getDatasource();
            this.query = r.getQuery();
            this.durationDelta = r.getDurationDelta();
            this.wsPath = r.getWsPath();
            this.wsAction = r.getWsAction();
            this.wsEncrypt = r.isWsEncrypt();
            this.wsContentType = r.getWsContentType();
            this.userName = r.getUserName();
            this.encryptedPassword = r.getEncryptedPassword();
            this.domain = r.getDomain();
            this.queryType = r.getQueryType();
            this.procOutTypes = r.getProcOutTypes();
            this.resourcePath = r.getResourcePath();
            this.resourceType = r.getResourceType();
            this.planTests = r.getPlanTests();
        }
    }
    
    /**
     * <p>
     * Constructor. Extracts attribute values from {@link Element} object.
     * </p>
     */
    public Regression (Element rNode) {
        if (rNode.getName().equals ("regressionTest")) {
            this.type = TYPE_TEST;

            for (Element rtNode : rNode.getChildren()) {
                if (rtNode.getName().equals ("id"))
                    this.id = rtNode.getText();

                if (rtNode.getName().equals ("inputFilePath"))
                    this.inputFilePath = rtNode.getText();

                if (rtNode.getName().equals ("createNewFile"))
                    this.createNewFile = (rtNode.getText() == null) ? true : rtNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (rtNode.getName().equals ("newFileParams"))
                    this.newFileParams = new NewFileParams (rtNode);

                if (rtNode.getName().equals ("testRunParams"))
                    this.testRunParams = new TestRunParams (rtNode);

                if (rtNode.getName().equals ("compareFiles"))
                    this.compareFiles = new CompareFiles (rtNode);

                if (rtNode.getName().equals ("compareLogs"))
                    this.compareLogs = new CompareLogs (rtNode);
            }
        } else if (rNode.getName().equals ("regressionQuery")) { // parent container element is parsed in the DAO
            this.type = TYPE_QUERY;
            
            for (Element rqNode : rNode.getChildren()) {
                if (rqNode.getName().equals ("datasource"))
                    this.setDatasource (rqNode.getText());

                if (rqNode.getName().equals ("query"))
                    this.setQuery (rqNode.getText());

                if (rqNode.getName().equals ("durationDelta"))
                    this.durationDelta = rqNode.getText();

                if (rqNode.getName().equals ("wsPath"))
                    this.wsPath = rqNode.getText();

                if (rqNode.getName().equals ("wsAction"))
                    this.wsAction = rqNode.getText();

                if (rqNode.getName().equals ("wsEncrypt"))
                    this.wsEncrypt = (rqNode.getText() == null) ? true : rqNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (rqNode.getName().equals ("wsContentType")) {
                    for (int i = 0; i < WS_CONTENT_TYPE_LABELS.length; i++) {
                        if (rqNode.getText().equals (WS_CONTENT_TYPE_LABELS[i])) {
                            this.wsContentType = i;
                            break;
                        }
                    }
                }
            }
        } else if (rNode.getName().equals ("regressionSecurityUser")) { // parent container element is parsed in the DAO
            this.type = TYPE_SECURITY_USER;
            
            for (Element sucNode : rNode.getChildren()) {
                if (sucNode.getName().equals ("id"))
                    this.id = sucNode.getText();

                if (sucNode.getName().equals ("userName"))
                    this.userName = sucNode.getText();

                if (sucNode.getName().equals ("encryptedPassword"))
                    this.encryptedPassword = sucNode.getText();

                if (sucNode.getName().equals ("domain"))
                    this.domain = sucNode.getText();
            }

        } else if (rNode.getName().equals ("regressionSecurityQuery")) { // parent container element is parsed in the DAO
            this.type = TYPE_SECURITY_QUERY;
            
            for (Element sqcNode : rNode.getChildren()) {
                if (sqcNode.getName().equals ("id"))
                    this.id = sqcNode.getText();

                if (sqcNode.getName().equals ("datasource"))
                    this.datasource = sqcNode.getText();

                if (sqcNode.getName().equals ("queryType")) {
                    for (int i = 0; i < QUERY_TYPE_LABELS.length; i++) {
                        if (sqcNode.getText().equalsIgnoreCase (QUERY_TYPE_LABELS[i])) {
                            this.queryType = i;
                            break;
                        }
                    }
                }

                if (sqcNode.getName().equals ("query"))
                    this.query = sqcNode.getText();

                if (sqcNode.getName().equals ("procOutTypes"))
                    this.procOutTypes = sqcNode.getText();

                if (sqcNode.getName().equals ("wsPath"))
                    this.wsPath = sqcNode.getText();

                if (sqcNode.getName().equals ("wsAction"))
                    this.wsAction = sqcNode.getText();

                if (sqcNode.getName().equals ("wsEncrypt"))
                    this.wsEncrypt = (sqcNode.getText() == null) ? false : sqcNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (sqcNode.getName().equals ("wsContentType")) {
                    for (int i = 0; i < WS_CONTENT_TYPE_LABELS.length; i++) {
                        if (sqcNode.getText().equalsIgnoreCase (WS_CONTENT_TYPE_LABELS[i])) {
                            this.wsContentType = i;
                            break;
                        }
                    }
                }

                if (sqcNode.getName().equals ("resourcePath"))
                    this.resourcePath = sqcNode.getText();

                if (sqcNode.getName().equals ("resourceType"))
                    this.resourceType = sqcNode.getText();

            }
        } else if (rNode.getName().equals ("regressionSecurityPlan")) { // parent container element is parsed in the DAO
            this.type = TYPE_SECURITY_PLAN;
            
            for (Element spcNode : rNode.getChildren()) {
                if (spcNode.getName().equals ("id"))
                    this.id = spcNode.getText();

                if (spcNode.getName().equals ("regressionSecurityPlanTest")) {
                    if (this.planTests == null)
                        this.planTests = new ArrayList<SecurityPlanTest>();
                    
                    this.planTests.add (new SecurityPlanTest (spcNode));
                }
            }
        }
    }

    /**
     * <p>
     * Returns the object as a JDom Element.
     * </p>
     * 
     * @param  name   The name of the element to use.
     * @param  indent The number of tabs (spaces) to indent the child elements.
     * @return        The value.
     */
    public Element toElement(
        String name,
        int indent
    ) {
        String indentStr = StringUtils.getIndent (indent);

        Element result = new Element (name);
        
        switch (this.type) {
            case TYPE_TEST:
                
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("id").setText (this.id));

                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("inputFilePath").setText (this.inputFilePath));

                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("createNewFile").setText ("" + this.createNewFile));

                if (this.newFileParams != null) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (this.newFileParams.toElement ("newFileParams", indent + 1).addContent ("\n" + indentStr));
                }

                if (this.testRunParams != null) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (this.testRunParams.toElement ("testRunParams", indent + 1).addContent ("\n" + indentStr));
                }

                if (this.compareFiles != null) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (this.compareFiles.toElement ("compareFiles", indent + 1).addContent ("\n" + indentStr));
                }

                if (this.compareLogs != null) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (this.compareLogs.toElement ("compareLogs", indent + 1).addContent ("\n" + indentStr));
                }

                break;
            
            case TYPE_QUERY:
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("datasource").setText (this.datasource));

                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("query").setText (this.query));

                if (this.durationDelta != null && this.durationDelta.length() > 0) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("durationDelta").setText (this.durationDelta));
                }

                if (this.wsPath != null && this.wsPath.length() > 0) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("wsPath").setText (this.wsPath));
                }

                if (this.wsAction != null && this.wsAction.length() > 0) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("wsAction").setText (this.wsAction));
                }

                // no need to set this unless the wsPath attribute has a value.
                //
                if (this.wsPath != null && this.wsPath.length() > 0) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("wsEncrypt").setText ("" + this.wsEncrypt));
                }

                if (this.wsContentType != WS_CONTENT_TYPE_NULL) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("wsContentType").setText (WS_CONTENT_TYPE_LABELS[this.wsContentType]));
                }

                break;
            
            case TYPE_SECURITY_USER:
                
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("id").setText (this.id));
    
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("userName").setText (this.userName));
    
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("encryptedPassword").setText (this.encryptedPassword));
    
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("domain").setText (this.domain));
                
                break;
            
            case TYPE_SECURITY_QUERY:
                
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("id").setText (this.id));
    
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("datasource").setText (this.datasource));
    
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("queryType").setText (QUERY_TYPE_LABELS[this.queryType]));
    
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("query").setText (this.query));
    
                if (this.procOutTypes != null) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("procOutTypes").setText (this.procOutTypes));
                }
    
                if (this.wsPath != null) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("wsPath").setText (this.wsPath));
                }
    
                if (this.wsAction != null) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("wsAction").setText (this.wsAction));
                }
    
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("wsEncrypt").setText ("" + this.wsEncrypt));
    
                if (this.wsContentType != WS_CONTENT_TYPE_NULL) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("wsContentType").setText (WS_CONTENT_TYPE_LABELS[this.wsContentType]));
                }
    
                if (this.resourcePath != null) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("resourcePath").setText (this.resourcePath));
                }
    
                if (this.resourceType != null) {
                    result.addContent ("\n" + indentStr);
                    result.addContent (new Element ("resourceType").setText (this.resourceType));
                }
                
                break;
            
            case TYPE_SECURITY_PLAN:
                
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("id").setText (this.id));
                
                if (this.planTests != null) {
                    for (SecurityPlanTest pt : this.planTests) {
                        result.addContent ("\n" + indentStr);
                        result.addContent (pt.toElement ("regressionSecurityPlanTest", indent + 1).addContent ("\n" + indentStr));
                    }
                }
                
                break;
        }

        return result;
    }

    /**
     * <p>
     * Sets the <code>operation</code> field.
     * </p>
     * 
     * @param  operation  The operation to be performed on the archive.
     */
    public void setOperation (String operation) {
        this.operation = operation;
    }

    /**
     * <p>
     * Returns the value of the <code>operation</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getOperation() {
        return operation;
    }

    /**
     * <p>
     * Sets the <code>origId</code> field.
     * </p>
     * 
     * @param  origId  For edit/copy operations, this contains the original ID (for 
     *                 cases where the user wants to change the ID field.)
     */
    public void setOrigId (String origId) {
        this.origId = origId;
    }

    /**
     * <p>
     * Returns the value of the <code>origId</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getOrigId() {
        return origId;
    }

    /**
     * <p>
     * Query and security elements were not given "id" elements so this sets the <code>id</code> field 
     * from the hash codes of various attributes in an attempt to provide uniqueness.
     * </p>
     */
    public void setId() {
        if (this.type == TYPE_QUERY && this.datasource != null && this.query != null) {
            this.id = "" + Math.abs (this.datasource.hashCode()) + Math.abs (this.query.hashCode());
        }
    }

    /**
     * <p>
     * Sets the <code>id</code> field.
     * </p>
     * 
     * @param  id  The ID of the data source record.
     */
    public void setId (String id) {
        this.id = id;
    }

    /**
     * <p>
     * Returns the value of the <code>id</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getId() {
        return id;
    }

    /**
     * <p>
     * Sets the <code>type</code> field.
     * </p>
     * 
     * @param  type  Indicates whether the regression entry is a test, query, or security test. See the TEST_* constants above.
     */
    public void setType (int type) {
        this.type = type;
    }

    /**
     * <p>
     * Returns the value of the <code>type</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getType() {
        return type;
    }

    /**
     * <p>
     * Sets the <code>inputFilePath</code> field.
     * </p>
     * 
     * @param  inputFilePath  The full path to the test file including file name.
     */
    public void setInputFilePath (String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    /**
     * <p>
     * Returns the value of the <code>inputFilePath</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getInputFilePath() {
        return inputFilePath;
    }

    /**
     * <p>
     * Sets the <code>createNewFile</code> field.
     * </p>
     * 
     * @param  createNewFile  Indicates whether PDTool should create the file at <code>inputFilePath</code>.
     */
    public void setCreateNewFile (boolean createNewFile) {
        this.createNewFile = createNewFile;
    }

    /**
     * <p>
     * Returns the value of the <code>createNewFile</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isCreateNewFile() {
        return createNewFile;
    }

    /**
     * <p>
     * Sets the <code>newFileParams</code> field.
     * </p>
     * 
     * @param  newFileParams  Parameter settings for testing a regression test file.
     */
    public void setNewFileParams (Regression.NewFileParams newFileParams) {
        this.newFileParams = newFileParams;
    }

    /**
     * <p>
     * Returns the value of the <code>newFileParams</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public Regression.NewFileParams getNewFileParams() {
        return newFileParams;
    }

    /**
     * <p>
     * Sets the <code>testRunParams</code> field.
     * </p>
     * 
     * @param  testRunParams  Parameter settings for testing a regression test file.
     */
    public void setTestRunParams (Regression.TestRunParams testRunParams) {
        this.testRunParams = testRunParams;
    }

    /**
     * <p>
     * Returns the value of the <code>testRunParams</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public Regression.TestRunParams getTestRunParams() {
        return testRunParams;
    }

    /**
     * <p>
     * Sets the <code>compareFiles</code> field.
     * </p>
     * 
     * @param  compareFiles  Parameter settings for comparing regression execution result files.
     */
    public void setCompareFiles (Regression.CompareFiles compareFiles) {
        this.compareFiles = compareFiles;
    }

    /**
     * <p>
     * Returns the value of the <code>compareFiles</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public Regression.CompareFiles getCompareFiles() {
        return compareFiles;
    }

    /**
     * <p>
     * Sets the <code>compareLogs</code> field.
     * </p>
     * 
     * @param  compareLogs  Parameter settings for comparing regression execution log files.
     */
    public void setCompareLogs (Regression.CompareLogs compareLogs) {
        this.compareLogs = compareLogs;
    }

    /**
     * <p>
     * Returns the value of the <code>compareLogs</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public Regression.CompareLogs getCompareLogs() {
        return compareLogs;
    }

    /**
     * <p>
     * Sets the <code>datasource</code> field.
     * </p>
     * 
     * @param  datasource  The datasource identifies which CIS published data source the query belongs to.
     */
    public void setDatasource (String datasource) {
        this.datasource = datasource;
        
        this.setId();
    }

    /**
     * <p>
     * Returns the value of the <code>datasource</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDatasource() {
        return datasource;
    }

    /**
     * <p>
     * Sets the <code>query</code> field.
     * </p>
     * 
     * @param  query  The query contains the SQL SELECT statement or the web service input.  
     */
    public void setQuery (String query) {
        this.query = query;
        
        this.setId();
    }

    /**
     * <p>
     * Returns the value of the <code>query</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getQuery() {
        return query;
    }

    /**
     * <p>
     * Sets the <code>durationDelta</code> field.
     * </p>
     * 
     * @param  durationDelta  Duration delta for this query. Format must be as follows within brackets: [000 00:00:00.0000] 
     */
    public void setDurationDelta (String durationDelta) {
        this.durationDelta = durationDelta;
    }

    /**
     * <p>
     * Returns the value of the <code>durationDelta</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDurationDelta() {
        return durationDelta;
    }

    /**
     * <p>
     * Sets the <code>wsPath</code> field.
     * </p>
     * 
     * @param  wsPath  The web service path is really the endpoint URL or the port path.
     */
    public void setWsPath (String wsPath) {
        this.wsPath = wsPath;
    }

    /**
     * <p>
     * Returns the value of the <code>wsPath</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getWsPath() {
        return wsPath;
    }

    /**
     * <p>
     * Sets the <code>wsAction</code> field.
     * </p>
     * 
     * @param  wsAction  The web service action is the operation to be executed.
     */
    public void setWsAction (String wsAction) {
        this.wsAction = wsAction;
    }

    /**
     * <p>
     * Returns the value of the <code>wsAction</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getWsAction() {
        return wsAction;
    }

    /**
     * <p>
     * Sets the <code>wsEncrypt</code> field.
     * </p>
     * 
     * @param  wsEncrypt  The web service encrypt determines if http (false) or https (true) should be used.
     */
    public void setWsEncrypt (boolean wsEncrypt) {
        this.wsEncrypt = wsEncrypt;
    }

    /**
     * <p>
     * Returns the value of the <code>wsEncrypt</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isWsEncrypt() {
        return wsEncrypt;
    }

    /**
     * <p>
     * Sets the <code>wsContentType</code> field.
     * </p>
     * 
     * @param  wsContentType  The content type for a web service. See the CONTENT_TYPE_* constants.
     */
    public void setWsContentType (int wsContentType) {
        this.wsContentType = wsContentType;
    }

    /**
     * <p>
     * Returns the value of the <code>wsContentType</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getWsContentType() {
        return wsContentType;
    }

    /**
     * <p>
     * Sets the <code>userName</code> field.
     * </p>
     * 
     * @param  userName  The security user's name.
     */
    public void setUserName (String userName) {
        this.userName = userName;
    }

    /**
     * <p>
     * Returns the value of the <code>userName</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * <p>
     * Sets the <code>encryptedPassword</code> field.
     * </p>
     * 
     * @param  encryptedPassword  The security user's encrypted password.
     */
    public void setEncryptedPassword (String encryptedPassword) {
        this.encryptedPassword = StringUtils.encryptPassword (encryptedPassword);
    }

    /**
     * <p>
     * Returns the value of the <code>encryptedPassword</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    /**
     * <p>
     * Sets the <code>domain</code> field.
     * </p>
     * 
     * @param  domain  The security user's CIS user domain.
     */
    public void setDomain (String domain) {
        this.domain = domain;
    }

    /**
     * <p>
     * Returns the value of the <code>domain</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * <p>
     * Sets the <code>queryType</code> field.
     * </p>
     * 
     * @param  queryType  The type of the query. See QUERY_TYPE_* constants above.
     */
    public void setQueryType (int queryType) {
        this.queryType = queryType;
    }

    /**
     * <p>
     * Returns the value of the <code>encryptedPassword</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getQueryType() {
        return queryType;
    }

    /**
     * <p>
     * Sets the <code>procOutTypes</code> field.
     * </p>
     * 
     * @param  procOutTypes  The output types of the procedure to be tested.
     */
    public void setProcOutTypes (String procOutTypes) {
        this.procOutTypes = procOutTypes;
    }

    /**
     * <p>
     * Returns the value of the <code>procOutTypes</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getProcOutTypes() {
        return procOutTypes;
    }

    /**
     * <p>
     * Sets the <code>resourcePath</code> field.
     * </p>
     * 
     * @param  resourcePath  A temporary field used for creating security test plans.
     */
    public void setResourcePath (String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * <p>
     * Returns the value of the <code>resourcePath</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * <p>
     * Sets the <code>resourceType</code> field.
     * </p>
     * 
     * @param  resourceType  A temporary field used for creating security test plans.
     */
    public void setResourceType (String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * <p>
     * Returns the value of the <code>resourceType</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * <p>
     * Sets the <code>planTests</code> field.
     * </p>
     * 
     * @param  planTests  The security user's name.
     */
    public void setPlanTests (List<SecurityPlanTest> planTests) {
        this.planTests = planTests;
    }

    /**
     * <p>
     * Returns the value of the <code>planTests</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<SecurityPlanTest> getPlanTests() {
        return planTests;
    }



    public static class NewFileParams {
        public static final int CREATE_SOAP_TYPE_ALL = 0;
        public static final int CREATE_SOAP_TYPE_SOAP11 = 1;
        public static final int CREATE_SOAP_TYPE_SOAP12 = 2;
        public static final String[] CREATE_SOAP_TYPE_LABELS = {
            "all",
            "soap11",
            "soap12"
        };

        public static final int GEN_MODE_NOEXEC = 0;
        public static final int GEN_MODE_OVERWRITE = 1;
        public static final int GEN_MODE_APPEND = 2;
        public static final String[] GEN_MODE_LABELS = {
            "NOEXEC",
            "OVERWRITE",
            "APPEND"
        };

        public static final int PLAN_MODE_TYPE_SINGLEPLAN = 0;
        public static final int PLAN_MODE_TYPE_MULTIPLAN = 1;
        public static final String[] PLAN_MODE_TYPE_LABELS = {
            "SINGLEPLAN",
            "MULTIPLAN"
        };

        boolean createQueries = false;
        boolean createProcedures = false;
        boolean createWS = false;
        int createSoapType;
        boolean useDefaultViewQuery = false;
        boolean useDefaultProcQuery = false;
        boolean useDefaultWSQuery = false;
        String publishedViewQry;
        String publishedProcQry;
        boolean useAllDatasources = true;
        List<String> datasources;
        List<String> resources;
        DefaultProcParamValues defaultProcParamValues;
        
        // security generation options
        //
        String pathToTargetRegressionXML;
        String encryptedPassword;
        String userFilter;
        String domainFilter;
        int userMode = GEN_MODE_NOEXEC;
        int queryMode = GEN_MODE_NOEXEC;
        int planMode = GEN_MODE_NOEXEC;
        int planModeType = PLAN_MODE_TYPE_SINGLEPLAN;
        String planIdPrefix;
        boolean planGenerateExpectedOutcome = false;
        boolean flattenSecurityUsersXML = true;
        boolean flattenSecurityQueryQueriesXML = true;
        boolean flattenSecurityQueryProceduresXML = true;
        boolean flattenSecurityQueryWebServicesXML = true;
        boolean flattenSecurityPlansXML = true;

        /**
         * <p>
         * Constructor.
         * </p>
         */
        public NewFileParams() {}

        /**
         * <p>
         * Copy Constructor.
         * </p>
         */
        public NewFileParams (NewFileParams nfp) {
            if (nfp != null) {
                this.createQueries = nfp.isCreateQueries();
                this.createProcedures = nfp.isCreateProcedures();
                this.createWS = nfp.isCreateWS();
                this.createSoapType = nfp.getCreateSoapType();
                this.useDefaultViewQuery = nfp.isUseDefaultViewQuery();
                this.useDefaultProcQuery = nfp.isUseDefaultProcQuery();
                this.useDefaultWSQuery = nfp.isUseDefaultWSQuery();
                this.publishedViewQry = nfp.getPublishedViewQry();
                this.publishedProcQry = nfp.getPublishedProcQry();
                this.useAllDatasources = nfp.isUseAllDatasources();
                this.datasources = (nfp.getDatasources() != null) ? new ArrayList<String> (nfp.getDatasources()) : null; // Do not update!!
                this.resources = (nfp.getResources() != null) ? new ArrayList<String> (nfp.getResources()) : null; // Do not update!!
                this.defaultProcParamValues = (nfp.getDefaultProcParamValues() != null) ? new DefaultProcParamValues (nfp.getDefaultProcParamValues()) : null;
                this.pathToTargetRegressionXML = nfp.getPathToTargetRegressionXML();
                this.encryptedPassword = nfp.getEncryptedPassword();
                this.userFilter = nfp.getUserFilter();
                this.domainFilter = nfp.getDomainFilter();
                this.userMode = nfp.getUserMode();
                this.queryMode = nfp.getQueryMode();
                this.planMode = nfp.getPlanMode();
                this.planModeType = nfp.getPlanModeType();
                this.planIdPrefix = nfp.getPlanIdPrefix();
                this.planGenerateExpectedOutcome = nfp.isPlanGenerateExpectedOutcome();
                this.flattenSecurityUsersXML = nfp.isFlattenSecurityUsersXML();
                this.flattenSecurityQueryQueriesXML = nfp.isFlattenSecurityQueryQueriesXML();
                this.flattenSecurityQueryProceduresXML = nfp.isFlattenSecurityQueryProceduresXML();
                this.flattenSecurityQueryWebServicesXML = nfp.isFlattenSecurityQueryWebServicesXML();
                this.flattenSecurityPlansXML = nfp.isFlattenSecurityPlansXML();
            }
        }

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public NewFileParams (Element nNode) {
            for (Element ncNode : nNode.getChildren()) {
                if (ncNode.getName().equals ("createQueries"))
                    this.createQueries = (ncNode.getText() == null) ? true : ncNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ncNode.getName().equals ("createProcedures"))
                    this.createProcedures = (ncNode.getText() == null) ? true : ncNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ncNode.getName().equals ("createWS"))
                    this.createWS = (ncNode.getText() == null) ? true : ncNode.getText().matches ("(?i)^(yes|true|on|1)$");
                
                if (ncNode.getName().equals ("createSoapType")) {
                    for (int i = 0; i < CREATE_SOAP_TYPE_LABELS.length; i++) {
                        if (ncNode.getText().equalsIgnoreCase (CREATE_SOAP_TYPE_LABELS[i])) {
                            this.createSoapType = i;
                            break;
                        }
                    }
                }

                if (ncNode.getName().equals ("useDefaultViewQuery"))
                    this.useDefaultViewQuery = (ncNode.getText() == null) ? true : ncNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ncNode.getName().equals ("useDefaultProcQuery"))
                    this.useDefaultProcQuery = (ncNode.getText() == null) ? true : ncNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ncNode.getName().equals ("useDefaultWSQuery"))
                    this.useDefaultWSQuery = (ncNode.getText() == null) ? true : ncNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ncNode.getName().equals ("publishedViewQry"))
                    this.publishedViewQry = ncNode.getText();

                if (ncNode.getName().equals ("publishedProcQry"))
                    this.publishedProcQry = ncNode.getText();
                
                if (ncNode.getName().equals ("useAllDatasources"))
                    this.useDefaultViewQuery = (ncNode.getText() == null) ? true : ncNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ncNode.getName().equals ("datasources")) {
                    for (Element dNode : ncNode.getChildren()) {
                        if (dNode.getName().equals ("dsName") && dNode.getText() != null && dNode.getText().length() > 0) { // ignore empty strings
                            if (datasources == null)
                                datasources = new ArrayList<String>();
                            
                            datasources.add (dNode.getText());
                        }
                    }
                }

                if (ncNode.getName().equals ("resources")) {
                    for (Element rNode : ncNode.getChildren()) {
                        if (rNode.getName().equals ("resource") && rNode.getText() != null && rNode.getText().length() > 0) { // ignore empty strings
                            if (resources == null)
                                resources = new ArrayList<String>();
                            
                            resources.add (rNode.getText());
                        }
                    }
                }

                if (ncNode.getName().equals ("securityGenerationOptions")) {
                    for (Element sgoNode : ncNode.getChildren()) {
                        if (sgoNode.getName().equals ("pathToTargetRegressionXML"))
                            this.pathToTargetRegressionXML = sgoNode.getText();

                        if (sgoNode.getName().equals ("encryptedPassword"))
                            this.encryptedPassword = sgoNode.getText();

                        if (sgoNode.getName().equals ("userFilter"))
                            this.userFilter = sgoNode.getText();

                        if (sgoNode.getName().equals ("domainFilter"))
                            this.domainFilter = sgoNode.getText();

                        if (sgoNode.getName().equals ("userMode")) {
                            for (int i = 0; i < GEN_MODE_LABELS.length; i++) {
                                if (sgoNode.getText().equalsIgnoreCase (GEN_MODE_LABELS[i])) {
                                    this.userMode = i;
                                    break;
                                }
                            }
                        }

                        if (sgoNode.getName().equals ("queryMode")) {
                            for (int i = 0; i < GEN_MODE_LABELS.length; i++) {
                                if (sgoNode.getText().equalsIgnoreCase (GEN_MODE_LABELS[i])) {
                                    this.queryMode = i;
                                    break;
                                }
                            }
                        }

                        if (sgoNode.getName().equals ("planMode")) {
                            for (int i = 0; i < GEN_MODE_LABELS.length; i++) {
                                if (sgoNode.getText().equalsIgnoreCase (GEN_MODE_LABELS[i])) {
                                    this.planMode = i;
                                    break;
                                }
                            }
                        }

                        if (sgoNode.getName().equals ("planModeType")) {
                            for (int i = 0; i < PLAN_MODE_TYPE_LABELS.length; i++) {
                                if (sgoNode.getText().equalsIgnoreCase (PLAN_MODE_TYPE_LABELS[i])) {
                                    this.planModeType = i;
                                    break;
                                }
                            }
                        }

                        if (sgoNode.getName().equals ("planIdPrefix"))
                            this.planIdPrefix = sgoNode.getText();

                        if (sgoNode.getName().equals ("planGenerateExpectedOutcome"))
                            this.planGenerateExpectedOutcome = (sgoNode.getText() == null) ? true : sgoNode.getText().matches ("(?i)^(yes|true|on|1)$");

                        if (sgoNode.getName().equals ("flattenSecurityUsersXML"))
                            this.flattenSecurityUsersXML = (sgoNode.getText() == null) ? true : sgoNode.getText().matches ("(?i)^(yes|true|on|1)$");

                        if (sgoNode.getName().equals ("flattenSecurityQueryQueriesXML"))
                            this.flattenSecurityQueryQueriesXML = (sgoNode.getText() == null) ? true : sgoNode.getText().matches ("(?i)^(yes|true|on|1)$");

                        if (sgoNode.getName().equals ("flattenSecurityQueryProceduresXML"))
                            this.flattenSecurityQueryProceduresXML = (sgoNode.getText() == null) ? true : sgoNode.getText().matches ("(?i)^(yes|true|on|1)$");

                        if (sgoNode.getName().equals ("flattenSecurityQueryWebServicesXML"))
                            this.flattenSecurityQueryWebServicesXML = (sgoNode.getText() == null) ? true : sgoNode.getText().matches ("(?i)^(yes|true|on|1)$");

                        if (sgoNode.getName().equals ("flattenSecurityPlansXML"))
                            this.flattenSecurityPlansXML = (sgoNode.getText() == null) ? true : sgoNode.getText().matches ("(?i)^(yes|true|on|1)$");
                    }
                }

                if (ncNode.getName().equals ("defaultProcParamValues"))
                    this.defaultProcParamValues = new DefaultProcParamValues (ncNode);
            }
        }

        /**
         * <p>
         * Returns the object as a JDom Element.
         * </p>
         * 
         * @param  name   The name of the element to use.
         * @param  indent The number of tabs (spaces) to indent the child elements.
         * @return        The value.
         */
        public Element toElement(
            String name,
            int indent
        ) {
            String indentStr = StringUtils.getIndent (indent);
            String indentStr2 = StringUtils.getIndent (indent + 1);
    
            Element result = new Element (name);

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("createQueries").setText ("" + this.createQueries));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("createProcedures").setText ("" + this.createProcedures));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("createWS").setText ("" + this.createWS));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("createSoapType").setText (CREATE_SOAP_TYPE_LABELS[this.createSoapType]));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("useDefaultViewQuery").setText ("" + this.useDefaultViewQuery));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("useDefaultProcQuery").setText ("" + this.useDefaultProcQuery));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("useDefaultWSQuery").setText ("" + this.useDefaultWSQuery));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("publishedViewQry").setText (this.publishedViewQry));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("publishedProcQry").setText (this.publishedProcQry));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("useAllDatasources").setText ("" + this.useAllDatasources));
            
            if (this.datasources != null && this.datasources.size() > 0) {
                Element dsNode = new Element ("datasources");
                
                for (String ds : datasources) {
                    dsNode.addContent ("\n" + indentStr2);
                    dsNode.addContent (new Element ("dsName").setText (ds));
                }
                
                    dsNode.addContent ("\n" + indentStr); // indent closing tag

                result.addContent ("\n" + indentStr);
                result.addContent (dsNode);
            }
            
            if (this.resources != null && this.resources.size() > 0) {
                Element rNode = new Element ("resources");
                
                for (String r : resources) {
                    rNode.addContent ("\n" + indentStr2);
                    rNode.addContent (new Element ("resource").setText (r));
                }
                
                    rNode.addContent ("\n" + indentStr); // indent closing tag

                result.addContent ("\n" + indentStr);
                result.addContent (rNode);
            }
            
            // if the path to the target regression module XML is not null then create the security generation options element.
            // (the path is a required element of the options element.)
            //
            if (this.pathToTargetRegressionXML != null) {
                Element sgoNode = new Element ("securityGenerationOptions");
                
                sgoNode.addContent ("\n" + indentStr2);
                sgoNode.addContent (new Element ("pathToTargetRegressionXML").setText (this.getPathToTargetRegressionXML()));
                
                if (this.getEncryptedPassword() != null) {
                    sgoNode.addContent ("\n" + indentStr2);
                    sgoNode.addContent (new Element ("encryptedPassword").setText (this.getEncryptedPassword()));
                }
                
                if (this.getUserFilter() != null) {
                    sgoNode.addContent ("\n" + indentStr2);
                    sgoNode.addContent (new Element ("userFilter").setText (this.getUserFilter()));
                }
                
                if (this.getDomainFilter() != null) {
                    sgoNode.addContent ("\n" + indentStr2);
                    sgoNode.addContent (new Element ("domainFilter").setText (this.getDomainFilter()));
                }
                
                sgoNode.addContent ("\n" + indentStr2);
                sgoNode.addContent (new Element ("userMode").setText (GEN_MODE_LABELS[this.getUserMode()]));
                
                sgoNode.addContent ("\n" + indentStr2);
                sgoNode.addContent (new Element ("queryMode").setText (GEN_MODE_LABELS[this.getQueryMode()]));
                
                sgoNode.addContent ("\n" + indentStr2);
                sgoNode.addContent (new Element ("planMode").setText (GEN_MODE_LABELS[this.getPlanMode()]));
                
                sgoNode.addContent ("\n" + indentStr2);
                sgoNode.addContent (new Element ("planModeType").setText (PLAN_MODE_TYPE_LABELS[this.getPlanModeType()]));
                
                if (this.getPlanIdPrefix() != null) {
                    sgoNode.addContent ("\n" + indentStr2);
                    sgoNode.addContent (new Element ("planIdPrefix").setText (this.getPlanIdPrefix()));
                }
                
                sgoNode.addContent ("\n" + indentStr2);
                sgoNode.addContent (new Element ("planGenerateExpectedOutcome").setText ("" + this.isPlanGenerateExpectedOutcome()));
                
                sgoNode.addContent ("\n" + indentStr2);
                sgoNode.addContent (new Element ("flattenSecurityUsersXML").setText ("" + this.isFlattenSecurityUsersXML()));
                
                sgoNode.addContent ("\n" + indentStr2);
                sgoNode.addContent (new Element ("flattenSecurityQueryQueriesXML").setText ("" + this.isFlattenSecurityQueryQueriesXML()));
                
                sgoNode.addContent ("\n" + indentStr2);
                sgoNode.addContent (new Element ("flattenSecurityQueryProceduresXML").setText ("" + this.isFlattenSecurityQueryProceduresXML()));
                
                sgoNode.addContent ("\n" + indentStr2);
                sgoNode.addContent (new Element ("flattenSecurityQueryWebServicesXML").setText ("" + this.isFlattenSecurityQueryWebServicesXML()));
                
                sgoNode.addContent ("\n" + indentStr2);
                sgoNode.addContent (new Element ("flattenSecurityPlansXML").setText ("" + this.isFlattenSecurityPlansXML()));

                sgoNode.addContent ("\n" + indentStr); // indent closing tag

                result.addContent ("\n" + indentStr);
                result.addContent (sgoNode);
            }
            
            if (this.defaultProcParamValues != null) {
                result.addContent ("\n" + indentStr);
                result.addContent (this.defaultProcParamValues.toElement ("defaultProcParamValues", indent + 1).addContent ("\n" + indentStr));
            }

            return result;
        }

        /**
         * <p>
         * Sets the <code>createQueries</code> field.
         * </p>
         * 
         * @param  createQueries  Defines whether query statement should be created in the published
         *                        test input file for views of the configured Composite data source.
         */
        public void setCreateQueries (boolean createQueries) {
            this.createQueries = createQueries;
        }

        /**
         * <p>
         * Returns the value of the <code>createQueries</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isCreateQueries () {
            return createQueries;
        }

        /**
         * <p>
         * Sets the <code>createProcedures</code> field.
         * </p>
         * 
         * @param  createProcedures  Defines whether procedure invocations should be created in the published 
         *                           test input file for procedures of the configured Composite data source.
         */
        public void setCreateProcedures (boolean createProcedures) {
            this.createProcedures = createProcedures;
        }

        /**
         * <p>
         * Returns the value of the <code>createProcedures</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isCreateProcedures () {
            return createProcedures;
        }

        /**
         * <p>
         * Sets the <code>createWS</code> field.
         * </p>
         * 
         * @param  createWS  Defines whether web service invocation should be created in the published test input 
         *                   file for web services of the configured Composite data source.
         */
        public void setCreateWS (boolean createWS) {
            this.createWS = createWS;
        }

        /**
         * <p>
         * Returns the value of the <code>createWS</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isCreateWS () {
            return createWS;
        }

        /**
         * <p>
         * Sets the <code>createSoapType</code> field.
         * </p>
         * 
         * @param  createSoapType  The soap type specifies whether to generate web services using soap11 (default), soap12 or all.
         */
        public void setCreateSoapType (int createSoapType) {
            this.createSoapType = createSoapType;
        }

        /**
         * <p>
         * Returns the value of the <code>createSoapType</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getCreateSoapType () {
            return createSoapType;
        }

        /**
         * <p>
         * Sets the <code>useDefaultViewQuery</code> field.
         * </p>
         * 
         * @param  useDefaultViewQuery  Defines when to use the default query when generating input file instead of 
         *                              using the list of pre-defined regression queries.
         */
        public void setUseDefaultViewQuery (boolean useDefaultViewQuery) {
            this.useDefaultViewQuery = useDefaultViewQuery;
        }

        /**
         * <p>
         * Returns the value of the <code>useDefaultViewQuery</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isUseDefaultViewQuery () {
            return useDefaultViewQuery;
        }

        /**
         * <p>
         * Sets the <code>useDefaultProcQuery</code> field.
         * </p>
         * 
         * @param  useDefaultProcQuery  Defines when to use the default procedure when generating input file instead 
         *                              of using the list of pre-defined regression queries.
         */
        public void setUseDefaultProcQuery (boolean useDefaultProcQuery) {
            this.useDefaultProcQuery = useDefaultProcQuery;
        }

        /**
         * <p>
         * Returns the value of the <code>useDefaultProcQuery</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isUseDefaultProcQuery () {
            return useDefaultProcQuery;
        }

        /**
         * <p>
         * Sets the <code>useDefaultWSQuery</code> field.
         * </p>
         * 
         * @param  useDefaultWSQuery  Defines when to use the default web service when generating input file instead 
         *                            of using the list of pre-defined regression queries.
         */
        public void setUseDefaultWSQuery (boolean useDefaultWSQuery) {
            this.useDefaultWSQuery = useDefaultWSQuery;
        }

        /**
         * <p>
         * Returns the value of the <code>useDefaultWSQuery</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isUseDefaultWSQuery () {
            return useDefaultWSQuery;
        }

        /**
         * <p>
         * Sets the <code>publishedViewQry</code> field.
         * </p>
         * 
         * @param  publishedViewQry  Template query that should be used when generating the queries for the 
         *                           published test input file.
         */
        public void setPublishedViewQry (String publishedViewQry) {
            this.publishedViewQry = publishedViewQry;
        }

        /**
         * <p>
         * Returns the value of the <code>publishedViewQry</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getPublishedViewQry () {
            return publishedViewQry;
        }

        /**
         * <p>
         * Sets the <code>publishedProcQry</code> field.
         * </p>
         * 
         * @param  publishedProcQry  Template query that should be used when generating the queries for the 
         *                           published test input file.
         */
        public void setPublishedProcQry (String publishedProcQry) {
            this.publishedProcQry = publishedProcQry;
        }

        /**
         * <p>
         * Returns the value of the <code>publishedProcQry</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getPublishedProcQry () {
            return publishedProcQry;
        }

        /**
         * <p>
         * Sets the <code>useAllDatasources</code> field.
         * </p>
         * 
         * @param  useAllDatasources  Defines whether to use all the Composite data sources published by Composite 
         *                            or use the list provided in the input file.
         */
        public void setUseAllDatasources (boolean useAllDatasources) {
            this.useAllDatasources = useAllDatasources;
        }

        /**
         * <p>
         * Returns the value of the <code>useAllDatasources</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isUseAllDatasources () {
            return useAllDatasources;
        }

        /**
         * <p>
         * Sets the <code>datasources</code> field.
         * </p>
         * 
         * @param  datasources  List of data sources to interrogate when generating the input file.
         */
        public void setDatasources (List<String> datasources) {
            this.datasources = datasources;
        }

        /**
         * <p>
         * Returns the value of the <code>datasources</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public List<String> getDatasources () {
            return datasources;
        }

        /**
         * <p>
         * Sets the <code>resources</code> field.
         * </p>
         * 
         * @param  resources  List of resources to use for testing.
         */
        public void setResources (List<String> resources) {
            this.resources = resources;
        }

        /**
         * <p>
         * Returns the value of the <code>resources</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public List<String> getResources () {
            return resources;
        }

        /**
         * <p>
         * Sets the <code>defaultProcParamValues</code> field.
         * </p>
         * 
         * @param  defaultProcParamValues  The listing of variable types provides the user with the ability to 
         *                                 set default parameter values for procedures.
         */
        public void setDefaultProcParamValues (Regression.NewFileParams.DefaultProcParamValues defaultProcParamValues) {
            this.defaultProcParamValues = defaultProcParamValues;
        }

        /**
         * <p>
         * Returns the value of the <code>defaultProcParamValues</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public Regression.NewFileParams.DefaultProcParamValues getDefaultProcParamValues () {
            return defaultProcParamValues;
        }

        /**
         * <p>
         * Sets the <code>pathToTargetRegressionXML</code> field.
         * </p>
         * 
         * @param  pathToTargetRegressionXML  The path to the target regression module XML for generating security test entries.
         */
        public void setPathToTargetRegressionXML (String pathToTargetRegressionXML) {
            this.pathToTargetRegressionXML = pathToTargetRegressionXML;
        }

        /**
         * <p>
         * Returns the value of the <code>pathToTargetRegressionXML</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getPathToTargetRegressionXML () {
            return pathToTargetRegressionXML;
        }

        /**
         * <p>
         * Sets the <code>encryptedPassword</code> field.
         * </p>
         * 
         * @param  encryptedPassword  The encrypted password to set for generated users.
         */
        public void setEncryptedPassword (String encryptedPassword) {
            this.encryptedPassword = StringUtils.encryptPassword (encryptedPassword);
        }

        /**
         * <p>
         * Returns the value of the <code>encryptedPassword</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getEncryptedPassword () {
            return encryptedPassword;
        }

        /**
         * <p>
         * Sets the <code>userFilter</code> field.
         * </p>
         * 
         * @param  userFilter  The user filter to use when generating security test users.
         */
        public void setUserFilter (String userFilter) {
            this.userFilter = userFilter;
        }

        /**
         * <p>
         * Returns the value of the <code>userFilter</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getUserFilter () {
            return userFilter;
        }

        /**
         * <p>
         * Sets the <code>domainFilter</code> field.
         * </p>
         * 
         * @param  domainFilter  The domain filter to use when generating security filters.
         */
        public void setDomainFilter (String domainFilter) {
            this.domainFilter = domainFilter;
        }

        /**
         * <p>
         * Returns the value of the <code>domainFilter</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getDomainFilter () {
            return domainFilter;
        }

        /**
         * <p>
         * Sets the <code>userMode</code> field.
         * </p>
         * 
         * @param  userMode  The mode to use when generating users. See the GEN_MODE_* constants above.
         */
        public void setUserMode (int userMode) {
            this.userMode = userMode;
        }

        /**
         * <p>
         * Returns the value of the <code>userMode</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getUserMode () {
            return userMode;
        }

        /**
         * <p>
         * Sets the <code>queryMode</code> field.
         * </p>
         * 
         * @param  queryMode  The mode to use when generating queries. See the GEN_MODE_* constants above.
         */
        public void setQueryMode (int queryMode) {
            this.queryMode = queryMode;
        }

        /**
         * <p>
         * Returns the value of the <code>queryMode</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getQueryMode () {
            return queryMode;
        }

        /**
         * <p>
         * Sets the <code>planMode</code> field.
         * </p>
         * 
         * @param  planMode  The mode to use when generating plans. See the GEN_MODE_* constants above.
         */
        public void setPlanMode (int planMode) {
            this.planMode = planMode;
        }

        /**
         * <p>
         * Returns the value of the <code>planMode</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getPlanMode () {
            return planMode;
        }

        /**
         * <p>
         * Sets the <code>planModeType</code> field.
         * </p>
         * 
         * @param  planModeType  The plan mode type to use when generating plans. See the PLAN_MODE_TYPE_* constants above.
         */
        public void setPlanModeType (int planModeType) {
            this.planModeType = planModeType;
        }

        /**
         * <p>
         * Returns the value of the <code>planModeType</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getPlanModeType () {
            return planModeType;
        }

        /**
         * <p>
         * Sets the <code>planIdPrefix</code> field.
         * </p>
         * 
         * @param  planIdPrefix  The prefix to use for plan ID's instead of the default "sp".
         */
        public void setPlanIdPrefix (String planIdPrefix) {
            this.planIdPrefix = planIdPrefix;
        }

        /**
         * <p>
         * Returns the value of the <code>planIdPrefix</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getPlanIdPrefix () {
            return planIdPrefix;
        }

        /**
         * <p>
         * Sets the <code>planGenerateExpectedOutcome</code> field.
         * </p>
         * 
         * @param  planGenerateExpectedOutcome  Indicates whether to automatically generate plan outcome.
         */
        public void setPlanGenerateExpectedOutcome (boolean planGenerateExpectedOutcome) {
            this.planGenerateExpectedOutcome = planGenerateExpectedOutcome;
        }

        /**
         * <p>
         * Returns the value of the <code>planGenerateExpectedOutcome</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isPlanGenerateExpectedOutcome () {
            return planGenerateExpectedOutcome;
        }

        /**
         * <p>
         * Sets the <code>flattenSecurityUsersXML</code> field.
         * </p>
         * 
         * @param  flattenSecurityUsersXML  Indicates whether to flatten the users section (true) or pretty print the XML (false.)
         */
        public void setFlattenSecurityUsersXML (boolean flattenSecurityUsersXML) {
            this.flattenSecurityUsersXML = flattenSecurityUsersXML;
        }

        /**
         * <p>
         * Returns the value of the <code>flattenSecurityUsersXML</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isFlattenSecurityUsersXML () {
            return flattenSecurityUsersXML;
        }

        /**
         * <p>
         * Sets the <code>flattenSecurityQueryQueriesXML</code> field.
         * </p>
         * 
         * @param  flattenSecurityQueryQueriesXML  Indicates whether to flatten the queries section of type QUERY (true) or pretty print the XML (false.)
         */
        public void setFlattenSecurityQueryQueriesXML (boolean flattenSecurityQueryQueriesXML) {
            this.flattenSecurityQueryQueriesXML = flattenSecurityQueryQueriesXML;
        }

        /**
         * <p>
         * Returns the value of the <code>flattenSecurityQueryQueriesXML</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isFlattenSecurityQueryQueriesXML () {
            return flattenSecurityQueryQueriesXML;
        }

        /**
         * <p>
         * Sets the <code>flattenSecurityQueryProceduresXML</code> field.
         * </p>
         * 
         * @param  flattenSecurityQueryProceduresXML  Indicates whether to flatten the queries section of type PROCEDURE (true) or pretty print the XML (false.)
         */
        public void setFlattenSecurityQueryProceduresXML (boolean flattenSecurityQueryProceduresXML) {
            this.flattenSecurityQueryProceduresXML = flattenSecurityQueryProceduresXML;
        }

        /**
         * <p>
         * Returns the value of the <code>flattenSecurityQueryProceduresXML</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isFlattenSecurityQueryProceduresXML () {
            return flattenSecurityQueryProceduresXML;
        }

        /**
         * <p>
         * Sets the <code>flattenSecurityQueryWebServicesXML</code> field.
         * </p>
         * 
         * @param  flattenSecurityQueryWebServicesXML  Indicates whether to flatten the queries section of type WEB_SERVICE (true) or pretty print the XML (false.)
         */
        public void setFlattenSecurityQueryWebServicesXML (boolean flattenSecurityQueryWebServicesXML) {
            this.flattenSecurityQueryWebServicesXML = flattenSecurityQueryWebServicesXML;
        }

        /**
         * <p>
         * Returns the value of the <code>flattenSecurityQueryWebServicesXML</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isFlattenSecurityQueryWebServicesXML () {
            return flattenSecurityQueryWebServicesXML;
        }

        /**
         * <p>
         * Sets the <code>flattenSecurityPlansXML</code> field.
         * </p>
         * 
         * @param  flattenSecurityPlansXML  Indicates whether to flatten the plans section (true) or pretty print the XML (false.)
         */
        public void setFlattenSecurityPlansXML (boolean flattenSecurityPlansXML) {
            this.flattenSecurityPlansXML = flattenSecurityPlansXML;
        }

        /**
         * <p>
         * Returns the value of the <code>flattenSecurityPlansXML</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isFlattenSecurityPlansXML () {
            return flattenSecurityPlansXML;
        }

        public static class DefaultProcParamValues {
            String bitVal;
            String varcharVal;
            String charVal;
            String clobVal;
            String integerVal;
            String intVal;
            String bigintVal;
            String smallintVal;
            String tinyintVal;
            String decimalVal;
            String numericVal;
            String realVal;
            String floatVal;
            String doubleVal;
            String dateVal;
            String timeVal;
            String timestampVal;
            String binaryVal;
            String varbinaryVal;
            String blobVal;
            String xmlVal;
            
            /**
             * <p>
             * Constructor.
             * </p>
             */
            public DefaultProcParamValues() {}

            /**
             * <p>
             * Copy Constructor.
             * </p>
             */
            public DefaultProcParamValues (DefaultProcParamValues d) {
                if (d != null) {
                    this.bitVal = d.getBitVal();
                    this.varcharVal = d.getVarcharVal();
                    this.charVal = d.getCharVal();
                    this.clobVal = d.getClobVal();
                    this.integerVal = d.getIntegerVal();
                    this.intVal = d.getIntVal();
                    this.bigintVal = d.getBigintVal();
                    this.smallintVal = d.getSmallintVal();
                    this.tinyintVal = d.getTinyintVal();
                    this.decimalVal = d.getDecimalVal();
                    this.numericVal = d.getNumericVal();
                    this.realVal = d.getRealVal();
                    this.floatVal = d.getFloatVal();
                    this.doubleVal = d.getDoubleVal();
                    this.dateVal = d.getDateVal();
                    this.timeVal = d.getTimeVal();
                    this.timestampVal = d.getTimestampVal();
                    this.binaryVal = d.getBinaryVal();
                    this.varbinaryVal = d.getVarbinaryVal();
                    this.blobVal = d.getBlobVal();
                    this.xmlVal = d.getXmlVal();
                }
            }

            /**
             * <p>
             * Constructor. Extracts attribute values from {@link Element} object.
             * </p>
             */
            public DefaultProcParamValues (Element dNode) {
                for (Element dvNode : dNode.getChildren()) {
                    if (dvNode.getName().equals ("bit"))
                        this.bitVal = dvNode.getText();

                    if (dvNode.getName().equals ("varchar"))
                        this.varcharVal = dvNode.getText();

                    if (dvNode.getName().equals ("char"))
                        this.charVal = dvNode.getText();

                    if (dvNode.getName().equals ("clob"))
                        this.clobVal = dvNode.getText();

                    if (dvNode.getName().equals ("integer"))
                        this.integerVal = dvNode.getText();

                    if (dvNode.getName().equals ("int"))
                        this.intVal = dvNode.getText();

                    if (dvNode.getName().equals ("bigint"))
                        this.bigintVal = dvNode.getText();

                    if (dvNode.getName().equals ("smallint"))
                        this.smallintVal = dvNode.getText();

                    if (dvNode.getName().equals ("tinyint"))
                        this.tinyintVal = dvNode.getText();

                    if (dvNode.getName().equals ("decimal"))
                        this.decimalVal = dvNode.getText();

                    if (dvNode.getName().equals ("numeric"))
                        this.numericVal = dvNode.getText();

                    if (dvNode.getName().equals ("real"))
                        this.realVal = dvNode.getText();

                    if (dvNode.getName().equals ("float"))
                        this.floatVal = dvNode.getText();

                    if (dvNode.getName().equals ("double"))
                        this.doubleVal = dvNode.getText();

                    if (dvNode.getName().equals ("date"))
                        this.dateVal = dvNode.getText();

                    if (dvNode.getName().equals ("time"))
                        this.timeVal = dvNode.getText();

                    if (dvNode.getName().equals ("timestamp"))
                        this.timestampVal = dvNode.getText();

                    if (dvNode.getName().equals ("binary"))
                        this.binaryVal = dvNode.getText();

                    if (dvNode.getName().equals ("varbinary"))
                        this.varbinaryVal = dvNode.getText();

                    if (dvNode.getName().equals ("blob"))
                        this.blobVal = dvNode.getText();

                    if (dvNode.getName().equals ("xml"))
                        this.xmlVal = dvNode.getText();
                }
            }

            /**
             * <p>
             * Returns the object as a JDom Element.
             * </p>
             * 
             * @param  name   The name of the element to use.
             * @param  indent The number of tabs (spaces) to indent the child elements.
             * @return        The value.
             */
            public Element toElement(
                String name,
                int indent
            ) {
                String indentStr = StringUtils.getIndent (indent);
        
                Element result = new Element (name);

                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("bit").setText (this.bitVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("varchar").setText (this.varcharVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("char").setText (this.charVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("clob").setText (this.clobVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("integer").setText (this.integerVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("int").setText (this.intVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("bigint").setText (this.bigintVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("smallint").setText (this.smallintVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("tinyint").setText (this.tinyintVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("decimal").setText (this.decimalVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("numeric").setText (this.numericVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("real").setText (this.realVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("float").setText (this.floatVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("double").setText (this.doubleVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("date").setText (this.dateVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("time").setText (this.timeVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("timestamp").setText (this.timestampVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("binary").setText (this.binaryVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("varbinary").setText (this.varbinaryVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("blob").setText (this.blobVal));
        
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("xml").setText (this.xmlVal));
        
                return result;
            }

            public void setBitVal (String bitVal) {
                this.bitVal = bitVal;
            }

            public String getBitVal () {
                return bitVal;
            }

            public void setVarcharVal (String varcharVal) {
                this.varcharVal = varcharVal;
            }

            public String getVarcharVal () {
                return varcharVal;
            }

            public void setCharVal (String charVal) {
                this.charVal = charVal;
            }

            public String getCharVal () {
                return charVal;
            }

            public void setClobVal (String clobVal) {
                this.clobVal = clobVal;
            }

            public String getClobVal () {
                return clobVal;
            }

            public void setIntegerVal (String integerVal) {
                this.integerVal = integerVal;
            }

            public String getIntegerVal () {
                return integerVal;
            }

            public void setIntVal (String intVal) {
                this.intVal = intVal;
            }

            public String getIntVal () {
                return intVal;
            }

            public void setBigintVal (String bigintVal) {
                this.bigintVal = bigintVal;
            }

            public String getBigintVal () {
                return bigintVal;
            }

            public void setSmallintVal (String smallintVal) {
                this.smallintVal = smallintVal;
            }

            public String getSmallintVal () {
                return smallintVal;
            }

            public void setTinyintVal (String tinyintVal) {
                this.tinyintVal = tinyintVal;
            }

            public String getTinyintVal () {
                return tinyintVal;
            }

            public void setDecimalVal (String decimalVal) {
                this.decimalVal = decimalVal;
            }

            public String getDecimalVal () {
                return decimalVal;
            }

            public void setNumericVal (String numericVal) {
                this.numericVal = numericVal;
            }

            public String getNumericVal () {
                return numericVal;
            }

            public void setRealVal (String realVal) {
                this.realVal = realVal;
            }

            public String getRealVal () {
                return realVal;
            }

            public void setFloatVal (String floatVal) {
                this.floatVal = floatVal;
            }

            public String getFloatVal () {
                return floatVal;
            }

            public void setDoubleVal (String doubleVal) {
                this.doubleVal = doubleVal;
            }

            public String getDoubleVal () {
                return doubleVal;
            }

            public void setDateVal (String dateVal) {
                this.dateVal = dateVal;
            }

            public String getDateVal () {
                return dateVal;
            }

            public void setTimeVal (String timeVal) {
                this.timeVal = timeVal;
            }

            public String getTimeVal () {
                return timeVal;
            }

            public void setTimestampVal (String timestampVal) {
                this.timestampVal = timestampVal;
            }

            public String getTimestampVal () {
                return timestampVal;
            }

            public void setBinaryVal (String binaryVal) {
                this.binaryVal = binaryVal;
            }

            public String getBinaryVal () {
                return binaryVal;
            }

            public void setVarbinaryVal (String varbinaryVal) {
                this.varbinaryVal = varbinaryVal;
            }

            public String getVarbinaryVal () {
                return varbinaryVal;
            }

            public void setBlobVal (String blobVal) {
                this.blobVal = blobVal;
            }

            public String getBlobVal () {
                return blobVal;
            }

            public void setXmlVal (String xmlVal) {
                this.xmlVal = xmlVal;
            }

            public String getXmlVal () {
                return xmlVal;
            }
        }
    }
    
    public static class TestRunParams {
        public static final int TEST_TYPE_FUNCTIONAL = 0;
        public static final int TEST_TYPE_MIGRATION = 1;
        public static final int TEST_TYPE_REGRESSION = 2;
        public static final int TEST_TYPE_PERFORMANCE = 3;
        public static final int TEST_TYPE_SECURITY = 4;
        public static final String[] TEST_TYPE_LABELS = {
            "functional",
            "migration",
            "regression",
            "performance",
            "security"
        };
        
        public static final int PRINT_OUTPUT_VERBOSE = 0;
        public static final int PRINT_OUTPUT_SUMMARY = 1;
        public static final int PRINT_OUTPUT_SILENT = 2;
        public static final String[] PRINT_OUTPUT_LABELS = {
            "verbose",
            "summary",
            "silent"
        };
        
        int testType;
        String logFilePath;
        int logDelimiter;
        boolean logAppend = true;
        String baseDir;
        int delimiter;
        int printOutput;
        int perfTestThreads;
        int perfTestDuration;
        int perfTestSleepPrint;
        int perfTestSleepExec;
        boolean runQueries = false;
        boolean runProcedures = false;
        boolean runWS = false;
        boolean useAllDatasources = false;
        List<String> datasources;
        List<String> resources;
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public TestRunParams() {}

        /**
         * <p>
         * Copy Constructor.
         * </p>
         */
        public TestRunParams (TestRunParams t) {
            if (t != null) {
                this.testType = t.getTestType();
                this.logFilePath = t.getLogFilePath();
                this.logDelimiter = t.getLogDelimiter();
                this.logAppend = t.isLogAppend();
                this.baseDir = t.getBaseDir();
                this.delimiter = t.getDelimiter();
                this.printOutput = t.getPrintOutput();
                this.perfTestThreads = t.getPerfTestThreads();
                this.perfTestDuration = t.getPerfTestDuration();
                this.perfTestSleepPrint = t.getPerfTestSleepPrint();
                this.perfTestSleepExec = t.getPerfTestSleepExec();
                this.runQueries = t.isRunQueries();
                this.runProcedures = t.isRunProcedures();
                this.runWS = t.isRunWS();
                this.useAllDatasources = t.isUseAllDatasources();
                this.datasources = (t.getDatasources() != null) ? new ArrayList<String> (t.getDatasources()) : null; // do not update!!
                this.resources = (t.getResources() != null) ? new ArrayList<String> (t.getResources()) : null; // do not update!!
            }
        }

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public TestRunParams (Element tNode) {
            for (Element tcNode : tNode.getChildren()) {
                if (tcNode.getName().equals ("testType")) {
                    for (int i = 0; i < TEST_TYPE_LABELS.length; i++) {
                        if (tcNode.getText().equalsIgnoreCase (TEST_TYPE_LABELS[i])) {
                            this.testType = i;
                            break;
                        }
                    }
                }

                if (tcNode.getName().equals ("logFilePath"))
                    this.logFilePath = tcNode.getText();

                if (tcNode.getName().equals ("logDelimiter")) {
                    for (int i = 0; i < DELIMITER_LABELS.length; i++) {
                        if (tcNode.getText().equalsIgnoreCase (DELIMITER_LABELS[i])) {
                            this.logDelimiter = i;
                            break;
                        }
                    }
                }

                if (tcNode.getName().equals ("logAppend"))
                    this.logAppend = (tcNode.getText() == null) ? true : tcNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (tcNode.getName().equals ("baseDir"))
                    this.baseDir = tcNode.getText();

                if (tcNode.getName().equals ("delimiter")) {
                    for (int i = 0; i < DELIMITER_LABELS.length; i++) {
                        if (tcNode.getText().equalsIgnoreCase (DELIMITER_LABELS[i])) {
                            this.delimiter = i;
                            break;
                        }
                    }
                }

                if (tcNode.getName().equals ("printOutput")) {
                    for (int i = 0; i < PRINT_OUTPUT_LABELS.length; i++) {
                        if (tcNode.getText().equalsIgnoreCase (PRINT_OUTPUT_LABELS[i])) {
                            this.printOutput = i;
                            break;
                        }
                    }
                }

                if (tcNode.getName().equals ("perfTestThreads"))
                    this.perfTestThreads = Integer.parseInt (tcNode.getText());

                if (tcNode.getName().equals ("perfTestDuration"))
                    this.perfTestDuration = Integer.parseInt (tcNode.getText());

                if (tcNode.getName().equals ("perfTestSleepPrint"))
                    this.perfTestSleepPrint = Integer.parseInt (tcNode.getText());

                if (tcNode.getName().equals ("perfTestSleepExec"))
                    this.perfTestSleepExec = Integer.parseInt (tcNode.getText());

                if (tcNode.getName().equals ("runQueries"))
                    this.runQueries = (tcNode.getText() == null) ? true : tcNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (tcNode.getName().equals ("runProcedures"))
                    this.runProcedures = (tcNode.getText() == null) ? true : tcNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (tcNode.getName().equals ("runWS"))
                    this.runWS = (tcNode.getText() == null) ? true : tcNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (tcNode.getName().equals ("useAllDatasources"))
                    this.useAllDatasources = (tcNode.getText() == null) ? true : tcNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (tcNode.getName().equals ("datasources")) {
                    for (Element dNode : tcNode.getChildren()) {
                        if (dNode.getName().equals ("dsName") && dNode.getText() != null && dNode.getText().length() > 0) { // ignore empty strings
                            if (datasources == null)
                                datasources = new ArrayList<String>();
                            
                            datasources.add (dNode.getText());
                        }
                    }
                }

                if (tcNode.getName().equals ("resources")) {
                    for (Element rNode : tcNode.getChildren()) {
                        if (rNode.getName().equals ("resource") && rNode.getText() != null && rNode.getText().length() > 0) { // ignore empty strings) {
                            if (resources == null)
                                resources = new ArrayList<String>();
                            
                            resources.add (rNode.getText());
                        }
                    }
                }
            }
        }

        /**
         * <p>
         * Returns the object as a JDom Element.
         * </p>
         * 
         * @param  name   The name of the element to use.
         * @param  indent The number of tabs (spaces) to indent the child elements.
         * @return        The value.
         */
        public Element toElement(
            String name,
            int indent
        ) {
            String indentStr = StringUtils.getIndent (indent);
            String indentStr2 = StringUtils.getIndent (indent + 1);
    
            Element result = new Element (name);

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("testType").setText (TEST_TYPE_LABELS[this.testType]));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("logFilePath").setText (this.logFilePath));

            if (this.logDelimiter != DELIMITER_NULL) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("logDelimiter").setText (DELIMITER_LABELS[this.logDelimiter]));
            }

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("logAppend").setText ("" + this.logAppend));

            if (this.baseDir != null) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("baseDir").setText (this.baseDir));
            }

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("delimiter").setText (DELIMITER_LABELS[this.delimiter]));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("printOutput").setText (PRINT_OUTPUT_LABELS[this.printOutput]));

            if (this.perfTestThreads > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("perfTestThreads").setText ("" + this.perfTestThreads));
            }

            if (this.perfTestThreads > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("perfTestDuration").setText ("" + this.perfTestDuration));
            }

            if (this.perfTestThreads > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("perfTestSleepPrint").setText ("" + this.perfTestSleepPrint));
            }

            if (this.perfTestThreads > 0) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("perfTestSleepExec").setText ("" + this.perfTestSleepExec));
            }

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("runQueries").setText ("" + this.runQueries));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("runProcedures").setText ("" + this.runProcedures));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("runWS").setText ("" + this.runWS));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("useAllDatasources").setText ("" + this.useAllDatasources));
            
            if (this.datasources != null && this.datasources.size() > 0) {
                Element dsNode = new Element ("datasources");
                
                for (String ds : datasources) {
                    dsNode.addContent ("\n" + indentStr2);
                    dsNode.addContent (new Element ("dsName").setText (ds));
                }
                
                    dsNode.addContent ("\n" + indentStr);

                result.addContent ("\n" + indentStr);
                result.addContent (dsNode);
            }
            
            if (this.resources != null && this.resources.size() > 0) {
                Element rNode = new Element ("resources");
                
                for (String r : resources) {
                    rNode.addContent ("\n" + indentStr2);
                    rNode.addContent (new Element ("resource").setText (r));
                }
                
                    rNode.addContent ("\n" + indentStr);

                result.addContent ("\n" + indentStr);
                result.addContent (rNode);
            }
            
            return result;
        }

        /**
         * <p>
         * Sets the <code>testType</code> field.
         * </p>
         * 
         * @param  testType  Defines what type of test will be executed.
         */
        public void setTestType (int testType) {
            this.testType = testType;
        }

        /**
         * <p>
         * Returns the value of the <code>testType</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getTestType () {
            return testType;
        }

        /**
         * <p>
         * Sets the <code>logFilePath</code> field.
         * </p>
         * 
         * @param  logFilePath  Full path to the test run results which are written to this log file.
         */
        public void setLogFilePath (String logFilePath) {
            this.logFilePath = logFilePath;
        }

        /**
         * <p>
         * Returns the value of the <code>logFilePath</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getLogFilePath () {
            return logFilePath;
        }

        /**
         * <p>
         * Sets the <code>logDelimiter</code> field.
         * </p>
         * 
         * @param  logDelimiter  This is the delimiter used when generating the summary log file. See the DELIMITER_* constants above.
         */
        public void setLogDelimiter (int logDelimiter) {
            this.logDelimiter = logDelimiter;
        }

        /**
         * <p>
         * Returns the value of the <code>logDelimiter</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getLogDelimiter () {
            return logDelimiter;
        }

        /**
         * <p>
         * Sets the <code>logAppend</code> field.
         * </p>
         * 
         * @param  logAppend  Indicates whether to append log entries to the log file.
         */
        public void setLogAppend (boolean logAppend) {
            this.logAppend = logAppend;
        }

        /**
         * <p>
         * Returns the value of the <code>logAppend</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isLogAppend () {
            return logAppend;
        }

        /**
         * <p>
         * Sets the <code>baseDir</code> field.
         * </p>
         * 
         * @param  baseDir  Base directory for output result files.
         */
        public void setBaseDir (String baseDir) {
            this.baseDir = baseDir;
        }

        /**
         * <p>
         * Returns the value of the <code>baseDir</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getBaseDir () {
            return baseDir;
        }

        /**
         * <p>
         * Sets the <code>delimiter</code> field.
         * </p>
         * 
         * @param  delimiter  This is the delimiter used when generating the result data files.
         */
        public void setDelimiter (int delimiter) {
            this.delimiter = delimiter;
        }

        /**
         * <p>
         * Returns the value of the <code>delimiter</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getDelimiter () {
            return delimiter;
        }

        /**
         * <p>
         * Sets the <code>printOutput</code> field.
         * </p>
         * 
         * @param  printOutput  Indicates how verbose to be with output messages. See the PRINT_OUTPUT_* constants above.
         */
        public void setPrintOutput (int printOutput) {
            this.printOutput = printOutput;
        }

        /**
         * <p>
         * Returns the value of the <code>printOutput</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getPrintOutput () {
            return printOutput;
        }

        /**
         * <p>
         * Sets the <code>perfTestThreads</code> field.
         * </p>
         * 
         * @param  perfTestThreads  The number of threads to create when doing performance testing.
         */
        public void setPerfTestThreads (int perfTestThreads) {
            this.perfTestThreads = perfTestThreads;
        }

        /**
         * <p>
         * Returns the value of the <code>perfTestThreads</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getPerfTestThreads () {
            return perfTestThreads;
        }

        /**
         * <p>
         * Sets the <code>perfTestDuration</code> field.
         * </p>
         * 
         * @param  perfTestDuration  The duration in seconds to execute the performance test for.
         */
        public void setPerfTestDuration (int perfTestDuration) {
            this.perfTestDuration = perfTestDuration;
        }

        /**
         * <p>
         * Returns the value of the <code>perfTestDuration</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getPerfTestDuration () {
            return perfTestDuration;
        }

        /**
         * <p>
         * Sets the <code>perfTestSleepPrint</code> field.
         * </p>
         * 
         * @param  perfTestSleepPrint  The number of seconds to sleep in between printing stats when 
         *                             executing the performance test.
         */
        public void setPerfTestSleepPrint (int perfTestSleepPrint) {
            this.perfTestSleepPrint = perfTestSleepPrint;
        }

        /**
         * <p>
         * Returns the value of the <code>perfTestSleepPrint</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getPerfTestSleepPrint () {
            return perfTestSleepPrint;
        }

        /**
         * <p>
         * Sets the <code>perfTestSleepExec</code> field.
         * </p>
         * 
         * @param  perfTestSleepExec  The number of seconds to sleep in between query executions when executing the performance test.
         */
        public void setPerfTestSleepExec (int perfTestSleepExec) {
            this.perfTestSleepExec = perfTestSleepExec;
        }

        /**
         * <p>
         * Returns the value of the <code>perfTestSleepExec</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getPerfTestSleepExec () {
            return perfTestSleepExec;
        }

        /**
         * <p>
         * Sets the <code>runQueries</code> field.
         * </p>
         * 
         * @param  runQueries  Defines whether query statement should be executed from the published test input 
         *                     file for views of the configured Composite data source.
         */
        public void setRunQueries (boolean runQueries) {
            this.runQueries = runQueries;
        }

        /**
         * <p>
         * Returns the value of the <code>runQueries</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isRunQueries () {
            return runQueries;
        }

        /**
         * <p>
         * Sets the <code>runProcedures</code> field.
         * </p>
         * 
         * @param  runProcedures  Defines whether procedure invocations should be executed from the published test 
         *                        input file for procedures of the configured Composite data source.
         */
        public void setRunProcedures (boolean runProcedures) {
            this.runProcedures = runProcedures;
        }

        /**
         * <p>
         * Returns the value of the <code>runProcedures</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isRunProcedures () {
            return runProcedures;
        }

        /**
         * <p>
         * Sets the <code>runWS</code> field.
         * </p>
         * 
         * @param  runWS  Defines whether web service invocation should be executed from the published test input 
         *                file for web services of the configured Composite data source.
         */
        public void setRunWS (boolean runWS) {
            this.runWS = runWS;
        }

        /**
         * <p>
         * Returns the value of the <code>runWS</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isRunWS () {
            return runWS;
        }

        /**
         * <p>
         * Sets the <code>useAllDatasources</code> field.
         * </p>
         * 
         * @param  useAllDatasources  Defines whether to run queries from all the Composite data sources found in the 
         *                            published test input file or use the list provided in the input file.
         */
        public void setUseAllDatasources (boolean useAllDatasources) {
            this.useAllDatasources = useAllDatasources;
        }

        /**
         * <p>
         * Returns the value of the <code>useAllDatasources</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isUseAllDatasources () {
            return useAllDatasources;
        }

        /**
         * <p>
         * Sets the <code>datasources</code> field.
         * </p>
         * 
         * @param  datasources  The list of data sources to execute when running the input file.
         */
        public void setDatasources (List<String> datasources) {
            this.datasources = datasources;
        }

        /**
         * <p>
         * Returns the value of the <code>datasources</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public List<String> getDatasources () {
            return datasources;
        }

        /**
         * <p>
         * Sets the <code>resources</code> field.
         * </p>
         * 
         * @param  resources  This is a list of resources for which use as a filter against the actual
         *                    Composite datasource(s) to perform comparisons or test executions.
         */
        public void setResources (List<String> resources) {
            this.resources = resources;
        }

        /**
         * <p>
         * Returns the value of the <code>resources</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public List<String> getResources () {
            return resources;
        }
    }
    
    public static class CompareFiles {
        String logFilePath;
        int logDelimiter;
        boolean logAppend;
        String baseDir1;
        String baseDir2;
        boolean compareQueries = true;
        boolean compareProcedures = true;
        boolean compareWS = true;
        boolean useAllDatasources = true;
        List<String> datasources;
        List<String> resources;
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public CompareFiles() {}

        /**
         * <p>
         * Copy Constructor.
         * </p>
         */
        public CompareFiles (CompareFiles c) {
            if (c != null) {
                this.logFilePath = c.getLogFilePath();
                this.logDelimiter = c.getLogDelimiter();
                this.logAppend = c.isLogAppend();
                this.baseDir1 = c.getBaseDir1();
                this.baseDir2 = c.getBaseDir2();
                this.compareQueries = c.isCompareQueries();
                this.compareProcedures = c.isCompareProcedures();
                this.compareWS = c.isCompareWS();
                this.useAllDatasources = c.isUseAllDatasources();
                this.datasources = (c.getDatasources() != null) ? new ArrayList<String> (c.getDatasources()) : null; // do not update!!
                this.resources = (c.getResources() != null) ? new ArrayList<String> (c.getResources()) : null; // do not update!!
            }
        }

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public CompareFiles (Element cNode) {
            for (Element ccNode : cNode.getChildren()) {
                if (ccNode.getName().equals ("logFilePath"))
                    this.logFilePath = ccNode.getText();

                if (ccNode.getName().equals ("logDelimiter")) {
                    for (int i = 0; i < DELIMITER_LABELS.length; i++) {
                        if (ccNode.getText().equalsIgnoreCase (DELIMITER_LABELS[i])) {
                            this.logDelimiter = i;
                            break;
                        }
                    }
                }

                if (ccNode.getName().equals ("logAppend"))
                    this.logAppend = (ccNode.getText() == null) ? true : ccNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ccNode.getName().equals ("baseDir1"))
                    this.baseDir1 = ccNode.getText();

                if (ccNode.getName().equals ("baseDir2"))
                    this.baseDir2 = ccNode.getText();

                if (ccNode.getName().equals ("compareQueries"))
                    this.compareQueries = (ccNode.getText() == null) ? true : ccNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ccNode.getName().equals ("compareProcedures"))
                    this.compareProcedures = (ccNode.getText() == null) ? true : ccNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ccNode.getName().equals ("compareWS"))
                    this.compareWS = (ccNode.getText() == null) ? true : ccNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ccNode.getName().equals ("useAllDatasources"))
                    this.useAllDatasources = (ccNode.getText() == null) ? true : ccNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ccNode.getName().equals ("datasources")) {
                    for (Element dNode : ccNode.getChildren()) {
                        if (dNode.getName().equals ("dsName") && dNode.getText() != null && dNode.getText().length() > 0) { // ignore empty strings) {
                            if (datasources == null)
                                datasources = new ArrayList<String>();
                            
                            datasources.add (dNode.getText());
                        }
                    }
                }

                if (ccNode.getName().equals ("resources")) {
                    for (Element rNode : ccNode.getChildren()) {
                        if (rNode.getName().equals ("resource") && rNode.getText() != null && rNode.getText().length() > 0) { // ignore empty strings) {
                            if (resources == null)
                                resources = new ArrayList<String>();
                            
                            resources.add (rNode.getText());
                        }
                    }
                }
            }
        }

        /**
         * <p>
         * Returns the object as a JDom Element.
         * </p>
         * 
         * @param  name   The name of the element to use.
         * @param  indent The number of tabs (spaces) to indent the child elements.
         * @return        The value.
         */
        public Element toElement(
            String name,
            int indent
        ) {
            String indentStr = StringUtils.getIndent (indent);
            String indentStr2 = StringUtils.getIndent (indent + 1);
    
            Element result = new Element (name);

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("logFilePath").setText (this.logFilePath));

            if (this.logDelimiter != DELIMITER_NULL) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("logDelimiter").setText (DELIMITER_LABELS[this.logDelimiter]));
            }

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("logAppend").setText ("" + this.logAppend));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("baseDir1").setText (this.baseDir1));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("baseDir2").setText (this.baseDir2));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("compareQueries").setText ("" + this.compareQueries));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("compareProcedures").setText ("" + this.compareProcedures));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("runWS").setText ("" + this.compareWS));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("useAllDatasources").setText ("" + this.useAllDatasources));
            
            if (this.datasources != null && this.datasources.size() > 0) {
                Element dsNode = new Element ("datasources");
                
                for (String ds : datasources) {
                    dsNode.addContent ("\n" + indentStr2);
                    dsNode.addContent (new Element ("dsName").setText (ds));
                }
                
                    dsNode.addContent ("\n" + indentStr);

                result.addContent ("\n" + indentStr);
                result.addContent (dsNode);
            }
            
            if (this.resources != null && this.resources.size() > 0) {
                Element rNode = new Element ("resources");
                
                for (String r : resources) {
                    rNode.addContent ("\n" + indentStr2);
                    rNode.addContent (new Element ("resource").setText (r));
                }
                
                    rNode.addContent ("\n" + indentStr);

                result.addContent ("\n" + indentStr);
                result.addContent (rNode);
            }
            
            return result;
        }

        /**
         * <p>
         * Sets the <code>logFilePath</code> field.
         * </p>
         * 
         * @param  logFilePath  Full path to the test run results which are written to this log file.
         */
        public void setLogFilePath (String logFilePath) {
            this.logFilePath = logFilePath;
        }

        /**
         * <p>
         * Returns the value of the <code>logFilePath</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getLogFilePath () {
            return logFilePath;
        }

        /**
         * <p>
         * Sets the <code>logDelimiter</code> field.
         * </p>
         * 
         * @param  logDelimiter  This is the delimiter used when generating the summary log file. See the DELIMITER_* constants above.
         */
        public void setLogDelimiter (int logDelimiter) {
            this.logDelimiter = logDelimiter;
        }

        /**
         * <p>
         * Returns the value of the <code>logDelimiter</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getLogDelimiter () {
            return logDelimiter;
        }

        /**
         * <p>
         * Sets the <code>logAppend</code> field.
         * </p>
         * 
         * @param  logAppend  Indicates whether to append log entries to the log file.
         */
        public void setLogAppend (boolean logAppend) {
            this.logAppend = logAppend;
        }

        /**
         * <p>
         * Returns the value of the <code>logAppend</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isLogAppend () {
            return logAppend;
        }

        /**
         * <p>
         * Sets the <code>baseDir1</code> field.
         * </p>
         * 
         * @param  baseDir1  Base directory for output result files for instance 1 of the test runs.
         */
        public void setBaseDir1 (String baseDir1) {
            this.baseDir1 = baseDir1;
        }

        /**
         * <p>
         * Returns the value of the <code>baseDir1</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getBaseDir1 () {
            return baseDir1;
        }

        /**
         * <p>
         * Sets the <code>baseDir2</code> field.
         * </p>
         * 
         * @param  baseDir2  Base directory for output result files for instance 2 of the test runs.
         */
        public void setBaseDir2 (String baseDir2) {
            this.baseDir2 = baseDir2;
        }

        /**
         * <p>
         * Returns the value of the <code>baseDir2</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getBaseDir2 () {
            return baseDir2;
        }

        /**
         * <p>
         * Sets the <code>compareQueries</code> field.
         * </p>
         * 
         * @param  compareQueries  Defines whether query results should be compared based on entries found 
         *                         in the regression input file.
         */
        public void setCompareQueries (boolean compareQueries) {
            this.compareQueries = compareQueries;
        }

        /**
         * <p>
         * Returns the value of the <code>compareQueries</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isCompareQueries () {
            return compareQueries;
        }

        /**
         * <p>
         * Sets the <code>compareProcedures</code> field.
         * </p>
         * 
         * @param  compareProcedures  Defines whether procedure results should be compared based on entries 
         *                            found in the regression input file
         */
        public void setCompareProcedures (boolean compareProcedures) {
            this.compareProcedures = compareProcedures;
        }

        /**
         * <p>
         * Returns the value of the <code>compareProcedures</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isCompareProcedures () {
            return compareProcedures;
        }

        /**
         * <p>
         * Sets the <code>compareWS</code> field.
         * </p>
         * 
         * @param  compareWS  Defines whether web service results should be compared based on entries found 
         *                    in the regression input file.
         */
        public void setCompareWS (boolean compareWS) {
            this.compareWS = compareWS;
        }

        /**
         * <p>
         * Returns the value of the <code>compareWS</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isCompareWS () {
            return compareWS;
        }

        /**
         * <p>
         * Sets the <code>useAllDatasources</code> field.
         * </p>
         * 
         * @param  useAllDatasources  Defines whether to run queries from all the Composite data sources found in the 
         *                            published test input file or use the list provided in the input file.
         */
        public void setUseAllDatasources (boolean useAllDatasources) {
            this.useAllDatasources = useAllDatasources;
        }

        /**
         * <p>
         * Returns the value of the <code>useAllDatasources</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isUseAllDatasources () {
            return useAllDatasources;
        }

        /**
         * <p>
         * Sets the <code>datasources</code> field.
         * </p>
         * 
         * @param  datasources  The list of data sources to execute when running the input file.
         */
        public void setDatasources (List<String> datasources) {
            this.datasources = datasources;
        }

        /**
         * <p>
         * Returns the value of the <code>datasources</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public List<String> getDatasources () {
            return datasources;
        }

        /**
         * <p>
         * Sets the <code>resources</code> field.
         * </p>
         * 
         * @param  resources  This is a list of resources for which use as a filter against the actual
         *                    Composite datasource(s) to perform comparisons or test executions.
         */
        public void setResources (List<String> resources) {
            this.resources = resources;
        }

        /**
         * <p>
         * Returns the value of the <code>resources</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public List<String> getResources () {
            return resources;
        }
    }
    
    public static class CompareLogs {
        String logFilePath;
        int logDelimiter;
        boolean logAppend = true;
        String logFilePath1;
        String logFilePath2;
        int logDelimiter1;
        int delimiter1;
        int logDelimiter2;
        int delimiter2;
        String durationDelta; // interval string

        /**
         * <p>
         * Constructor.
         * </p>
         */
        public CompareLogs() {}

        /**
         * <p>
         * Copy Constructor.
         * </p>
         */
        public CompareLogs (CompareLogs c) {
            if (c != null) {
                this.logFilePath = c.getLogFilePath();
                this.logDelimiter = c.getLogDelimiter();
                this.logAppend = c.isLogAppend();
                this.logFilePath1 = c.getLogFilePath1();
                this.logFilePath2 = c.getLogFilePath2();
                this.logDelimiter1 = c.getLogDelimiter1();
                this.delimiter1 = c.getDelimiter1();
                this.logDelimiter2 = c.getLogDelimiter2();
                this.delimiter2 = c.getDelimiter2();
                this.durationDelta = c.getDurationDelta();
            }
        }

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public CompareLogs (Element cNode) {
            for (Element ccNode : cNode.getChildren()) {
                if (ccNode.getName().equals ("logFilePath"))
                    this.logFilePath = ccNode.getText();

                if (ccNode.getName().equals ("logDelimiter")) {
                    for (int i = 0; i < DELIMITER_LABELS.length; i++) {
                        if (ccNode.getText().equalsIgnoreCase (DELIMITER_LABELS[i])) {
                            this.logDelimiter = i;
                            break;
                        }
                    }
                }

                if (ccNode.getName().equals ("logAppend"))
                    this.logAppend = (ccNode.getText() == null) ? true : ccNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (ccNode.getName().equals ("logFilePath1"))
                    this.logFilePath1 = ccNode.getText();

                if (ccNode.getName().equals ("logFilePath2"))
                    this.logFilePath2 = ccNode.getText();

                if (ccNode.getName().equals ("logDelimiter1")) {
                    for (int i = 0; i < DELIMITER_LABELS.length; i++) {
                        if (ccNode.getText().equalsIgnoreCase (DELIMITER_LABELS[i])) {
                            this.logDelimiter1 = i;
                            break;
                        }
                    }
                }

                if (ccNode.getName().equals ("delimiter1")) { // deprecated but still supported
                    for (int i = 0; i < DELIMITER_LABELS.length; i++) {
                        if (ccNode.getText().equalsIgnoreCase (DELIMITER_LABELS[i])) {
                            this.logDelimiter1 = i;
                            break;
                        }
                    }
                }

                if (ccNode.getName().equals ("logDelimiter2")) {
                    for (int i = 0; i < DELIMITER_LABELS.length; i++) {
                        if (ccNode.getText().equalsIgnoreCase (DELIMITER_LABELS[i])) {
                            this.logDelimiter2 = i;
                            break;
                        }
                    }
                }

                if (ccNode.getName().equals ("delimiter2")) { // deprecated but still supported
                    for (int i = 0; i < DELIMITER_LABELS.length; i++) {
                        if (ccNode.getText().equalsIgnoreCase (DELIMITER_LABELS[i])) {
                            this.logDelimiter2 = i;
                            break;
                        }
                    }
                }

                if (ccNode.getName().equals ("durationDelta"))
                    this.durationDelta = ccNode.getText();
            }
        }

        /**
         * <p>
         * Returns the object as a JDom Element.
         * </p>
         * 
         * @param  name   The name of the element to use.
         * @param  indent The number of tabs (spaces) to indent the child elements.
         * @return        The value.
         */
        public Element toElement(
            String name,
            int indent
        ) {
            String indentStr = StringUtils.getIndent (indent);
    
            Element result = new Element (name);

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("logFilePath").setText (this.logFilePath));

            if (this.logDelimiter != DELIMITER_NULL) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("logDelimiter").setText (DELIMITER_LABELS[this.logDelimiter]));
            }

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("logAppend").setText ("" + this.logAppend));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("logFilePath1").setText (this.logFilePath1));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("logFilePath2").setText (this.logFilePath2));

            if (this.logDelimiter1 != DELIMITER_NULL) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("logDelimiter1").setText (DELIMITER_LABELS[this.logDelimiter1]));
            } else if (this.delimiter1 != DELIMITER_NULL) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("delimiter1").setText (DELIMITER_LABELS[this.delimiter1]));
            }

            if (this.logDelimiter2 != DELIMITER_NULL) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("logDelimiter2").setText (DELIMITER_LABELS[this.logDelimiter2]));
            } else if (this.delimiter2 != DELIMITER_NULL) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("delimiter2").setText (DELIMITER_LABELS[this.delimiter2]));
            }

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("durationDelta").setText (this.durationDelta));
            
            return result;
        }

        /**
         * <p>
         * Sets the <code>logFilePath</code> field.
         * </p>
         * 
         * @param  logFilePath  Full path to the test run results which are written to this log file.
         */
        public void setLogFilePath (String logFilePath) {
            this.logFilePath = logFilePath;
        }

        /**
         * <p>
         * Returns the value of the <code>logFilePath</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getLogFilePath() {
            return logFilePath;
        }

        /**
         * <p>
         * Sets the <code>logDelimiter</code> field.
         * </p>
         * 
         * @param  logDelimiter  This is the delimiter used when generating the summary log file. See the DELIMITER_* constants above.
         */
        public void setLogDelimiter (int logDelimiter) {
            this.logDelimiter = logDelimiter;
        }

        /**
         * <p>
         * Returns the value of the <code>logDelimiter</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getLogDelimiter() {
            return logDelimiter;
        }

        /**
         * <p>
         * Sets the <code>logAppend</code> field.
         * </p>
         * 
         * @param  logAppend  Indicates whether to append log entries to the log file.
         */
        public void setLogAppend (boolean logAppend) {
            this.logAppend = logAppend;
        }

        /**
         * <p>
         * Returns the value of the <code>logAppend</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isLogAppend() {
            return logAppend;
        }

        /**
         * <p>
         * Sets the <code>logFilePath1</code> field.
         * </p>
         * 
         * @param  logFilePath1  Path to the query execution log file representing the baseline system.
         */
        public void setLogFilePath1 (String logFilePath1) {
            this.logFilePath1 = logFilePath1;
        }

        /**
         * <p>
         * Returns the value of the <code>logFilePath1</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getLogFilePath1() {
            return logFilePath1;
        }

        /**
         * <p>
         * Sets the <code>logFilePath2</code> field.
         * </p>
         * 
         * @param  logFilePath2  Path to the query execution log file representing the system to compare results with.
         */
        public void setLogFilePath2 (String logFilePath2) {
            this.logFilePath2 = logFilePath2;
        }

        /**
         * <p>
         * Returns the value of the <code>logFilePath2</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getLogFilePath2() {
            return logFilePath2;
        }

        /**
         * <p>
         * Sets the <code>logDelimiter1</code> field.
         * </p>
         * 
         * @param  logDelimiter1  This is the delimiter used within query execution log file. See the DELIMITER_* constants above.
         */
        public void setLogDelimiter1 (int logDelimiter1) {
            this.logDelimiter1 = logDelimiter1;
        }

        /**
         * <p>
         * Returns the value of the <code>logDelimiter1</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getLogDelimiter1() {
            return logDelimiter1;
        }

        /**
         * <p>
         * Sets the <code>delimiter1</code> field.
         * </p>
         * 
         * @param  delimiter1  This is delimiter1. See the DELIMITER_* constants above.
         */
        public void setDelimiter1 (int delimiter1) {
            this.delimiter1 = delimiter1;
        }

        /**
         * <p>
         * Returns the value of the <code>delimiter1</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getDelimiter1() {
            return delimiter1;
        }

        /**
         * <p>
         * Sets the <code>logDelimiter2</code> field.
         * </p>
         * 
         * @param  logDelimiter2  This is the delimiter used within query execution log file. See the DELIMITER_* constants above.
         */
        public void setLogDelimiter2 (int logDelimiter2) {
            this.logDelimiter2 = logDelimiter2;
        }

        /**
         * <p>
         * Returns the value of the <code>logDelimiter2</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getLogDelimiter2() {
            return logDelimiter2;
        }

        /**
         * <p>
         * Sets the <code>delimiter2</code> field.
         * </p>
         * 
         * @param  delimiter2  This is delimiter2. See the DELIMITER_* constants above.
         */
        public void setDelimiter2 (int delimiter2) {
            this.delimiter2 = delimiter2;
        }

        /**
         * <p>
         * Returns the value of the <code>delimiter2</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public int getDelimiter2() {
            return delimiter2;
        }

        /**
         * <p>
         * Sets the <code>durationDelta</code> field.
         * </p>
         * 
         * @param  durationDelta  Default duration delta for all queries.
         */
        public void setDurationDelta (String durationDelta) {
            this.durationDelta = durationDelta;
        }

        /**
         * <p>
         * Returns the value of the <code>durationDelta</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getDurationDelta() {
            return durationDelta;
        }
    }

    public static class SecurityPlanTest {
        String id;
        boolean enabled = true;
        String userId;
        String queryId;
        boolean expectedOutcome = false;
        String description;

        /**
         * <p>
         * Constructor.
         * </p>
         */
        public SecurityPlanTest() {}

        /**
         * <p>
         * Copy Constructor.
         * </p>
         */
        public SecurityPlanTest (SecurityPlanTest spt) {
            if (spt != null) {
                this.id = spt.getId();
                this.enabled = spt.isEnabled();
                this.userId = spt.getUserId();
                this.queryId = spt.getQueryId();
                this.expectedOutcome = spt.isExpectedOutcome();
                this.description = spt.getDescription();
            }
        }

        /**
         * <p>
         * Constructor. Extracts attribute values from {@link Element} object.
         * </p>
         */
        public SecurityPlanTest (Element sptNode) {
            for (Element sptcNode : sptNode.getChildren()) {
                if (sptcNode.getName().equals ("id"))
                    this.id = sptcNode.getText();

                if (sptcNode.getName().equals ("enabled"))
                    this.enabled = (sptcNode.getText() == null) ? false : sptcNode.getText().matches ("(?i)^(yes|true|on|1)$");

                if (sptcNode.getName().equals ("userId"))
                    this.userId = sptcNode.getText();

                if (sptcNode.getName().equals ("queryId"))
                    this.queryId = sptcNode.getText();

                if (sptcNode.getName().equals ("expectedOutcome"))
                    this.expectedOutcome = (sptcNode.getText() == null) ? false : sptcNode.getText().matches ("(?i)^(pass|yes|true|on|1)$");

                if (sptcNode.getName().equals ("description"))
                    this.description = sptcNode.getText();
            }
        }

        /**
         * <p>
         * Returns the object as a JDom Element.
         * </p>
         * 
         * @param  name   The name of the element to use.
         * @param  indent The number of tabs (spaces) to indent the child elements.
         * @return        The value.
         */
        public Element toElement(
            String name,
            int indent
        ) {
            String indentStr = StringUtils.getIndent (indent);
    
            Element result = new Element (name);

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("id").setText (this.id));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("enabled").setText ("" + this.enabled));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("userId").setText (this.userId));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("queryId").setText (this.queryId));

            result.addContent ("\n" + indentStr);
            result.addContent (new Element ("expectedOutcome").setText ((this.expectedOutcome) ? "PASS" : "FAIL"));

            if (this.description != null) {
                result.addContent ("\n" + indentStr);
                result.addContent (new Element ("description").setText (this.description));
            }

            return result;
        }

        /**
         * <p>
         * Sets the <code>id</code> field.
         * </p>
         * 
         * @param  id  The plan identifier.
         */
        public void setId (String id) {
            this.id = id;
        }

        /**
         * <p>
         * Returns the value of the <code>id</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getId() {
            return id;
        }

        /**
         * <p>
         * Sets the <code>enabled</code> field.
         * </p>
         * 
         * @param  enabled  Indicates whether this test is enabled or not.
         */
        public void setEnabled (boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * <p>
         * Returns the value of the <code>enabled</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * <p>
         * Sets the <code>userId</code> field.
         * </p>
         * 
         * @param  userId  The user ID to use for the test.
         */
        public void setUserId (String userId) {
            this.userId = userId;
        }

        /**
         * <p>
         * Returns the value of the <code>userId</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getUserId() {
            return userId;
        }

        /**
         * <p>
         * Sets the <code>queryId</code> field.
         * </p>
         * 
         * @param  queryId  The query ID to use for the test.
         */
        public void setQueryId (String queryId) {
            this.queryId = queryId;
        }

        /**
         * <p>
         * Returns the value of the <code>queryId</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getQueryId() {
            return queryId;
        }

        /**
         * <p>
         * Sets the <code>expectedOutcome</code> field.
         * </p>
         * 
         * @param  expectedOutcome  The expected outcome of the test. See the EXPECTED_OUTCOME_* constants above.
         */
        public void setExpectedOutcome (boolean expectedOutcome) {
            this.expectedOutcome = expectedOutcome;
        }

        /**
         * <p>
         * Returns the value of the <code>expectedOutcome</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public boolean isExpectedOutcome() {
            return expectedOutcome;
        }

        /**
         * <p>
         * Sets the <code>description</code> field.
         * </p>
         * 
         * @param  description  The description of the test.
         */
        public void setDescription (String description) {
            this.description = description;
        }

        /**
         * <p>
         * Returns the value of the <code>description</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public String getDescription() {
            return description;
        }
    }
}
