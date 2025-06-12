package co.jeee.krypto_navazujici.aes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AESServiceTest {

    private AESServiceImpl aesService;

    @BeforeEach
    void setUp() {
        aesService = new AESServiceImpl();
    }

    @Test
    void encrypt_shouldReturnEncryptedBytes_whenValidInputAndKeyProvided() {
       byte[] input = "tajnyzpravatest1".getBytes(); // exactly 16 bytes
        byte[] key = "1234567890abcdef".getBytes(); // exactly 16 bytes

        byte[] result = aesService.encrypt(input, key);

        assertNotNull(result);
        assertEquals(16, result.length); // AES output block size
    }

    @Test
    void encrypt_shouldThrowException_whenInvalidInputLength() {
        byte[] input = "short".getBytes();
        byte[] key = "1234567890abcdef".getBytes();

        assertThrows(IllegalArgumentException.class, () -> aesService.encrypt(input, key));
    }

    @Test
    void encrypt_shouldThrowException_whenInvalidKeyLength() {
        byte[] input = "tajnyzpravatest".getBytes(); // 16 bytes
        byte[] key = "short".getBytes(); // too short

        assertThrows(IllegalArgumentException.class, () -> aesService.encrypt(input, key));
    }

    @Test
    void decrypt_shouldReturnOriginalInput_whenEncryptedAndDecrypted() {
        byte[] input = "tajnyzpravatest1".getBytes(); // 16 bytes
        byte[] key = "1234567890abcdef".getBytes(); // 16 bytes

        byte[] encrypted = aesService.encrypt(input, key);
        byte[] decrypted = aesService.decrypt(encrypted, key);

        assertNotNull(decrypted);
        assertArrayEquals(input, decrypted);
    }
}
