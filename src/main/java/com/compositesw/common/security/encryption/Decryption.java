package com.compositesw.common.security.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * the class to decrypt password encrypted by public key
 * User: ttong
 * Date: 11-8-29
 * Since: 6.0.1
 */
public class Decryption {

    public static byte[] symmDecrypt(byte[] cipherData, byte[] keyBytes)throws Exception{
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "RC4");
        Cipher cipher1 = Cipher.getInstance("RC4/ECB/NOPADDING");
		cipher1.init(Cipher.DECRYPT_MODE, keySpec);
		return cipher1.doFinal(cipherData);
    }

    public static byte[] asymmDecrypt(byte[] cipherData, Key privateKey)throws Exception{
        Cipher cipher1 = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
		cipher1.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher1.doFinal(cipherData);
    }
}
