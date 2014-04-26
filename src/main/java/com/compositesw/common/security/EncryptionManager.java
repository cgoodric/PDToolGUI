/*
 * @(#)$RCSfile: EncryptionManager.java,v $
 *
 * Copyright (c) 2002 Composite Software, Inc.
 * 2988 Campus Drive, San Mateo, California, 94403, U.S.A.
 * All rights reserved.
 */


package com.compositesw.common.security;

import com.compositesw.common.Base64;
import com.compositesw.common.security.encryption.Decryption;
import com.compositesw.common.security.encryption.TEAV;

import java.security.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EncryptionManager is responsible for
 *
 * @author sahuero
 */
public final class EncryptionManager {
    private static final Logger logger = LoggerFactory.getLogger (EncryptionManager.class);

    /**
     * Debugging class identification
     */
    private static final String DIGEST_MODE = "SHA";


    // Do not change or else the monitor authentication will break!!!
    // server key
    private static byte[] SKEY = {(byte) 'a', (byte) '4', (byte) '5', (byte) '9',
                                 (byte) 'f', (byte) 'e', (byte) 'a', (byte) '2',
                                 (byte) '4', (byte) 'd', (byte) 'e', (byte) '1',
                                 (byte) '2', (byte) '0', (byte) '9', (byte) '3'};

    // client key
    private static byte[] CKEY = {(byte) '2', (byte) '3', (byte) 'f', (byte) '2',
                                  (byte) '3', (byte) 'd', (byte) 'a', (byte) '2',
                                  (byte) '3', (byte) '9', (byte) '3', (byte) '6',
                                  (byte) 'a', (byte) 'c', (byte) '4', (byte) '3'};

    /**/
    public static String encrypt(String s) throws CompositeSecurityException {
        return internalEncrypt(s, SKEY);
    }

    /**/
    public static String decrypt(String s) throws CompositeSecurityException {
        return internalDecrypt(s, SKEY);
    }

    /**/
    public static String clientEncrypt(String s) throws CompositeSecurityException {
        return internalEncrypt(s, CKEY);
    }

    /**/
    public static String clientDecrypt(String s) throws CompositeSecurityException {
        return internalDecrypt(s, CKEY);
    }
    
    private static String internalEncrypt(String s, byte[] key) throws CompositeSecurityException {
        if (s == null) {
            //TEAV can't encrypt null value
            return s;
        } else if (s.trim().equals("")) {
            //fixed bug 11697: there is an encryption problem that TEAV can't
            //encrypt empty string or string with spaces correctly.  It 
            //will not encrypt the string at all, but it will decrypt the
            //unaltered string with exception.  So we need to return the 
            //not encrypted string back.
            return s;
        }
        
        try {
            TEAV t = new TEAV(key);
            byte passwordBytes[] = s.getBytes("UTF8");
            int encoded[] = t.encode(passwordBytes, passwordBytes.length);
            return t.binToHex(encoded);
        } catch (Exception e) {
            throw new CompositeSecurityException(1900180, null, e);
        }
    }
    
    private static String internalDecrypt(String s, byte[] key) throws CompositeSecurityException {
        if (s == null) {
            //TEAV can't decrypt null value
            return s;
        } else if (s.trim().equals("")) {
            //fixed bug 11697: there is an encryption problem that TEAV can't
            //encrypt empty string or string with spaces correctly.  It 
            //will not encrypt the string at all, but it will decrypt the
            //unaltered string with exception.  So we need to return the 
            //not encrypted string back.
            return s;
        }
        
        try {
            TEAV t = new TEAV(key);
            int encoded[] = t.hexToBin(s);
            byte passwordBytes[] = t.decode(encoded);
            return new String(passwordBytes).trim();
        } catch (Exception e) {
            throw new CompositeSecurityException(1900190, null, e);
        }
    }

    /**/
    public static String getHex(byte b[]) {
        final char hex[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuffer r = new
                StringBuffer();

        for (int i = 0; i < b.length; i++) {
            int c = ((b[i]) >>> 4) & 0xf;
            r.append(hex[c]);
            c = ((int) b[i] & 0xf);
            r.append(hex[c]);
        }
        return r.toString();
    }

    /**/
    public static String hash(String s) throws CompositeSecurityException {
        byte[] buf = null;

        try {
            buf = s.getBytes("UTF8");
        } catch (Exception e) {
            logger.info(SecurityConstants.DOMAIN, 1900180, null, null);
            throw new CompositeSecurityException(1900180, null, e);
        }

        MessageDigest algorithm = null;
        try {
            algorithm = MessageDigest.getInstance(DIGEST_MODE);
        } catch (Exception e) {
            logger.info(SecurityConstants.DOMAIN, 1900180, null, null);
            throw new CompositeSecurityException(1900180, null, e);
        }
        algorithm.reset();
        algorithm.update(buf);
        byte[] digest = algorithm.digest();
        return getHex(digest);
    }


    public static boolean compareHashs(String s1, String s2) {
        if (s1.equalsIgnoreCase(s2) == true) {
            return true;
        }

        return false;
    }

    public static String decrypt(String encryptedPassword, String encryptedSessionKey, Key key) throws CompositeSecurityException{
        try{
            byte[] encryptedSessionKeyBytes = Base64.base64ToByteArray(encryptedSessionKey);
            byte[] encryptedPasswordBytes = Base64.base64ToByteArray(encryptedPassword);
            byte[] clearSessionKeyBytes = Decryption.asymmDecrypt(encryptedSessionKeyBytes, key);
            byte[] clearTextBytes = Decryption.symmDecrypt(encryptedPasswordBytes, clearSessionKeyBytes);
            return new String(clearTextBytes, "UTF-8");
        }catch(Exception e){
            throw new CompositeSecurityException(1900190, null, e);
        }

    }


}
