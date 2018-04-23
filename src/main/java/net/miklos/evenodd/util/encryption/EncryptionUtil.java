package net.miklos.evenodd.util.encryption;

import java.security.Key;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

/**
 * Utility class holding methods used for decryption
 */
public class EncryptionUtil {
    /**
     *
     * @param data      encrypted data to be decrypted
     * @param key       AES key used to decrypt
     * @param ivBytes   Initialization vector used in conjunction with the key
     * @return          decrypted result
     */
    public static byte[] decryptAES(byte[] data, Key key, byte[] ivBytes) {
        byte[] result = null;
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
            c.init(Cipher.DECRYPT_MODE, key, ivSpec);
            result = c.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
