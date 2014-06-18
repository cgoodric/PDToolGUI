package com.cisco.dvbu.ps.deploytool.gui.core.runtime.execute;

import com.cisco.dvbu.ps.deploytool.gui.core.runtime.file.FilesDAO;
import com.cisco.dvbu.ps.deploytool.gui.core.runtime.log.LogMessageBuffer;
import com.cisco.dvbu.ps.deploytool.gui.util.OSValidator;
import com.cisco.dvbu.ps.deploytool.gui.util.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is designed to run the ExecutePDTool script in a separate thread and capture the
 * output for use by the client. Simply instance the class to kick off an execution. Check the
 * launch result code to see if ExecutePDTool was able to start (may have to wait a few 1/10ths
 * of a second for the value to change from NOT_READY to SUCCESS or FAILURE.) If the launch result
 * code is SUCCESS then the log file path should be populated with the location of where the
 * output from ExecutePDTool is being written (also used with the {@link LogMessageBuffer} class
 * to reference specific execution's messages in memory.)
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ExecutePDTool {
    private static final Logger log = LoggerFactory.getLogger (ExecutePDTool.class);
    
    public static final int NOT_READY = -1;
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;
    
    public static final int TYPE_EXECUTE = 0;
    public static final int TYPE_VCS_INIT = 1;
    
    private String pdtHome;
    private final int type;
    private final String planPath;
    private final String confPath;
    private final String username;
    private final String password;
    private int launchResult = NOT_READY;
    private String logFilePath;
    
    
    /**
     * <p>
     * Constructor. For backwards compatibility of older usages.
     * </p>
     * 
     * @param iPlanPath  The absolute path to the deployment plan file.
     * @param iConfPath  The absolute path to the deployment configuration file.
     */
    public ExecutePDTool (
        String iPlanPath,
        String iConfPath
    ) {
        this (TYPE_EXECUTE, iPlanPath, iConfPath, null, null);
    }

    /**
     * <p>
     * Constructor. This also launches the ExecutePDTool script in a separate thread and captures the outputs to a log file.
     * </p>
     * 
     * @param iType      The type of execution to run. Valid values are TYPE_EXECUTE and TYPE_VCS_INIT.
     * @param iPlanPath  The absolute path to the deployment plan file. This can be set to <code>null</code> with TYPE_VCS_INIT executions.
     * @param iConfPath  The absolute path to the deployment configuration file.
     * @param iUsername  The user name to use with TYPE_VCS_INIT executions. Can be set to <code>null</code> (user name will be picked up from the deployment configuration.
     * @param iPassword  The password to use with TYPE_VCS_INIT executions. Can be set to <code>null</code> (password will be picked up from the deployment configuration.
     */
    public ExecutePDTool (
        int iType,
        String iPlanPath,
        String iConfPath,
        String iUsername,
        String iPassword
    ) {
        // the anonymous class below that runs a separate thread has an implied reference to its parent's "this".
        // referencing attributes of the parent allows the thread to continue running even after the parent 
        // class is dereferenced. once the run() method exits, the anonymous thread class should then be garbage 
        // collected.
        //
        this.pdtHome = FilesDAO.getPdtHome();
        this.type = iType;
        this.planPath = iPlanPath;
        this.confPath = iConfPath;
        this.username = iUsername;
        this.password = iPassword;

        // sanity check the inputs
        //
        if (type == TYPE_EXECUTE && planPath == null) {
            log.error ("The value for the plan path may not be null.");
            launchResult = FAILURE;
            return;
        }
        
        if (confPath == null) {
            log.error ("The value for the configuration path may not be null.");
            launchResult = FAILURE;
            return;
        }
        
        // look for module XML files that are using the old XML namespace and update them.
        //
        int errorCount = FilesDAO.updateModuleXmlFiles();
        if (errorCount > 0) {
            log.error ("Errors encountered while updating module XML files. Exiting.");
            launchResult = FAILURE;
            return;
        }

        // launch ExecutePDTool in a separate thread.
        //
        new Thread() {
            public void run() {
                ProcessBuilder pb;
                List<String> cmdArgs = new ArrayList<String>();
                Process p;
                
                // set up the basic call to ExecutePDTool based on operating system
                //
                if (OSValidator.isWindows()) {
                    cmdArgs.add ("cmd");
                    cmdArgs.add ("/c");
                    cmdArgs.add ("ExecutePDTool.bat");
                } else {
                    cmdArgs.add ("./ExecutePDTool.sh");
                }

                // set up arguments based on execute type
                //
                if (type == TYPE_EXECUTE) {
                    cmdArgs.add ("-exec");
                    cmdArgs.add (planPath);
                    cmdArgs.add ("-config");
                    cmdArgs.add (StringUtils.basename (confPath));
                } else if (type == TYPE_VCS_INIT) {
                    cmdArgs.add ("-vcsinit");

                    if (username != null && username.length() > 0) {
                        cmdArgs.add ("-vcsuser");
                        cmdArgs.add (username);
                    }
                    
                    if (password != null && password.length() > 0) {
                        cmdArgs.add ("-vcspassword");
                        cmdArgs.add (password);
                    }

                    cmdArgs.add ("-config");
                    cmdArgs.add (StringUtils.basename (confPath));                    
                }
                log.debug ("[Launcher Thread]: command string = " + cmdArgs);
                
                pb = new ProcessBuilder (cmdArgs);
                
                // set the working directory
                //
                pb.directory (new File (pdtHome + "/bin"));
                
                // redirect stderr to stdout to simplify reading of output
                //
                pb.redirectErrorStream (true);
                
                log.debug ("[Launcher Thread]: launching ExecutePDTool ...");
                try {
                    p = pb.start();
                } catch (IOException ioe) {
                    log.error ("[Launcher Thread]: Unable to launch ExecutePDTool: " + ioe.getMessage());
                    launchResult = FAILURE;
                    return;
                }
                log.debug ("[Launcher Thread]: ExecutePDTool launched.");
                
                // create a formatted date string to use as part of the log file name.
                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMMddHHmmss");
                
                // create the logs folder if it doesn't exist (PDTool doesn't ship with a logs folder.)
                //
                String logDirPath = pdtHome + "/logs";
                File logDirFile = new File (logDirPath);
                if (! logDirFile.exists()) {
                    logDirFile.mkdir();
                }
                
                // create an output writer for the log file on the filesystem.
                //
                String planName = StringUtils.basename (planPath).replaceAll("\\.dp$", "");
                logFilePath = pdtHome + "/logs/log_" + planName + "_" + sdf.format (dt) + ".log";
                File logFile = new File (logFilePath);
                BufferedWriter bw = null;
                log.debug ("[Launcher Thread]: writing log to " + logFilePath);
                
                // set the launch result code so that the calling class can see what happened with the launch.
                //
                launchResult = SUCCESS;
                
                try {
                    bw = new BufferedWriter (new FileWriter (logFile));
                } catch (IOException ioe) {
                    log.error ("Unable to open log file, " + logFilePath + ", for writing: " + ioe.getMessage());
                }

                // the process' input stream is the stdout and stderr (because we elected to combine the two 
                // streams) from ExecutePDTool.
                //
                BufferedReader br = new BufferedReader (new InputStreamReader (p.getInputStream()));
                String line;
                
                // get the singleton instance of the LogMessageBuffer class.
                //
                LogMessageBuffer lmb = LogMessageBuffer.getInstance();
                
                // read the output from ExecutePDTool and populate the LogMessageBuffer and write to the log file.
                //
                log.debug ("[Launcher Thread]: processing ExecutePDTool output ...");
                try {
                    while ((line = br.readLine()) != null) {
                        
                        // write the output line to the log message buffer
                        //
                        lmb.addMessage (logFilePath, line);
                        
                        // we need to check for null because we don't error out when the log file can't be created.
                        // we still have to capture the output of ExecutePDTool even when this is the case.
                        //
                        if (bw != null) {
                            bw.write (line);
                            bw.newLine();
                        }
                    }
                    
                    // close the log file writer
                    //
                    bw.flush();
                    bw.close();
                    
                } catch (IOException ioe) {
                    log.error ("[Launcher Thread]: Error reading/writing output from ExecutePDTool: " + ioe.getMessage());
                }
                log.debug ("[Launcher Thread]: ExecutePDTool output processing complete. Execution complete.");
                    
                // set the execution completed flag so the client knows to stop polling for additional log messages.
                //
                lmb.setExecutionCompleted (logFilePath);
            }
        }.start();

    }

    /**
     * <p>
     * Returns the value of the <code>launchResult</code> field. Caller should wait until the value is no longer -1 before
     * determining launch success or failure. NOTE: A success result merely indicates that the server was able
     * to successfully launch the ExecutePDTool process, not necessarily that it completed successfully.
     * </p>
     * 
     * @return     The value.
     */
    public int getLaunchResult () {
        return launchResult;
    }

    /**
     * <p>
     * Returns the value of the <code>logPath</code> field. Caller should wait until ExecutePDTool has successfully launched
     * before using this value (it will be <code>NULL</code> until then.)
     * </p>
     * 
     * @return     The value.
     */
    public String getLogFilePath () {
        return logFilePath;
    }
}
