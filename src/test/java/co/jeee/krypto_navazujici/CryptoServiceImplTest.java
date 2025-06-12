package co.jeee.krypto_navazujici;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

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
        String text = "tajnyzpravatest1"; // přesně 16 znaků
        String keyStr = "1234567890abcdef";
        byte[] key = keyStr.getBytes(StandardCharsets.UTF_8);

        assertEquals(16, key.length, "Klíč musí být 16 bajtů dlouhý pro AES");

        byte[] inputBytes = text.getBytes(StandardCharsets.UTF_8);
        assertEquals(16, inputBytes.length, "Vstupní text musí být přesně 16 bajtů pro AES");

        String encrypted = cryptoService.encrypt(text, key);
        assertNotNull(encrypted, "Výsledek šifrování nesmí být null");
        assertNotEquals(text, encrypted, "Zašifrovaný text nesmí být shodný s původním");

        String decrypted = cryptoService.decrypt(encrypted, key);
        assertEquals(text, decrypted, "Dešifrovaný text musí odpovídat původnímu");
    }
}