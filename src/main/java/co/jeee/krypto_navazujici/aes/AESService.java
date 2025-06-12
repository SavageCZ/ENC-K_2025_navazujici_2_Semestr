package co.jeee.krypto_navazujici.aes;

public interface AESService {
    /**
     * Provede šifrování vstupního 16-bajtového bloku pomocí 16-bajtového klíče.
     * Používá algoritmus AES-128 s 10 koly.
     *
     * @param plaintext vstupní data o délce 16 bajtů
     * @param key šifrovací klíč o délce 16 bajtů
     * @return šifrovaný blok o délce 16 bajtů
     */
    byte[] encrypt(byte[] plaintext, byte[] key);

    /**
     * Provede dešifrování 16b AES bloku pomocí zadaného klíče.
     *
     * @param ciphertext šifrovaná data (16 bajtů)
     * @param key klíč (16 bajtů)
     * @return dešifrovaná data (16 bajtů)
     */
    byte[] decrypt(byte[] ciphertext, byte[] key);
}
