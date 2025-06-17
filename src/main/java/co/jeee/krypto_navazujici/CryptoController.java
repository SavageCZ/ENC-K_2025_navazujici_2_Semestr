package co.jeee.krypto_navazujici;

import co.jeee.krypto_navazujici.model.CryptoOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CryptoController {

    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @PostMapping("/hash")
    public Map<String, String> hash(@RequestBody Map<String, String> req) {
        return Map.of("hash", cryptoService.hash(req.get("raw")));
    }

    @PostMapping("/verify")
    public Map<String, Boolean> verify(@RequestBody Map<String, String> req) {
        return Map.of("valid", cryptoService.verify(req.get("raw"), req.get("hash")));
    }

    @PostMapping("/encrypt")
    public Map<String, String> encrypt(@RequestBody Map<String, String> req) throws Exception {
        String text = req.get("text");
        String key = req.get("key");
        if (text == null || key == null) {
            throw new IllegalArgumentException("Text nebo klíč nesmí být null.");
        }
        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        if (textBytes.length != 16 || keyBytes.length != 16) {
            throw new IllegalArgumentException("AES podporuje pouze 16-bajtový vstup a klíč.");
        }

        String encrypted = cryptoService.encrypt(text, keyBytes);
        return Map.of("encrypted", encrypted);
    }

    @PostMapping("/decrypt")
    public Map<String, String> decrypt(@RequestBody Map<String, String> req) throws Exception {
        System.out.println("Přijaté klíče v requestu: " + req.keySet());
        if (!req.containsKey("ciphertext") || !req.containsKey("key")) {
            throw new IllegalArgumentException("Tělo požadavku musí obsahovat 'ciphertext' a 'key'.");
        }

        String encryptedHex = req.get("ciphertext");
        String key = req.get("key");

        if (encryptedHex == null || encryptedHex.trim().isEmpty() || key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Ciphertext nebo klíč nesmí být null nebo prázdný.");
        }

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException("AES vyžaduje 16-bajtový klíč.");
        }

        String decrypted = cryptoService.decrypt(encryptedHex, keyBytes);
        return Map.of("decrypted", decrypted);
    }

    @GetMapping("/history")
    public List<CryptoOperation> getHistory() {
        return cryptoService.getHistory();
    }
}