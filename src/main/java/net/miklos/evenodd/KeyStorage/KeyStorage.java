package net.miklos.evenodd.KeyStorage;

import net.miklos.evenodd.util.encryption.RSAUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;



/**
 * Singleton used for storing server's public and private key.
 *
 */
public class KeyStorage {

    /**
     * This object's only instance.
     */
    private static KeyStorage instance = new KeyStorage();

    /**
     * Server's key pair.
     */
    private KeyPair pair;

    /**
     * Instantiates new Key Storage object and creates server's key pair.
     */
    private KeyStorage() {
        try {
            pair = RSAUtils.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not create keys! Nested exception is " + e.getClass() + ": " + e.getMessage());
        }
    }

    /**
     * Gets KeyStorage instance.
     *
     * @return
     */
    public static KeyStorage getInstance() {
        return instance;
    }

    /**
     * Gets the server's public key.
     *
     * @return server's public key
     */
    public PublicKey getPublic() {
        return pair.getPublic();
    }

    /**
     * Gets the server's private key.
     *
     * @return server's private key
     */
    public PrivateKey getPrivate() {
        return pair.getPrivate();
    }

}
