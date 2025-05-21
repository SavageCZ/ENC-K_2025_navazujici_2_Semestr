package co.jeee.krypto_navazujici;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CryptoServiceImplTest {

    @Autowired
    private CryptoServiceImpl cryptoService;

    @Test
    void hashAndVerify_shouldWorkCorrectly() {
        String raw = "mojeHeslo123";
        String hash = cryptoService.hash(raw);

        assertNotNull(hash);
        assertTrue(cryptoService.verify(raw, hash));
        assertFalse(cryptoService.verify("špatnéHeslo", hash));
    }

    @Test
    void encryptAndDecrypt_shouldWorkCorrectly() throws Exception {
        String text = "tajná zpráva";

        String encrypted = cryptoService.encrypt(text);
        assertNotNull(encrypted);
        assertNotEquals(text, encrypted);

        String decrypted = cryptoService.decrypt(encrypted);
        assertEquals(text, decrypted);
    }
}