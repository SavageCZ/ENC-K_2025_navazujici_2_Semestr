package co.jeee.krypto_navazujici.aes;

public class AESUtils {

    /**
     * Převádí pole bajtů na hexadecimální řetězec (např. 0A1B2C...).
     *
     * @param bytes vstupní pole bajtů
     * @return hex řetězec
     */
    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * Převádí hexadecimální řetězec na pole bajtů.
     *
     * @param hex vstupní hex řetězec
     * @return pole bajtů
     */
    public static byte[] fromHex(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
}
