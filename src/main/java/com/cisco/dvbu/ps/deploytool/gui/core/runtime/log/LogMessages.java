package com.cisco.dvbu.ps.deploytool.gui.core.runtime.log;

import java.util.List;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * <p>
 * Bean object for log messages. Intended to be serialized/deserialized by Jackson into/from JSON.
 * </p>
 * <p>
 * Ideally, a PDTool execution would be performed synchronously and the output streamed back to
 * the browser client. At this point, streaming communications between browser and server are not
 * well defined by the W3C so the idea here is to make an asynchronous call to execute a PDTool plan, then
 * poll periodically to get any new messages until the execution is complete. This class encapsulates
 * the polling result payload of new lines since the last poll, providing new messages and the last line
 * number of the log file (for the next polling cycle.)
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class LogMessages {
//    private static final Logger log = LoggerFactory.getLogger (LogMessages.class);
    
    private int lastLineNum;
    private boolean executionCompleted;
    private int status;
    private List<String> messages; 

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public LogMessages () {}

    /**
     * <p>
     * Sets the <code>lastLineNum</code> field.
     * </p>
     * 
     * @param  lastLineNum  The last line number in the log file (so that subsequent calls to get 
     *                      messages can specify a starting point.)
     */
    public void setLastLineNum (int lastLineNum) {
        this.lastLineNum = lastLineNum;
    }

    /**
     * <p>
     * Returns the value of the <code>lastLineNum</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getLastLineNum () {
        return lastLineNum;
    }

    /**
     * <p>
     * Sets the <code>executionCompleted</code> field.
     * </p>
     * 
     * @param  executionCompleted  The flag indicating if the ExecutePDTool script has completed its execution.
     */
    public void setExecutionCompleted (boolean executionCompleted) {
        this.executionCompleted = executionCompleted;
    }

    /**
     * <p>
     * Returns the value of the <code>executionCompleted</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public boolean isExecutionCompleted () {
        return executionCompleted;
    }

    /**
     * <p>
     * Sets the <code>status</code> field.
     * </p>
     * 
     * @param  status  The status of attempting to retrieve the contents of the log file.
     */
    public void setStatus (int status) {
        this.status = status;
    }

    /**
     * <p>
     * Returns the value of the <code>status</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public int getStatus () {
        return status;
    }

    /**
     * <p>
     * Sets the <code>messages</code> field.
     * </p>
     * 
     * @param  messages  The list of messages.
     */
    public void setMessages (List<String> messages) {
        this.messages = messages;
    }

    /**
     * <p>
     * Returns the value of the <code>messages</code> field.
     * </p>
     * 
     * @return     The value.
     */
    public List<String> getMessages () {
        return messages;
    }
}
