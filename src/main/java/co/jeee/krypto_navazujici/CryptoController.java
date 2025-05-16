package co.jeee.krypto_navazujici;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return Map.of("encrypted", cryptoService.encrypt(req.get("text")));
    }

    @PostMapping("/decrypt")
    public Map<String, String> decrypt(@RequestBody Map<String, String> req) throws Exception {
        return Map.of("decrypted", cryptoService.decrypt(req.get("encrypted")));
    }
}