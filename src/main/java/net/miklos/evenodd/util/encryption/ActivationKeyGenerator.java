package net.miklos.evenodd.util.encryption;
import java.security.SecureRandom;

/**
 * Utility class used for generating random activation keys.
 */
public class ActivationKeyGenerator {

    /** Array of possible characters in the user activation key. */
    public static final char[] ACTIVATION_KEY_CHARACTERS = "QWERTZUIOPLKJHGFDSAYXCVBNMqwertzuiolkjhgfdsayxcvbnm1234567890"
            .toCharArray();

    /** Default activation key length. */
    public static final int DEFAULT_LENGTH = 64;

    /**
     * Generates a random activation key with size as specified.
     *
     * @param size
     *            random activation key size
     * @return random activation key
     */
    public static String generate(int size) {
        int arrayLength = ACTIVATION_KEY_CHARACTERS.length;

        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < size; i++)
            sb.append(ACTIVATION_KEY_CHARACTERS[random.nextInt(arrayLength)]);

        return sb.toString();
    }

    /**
     * Generates a random activation key with the default size.
     *
     * @return random activation key
     */
    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }

}
