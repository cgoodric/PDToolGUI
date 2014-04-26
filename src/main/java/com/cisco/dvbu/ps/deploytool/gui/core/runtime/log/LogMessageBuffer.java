package com.cisco.dvbu.ps.deploytool.gui.core.runtime.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * Log message buffer class. This is used to store ExecutePDTool execution logs in memory. The reason for this is that
 * the browser app will be frequently polling the server for the last few log lines during the execution of ExecutePDTool
 * so frequently going to the filesystem and scanning through a file for the last few lines could be costly in terms of
 * performance.
 * </p>
 * <p>
 * Each creation of a new log buffer will cause a garbage collection to get rid of any stale logs and keep memory usage
 * under control.
 * </p>
 * <p>
 * This class uses the singleton pattern such that only one instance object will exist at a time. Use the
 * {@link LogMessageBuffer#getInstance} static method to access the instance.
 * </p>
 * <p>
 * The private static class {@link LogMessageContainer} is used to hold each log file's in-memory copy. A hash map, referenced
 * by log file path, is used to store each container instance.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class LogMessageBuffer {
    private static final Logger log = LoggerFactory.getLogger (LogMessageBuffer.class);
    private static final int LOG_EXPIRATION_TIME = 300000; // 5 minutes
    private static LogMessageBuffer lmb = null;
    
    // private Map to hold LogMessageContainer instances, referenced by log file path. Synchronized for thread safety.
    //
    private Map<String, LogMessageContainer> logPool = Collections.synchronizedMap (new HashMap<String, LogMessageContainer>());

    // private constructor so that it may not be instantiated by other classes.
    //
    private LogMessageBuffer() {}
    
    /**
     * <p>
     * Returns the singleton LogMessageBuffer instance. If it doesn't yet exist, it is created before being returned.
     * </p>
     * 
     * @return     The value.
     */
    public static LogMessageBuffer getInstance() {
        if (lmb == null) {
            log.debug ("Instanciating new object.");
            lmb = new LogMessageBuffer();
        }
        
        return lmb;
    }
    
    /**
     * <p>
     * Adds a new log message to the buffer for the specified log path.
     * </p>
     * 
     * @param  logFilePath  The path to the log file.
     */
    public void addMessage (
        String logFilePath,
        String message
    ) {
        LogMessageContainer lmc = logPool.get (logFilePath);
        
        if (lmc == null) {

            // run the garbage collector whenever a new log message container is added.
            //
            //gc(); // commenting out as this may be causing a concurrent access exception

            log.debug ("Creating new pool entry for " + logFilePath);

            lmc = new LogMessageContainer();
            lmc.setExecutionCompleted (false);
            lmc.setLogFilePath (logFilePath);
            
            logPool.put (logFilePath, lmc);
        }

        lmc.setLastUpdated (System.currentTimeMillis());
        lmc.addMessage (message);
    }
    
    /**
     * <p>
     * Adds a new log file to the buffer.
     * </p>
     * 
     * @param  logFilePath  The path to the log file.
     */
    public void setExecutionCompleted (
        String logFilePath
    ) {
        LogMessageContainer lmc = logPool.get (logFilePath);
        
        if (lmc == null) {
            log.error ("Attempted to set execution completed flag on non-existent log in pool: " + logFilePath);
            return;
        }

        lmc.setExecutionCompleted (true);

        // run the garbage collector
        //
        gc();
    }
    
    /**
     * <p>
     * Returns the log messages that start at <code>startLine</code>.
     * </p>
     * 
     * @return     The value.
     */
    public LogMessages getMessages (
        String logFilePath,
        int startLine
    ) {
        LogMessageContainer lmc = logPool.get (logFilePath);
        LogMessages lm = new LogMessages();
        
        if (lmc == null) {
            log.error ("Attempting to retrieve messages for non-existent log in pool: " + logFilePath);
            lm.setLastLineNum (-1);
        } else {
            log.debug ("Retrieving log messages starting with line " + startLine);
            List<String> messages = lmc.getMessages (startLine);
            lm.setLastLineNum (startLine + messages.size());
            lm.setExecutionCompleted (lmc.isExecutionCompleted());
            lm.setMessages (messages);
        }
        
        return lm;
    }
    
    // performs garbage collection on the the pool by getting rid of anything that hasn't been updated
    // within the expiration window defined by LOG_EXPIRATION_TIME.
    //
    private void gc() {
        log.debug ("Running garbage collector");
        
        Iterator it = logPool.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            LogMessageContainer lmc = logPool.get (key);
            
            if (lmc.isExecutionCompleted() && lmc.getLastUpdated() < System.currentTimeMillis() - LOG_EXPIRATION_TIME) {
                log.debug ("Removing key " + lmc.getLogFilePath());
                
                // use the iterator to remove the entry to avoid a ConcurrentModificationException
                //
                it.remove();
            }
        }
    }
    
    /**
     * <p>
     * Static internal class for containing log file messages.
     * </p>
     * 
     * @author Calvin Goodrich
     * @version 1.0
     */
    static class LogMessageContainer {
        private static final Logger log = LoggerFactory.getLogger (LogMessageContainer.class);
        private String logFilePath;
        private long lastUpdated;
        private List<String> messages;
        private boolean executionCompleted;
        
        /**
         * <p>
         * Constructor.
         * </p>
         */
        public LogMessageContainer() {};

        /**
         * <p>
         * Sets the <code>logFilePath</code> field.
         * </p>
         * 
         * @param  logFilePath  The path to the log file.
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
         * Sets the <code>lastUpdated</code> field.
         * </p>
         * 
         * @param  lastUpdated  The time, in system long time, that the log messages were last updated (added to.)
         */
        public void setLastUpdated (long lastUpdated) {
            this.lastUpdated = lastUpdated;
        }

        /**
         * <p>
         * Returns the value of the <code>lastUpdated</code> field.
         * </p>
         * 
         * @return     The value.
         */
        public long getLastUpdated () {
            return lastUpdated;
        }

        /**
         * <p>
         * Adds the <code>message</code> list to the end of the existing log messages.
         * </p>
         * 
         * @param  message  The time, in system long time, that the log messages were last updated (added to.)
         */
        public void addMessage (String message) {
            if (this.messages == null)
                this.messages = new ArrayList<String>();
            
            if (message != null)
                this.messages.add (message);
            else
                log.error ("message may not be null.");
        }


        /**
         * <p>
         * Returns the sublist of <code>messages</code> starting with <code>startLine</code> to the end of the list.
         * </p>
         * 
         * @return     The sublist.
         */
        public List<String> getMessages (
            int startLine
        ) {
            if (startLine > messages.size() || startLine < 0) {
                log.error ("Attempted to access log line number outside of valid range. log file = " + logFilePath + ", startLine = " + startLine + ", messages.size() = " + messages.size());
                return new ArrayList<String>();
            } else {
                log.debug ("Retrieving lines " + startLine + " to " + messages.size() + " of log " + logFilePath);
                return messages.subList (startLine, messages.size());
            }
        }

        /**
         * <p>
         * Sets the <code>executionCompleted</code> field.
         * </p>
         * 
         * @param  executionCompleted  A boolean value indicating whether the execution of ExecutePDTool associated 
         *                             with this log has completed or not.
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
    }
}
