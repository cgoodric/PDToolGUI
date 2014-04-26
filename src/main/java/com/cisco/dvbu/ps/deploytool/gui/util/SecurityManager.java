package com.cisco.dvbu.ps.deploytool.gui.util;

import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.Preferences;
import com.cisco.dvbu.ps.deploytool.gui.core.config.prefs.PreferencesManager;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Provides methods for enforcing security.
 * </p>
 * 
 * @author Calvin Goodrich
 * @version 1.0
 */
public class SecurityManager {

    private static final Logger log = LoggerFactory.getLogger (SecurityManager.class);

    // do not allow the class to be instanced.
    //
    private SecurityManager () {}
    
    /**
     * <p>
     * Uses the supplied HttpServletRequest object to verify the remote client's IP matches
     * one of the local server's network interfaces' assigned addresses.
     * </p>
     * 
     * @param  req                Servlet request object passed to the servlet.
     * @throws SecurityException  Thrown when the remote client's IP address doesn't match 
     *                            any local network interface addresses.
     */
    public static void testAccess (HttpServletRequest req) throws SecurityException {
        Preferences p = PreferencesManager.getInstance();
        boolean hostMatched = false;
        
        log.debug ("client IP: " + req.getRemoteAddr());
        log.debug ("client Host: " + req.getRemoteHost());
        
        if (p.getRestrictAccessToLocalhost().equals("true")) {
            try {
                for (Enumeration e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                    NetworkInterface n = (NetworkInterface) e.nextElement();
                    
                    for (InterfaceAddress ia : n.getInterfaceAddresses()) {
                        log.debug (n.getDisplayName() + ": " + ia.getAddress().getHostAddress());
                        if (req.getRemoteAddr().equals(ia.getAddress().getHostAddress())) {
                            hostMatched = true;
                        }
                    }
                }
            } catch (Exception e) {
                throw new SecurityException (e);
            }
            
            if (! hostMatched) {
                throw new SecurityException ("Connection from remote host not allowed.");
            }
        }
    }
}
