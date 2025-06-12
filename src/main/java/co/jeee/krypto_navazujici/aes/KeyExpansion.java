package co.jeee.krypto_navazujici.aes;

import co.jeee.krypto_navazujici.aes.constants.Rcon;
import co.jeee.krypto_navazujici.aes.constants.SBox;

public class KeyExpansion {

    /**
     * Rozšiřuje původní 16-bajtový klíč na 11 klíčů (každý 4x4 bajty) pro AES-128.
     *
     * @param key vstupní 16-bajtový klíč
     * @return pole 11 kulových klíčů, každý jako 4x4 bajtová matice
     */
    public static byte[][][] expandKey(byte[] key) {
        int rounds = 11;
        byte[][][] roundKeys = new byte[rounds][4][4]; // 11 klíčů

        byte[] expanded = new byte[rounds * 16]; // 176 bajtů (11*16)
        System.arraycopy(key, 0, expanded, 0, 16); // první blok

        int bytesGenerated = 16;
        int rconIteration = 1;
        byte[] temp = new byte[4];

        while (bytesGenerated < expanded.length) {
            System.arraycopy(expanded, bytesGenerated - 4, temp, 0, 4);

            if (bytesGenerated % 16 == 0) {
                temp = scheduleCore(temp, rconIteration++);
            }

            for (int i = 0; i < 4; i++) {
                expanded[bytesGenerated] = (byte) (expanded[bytesGenerated - 16] ^ temp[i]);
                bytesGenerated++;
            }
        }

        for (int i = 0; i < rounds; i++) {
            for (int col = 0; col < 4; col++) {
                for (int row = 0; row < 4; row++) {
                    roundKeys[i][row][col] = expanded[i * 16 + col * 4 + row];
                }
            }
        }

        return roundKeys;
    }

    /**
     * Provádí tzv. key schedule core – rotace, substituce, přidání Rcon.
     */
    private static byte[] scheduleCore(byte[] word, int iteration) {
        // rotace vlevo
        byte temp = word[0];
        word[0] = word[1];
        word[1] = word[2];
        word[2] = word[3];
        word[3] = temp;

        // S-Box substituce
        for (int i = 0; i < 4; i++) {
            word[i] = SBox.SBOX[word[i] & 0xFF];
        }

        // Přidání Rcon
        word[0] ^= Rcon.RCON[iteration];

        return word;
    }
}
