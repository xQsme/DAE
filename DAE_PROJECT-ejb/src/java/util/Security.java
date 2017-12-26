package util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Security {

    public static final String hash(String plaintext, String algorithm) {
        try {
            ByteBuffer buffer = Charset.defaultCharset().encode(CharBuffer.wrap(plaintext));
            byte[] passwdBytes = buffer.array();
            
            MessageDigest mdEnc = MessageDigest.getInstance(algorithm);
            mdEnc.update(passwdBytes, 0, plaintext.toCharArray().length);
            
            char[] encrypted = new BigInteger(1, mdEnc.digest()).toString(16).toCharArray();
            return new String(encrypted);
        } catch (NoSuchAlgorithmException | NullPointerException e) {
            Logger.getLogger("util.security").log(Level.SEVERE, null, e);
            return null;
        }
    }
    
    public static final String hashPassword(String password) {
        return hash(password, "SHA-256");
    }
}
