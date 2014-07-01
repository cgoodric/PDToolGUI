package com.cisco.dvbu.ps.deploytool.gui.core.runtime.log;

/*
 * (c) 2014 Cisco and/or its affiliates. All rights reserved.
 */

import com.cisco.dvbu.ps.deploytool.gui.resources.LogResource;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Data access object for log files. Used by {@link LogResource} servlet.
 * </p>
 *
 * @author Calvin Goodrich
 * @version 1.0
 */
public class LogsDAO {

    private static final Logger log = LoggerFactory.getLogger (LogsDAO.class);

    /**
     * <p>
     * Constructor.
     * </p>
     */
    public LogsDAO () {}
    
    /**
     * <p>
     * Called by the UI to gather the latest log messages from a log file starting with
     * a particular line in the file.
     * </p>
     * 
     * @param logFilePath  Path to the log file being monitored.
     * @param startLine    Line to start looking for new messages.
     * @param usePool      Indicates whether to use an in-memory table of log messages (1) or to look at the log file directly (0).
     * @return             The requested list of log messages.
     * @see                LogResource
     */
    public LogMessages getLogMessages (
        String logFilePath,
        int startLine,
        int usePool
    ) {
        log.debug ("logFilePath = " + logFilePath + ", startLine = " + startLine + ", usePool = " + usePool);

        // checks whether to use an in-memory log message storage pool. Should be a bit faster
        // than polling the filesystem every time
        //
        if (usePool == 1) {
            log.debug ("Retrieving log messages from log message buffer.");
            LogMessageBuffer lmb = LogMessageBuffer.getInstance();
            return lmb.getMessages (logFilePath, startLine);
            
        // otherwise look at the log file directly
        //
        } else {
            
            log.debug ("Retrieving log messages from filesystem.");
            BufferedReader br = null;
            String line;
            LogMessages lm = new LogMessages();
            List<String> messages = new ArrayList<String>();
            
            File f = new File (logFilePath);
            
            lm.setExecutionCompleted (true);
            
            // sanity check the log file path
            //
            if (! f.exists()) {
                log.error ("Log file, " + logFilePath + ", does not exist!");

                lm.setStatus (-2);
                lm.setLastLineNum (0);
                messages.add ("Log file, " + logFilePath + ", does not exist!");
                lm.setMessages (messages);

                return lm;
            }
            
            if (! f.canRead()) {
                log.error ("Log file, " + logFilePath + ", not readable!");

                lm.setStatus (-1);
                lm.setLastLineNum (0);
                messages.add ("Log file, " + logFilePath + ", not readable!");
                lm.setMessages (messages);

                return lm;
            }
            
            // read the log file
            //
            try {
                br = new BufferedReader (new FileReader (f));
                
                while ((line = br.readLine()) != null) {
                    messages.add (line);
                }
                
                br.close();
                
                lm.setLastLineNum (messages.size() - 1);
                lm.setStatus (0);
                lm.setMessages (messages);

            } catch (Exception e) {
                log.error ("Error reading " + logFilePath + ": " + e.getMessage());

                lm.setStatus (-3);
                messages.add ("Error reading " + logFilePath + ": " + e.getMessage());
                lm.setMessages (messages);
            }
            
            return lm;
        }
    }
}
