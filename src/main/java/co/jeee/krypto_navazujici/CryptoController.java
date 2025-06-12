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
        String encrypted = cryptoService.encrypt(text, key.getBytes());
        return Map.of("encrypted", encrypted);
    }

    @PostMapping("/decrypt")
    public Map<String, String> decrypt(@RequestBody Map<String, String> req) throws Exception {
        String encryptedHex = req.get("encrypted");
        String key = req.get("key");
        String decrypted = cryptoService.decrypt(encryptedHex, key.getBytes());
        return Map.of("decrypted", decrypted);
    }

    @GetMapping("/history")
    public List<CryptoOperation> getHistory() {
        return cryptoService.getHistory();
    }
}