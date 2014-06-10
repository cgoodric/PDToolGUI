package com.cisco.dvbu.ps.deploytool.gui.core.runtime.execute;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for PDTool execution results. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * <p>
 * Ideally, a PDTool execution would be performed synchronously and the output streamed back to
 * the browser client. At this point, streaming communications between browser and server are not
 * well defined so the idea here is to make an asynchronous call to execute a PDTool plan, then
 * poll periodically to get any new messages until the execution is complete. This class encapsulates
 * the result of the asynchronous call providing status code, log file path, and any error messaging.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class ExecuteResult {
//    private static final Logger log = LoggerFactory.getLogger (ExecuteResult.class);
    
    private int resultCode;
    private String logFilePath;

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public ExecuteResult () {}

    /**
     * <p>
     * Sets the <code>resultCode</code> field.
     * </p>
     * 
     * @param  resultCode  The result code of the execution. 0 = success, non-zero = failure. Success has the meaning
     *                     that ExecutePDTool was successfully launched, not necessarily that it completed without failure.
     */
    public void setResultCode (int resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * <p>
     * Returns the value of the <code>resultCode</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getResultCode () {
        return resultCode;
    }

    /**
     * <p>
     * Sets the <code>logFilePath</code> field.
     * </p>
     * 
     * @param  logFilePath  The path to the execution's log file.
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
}
