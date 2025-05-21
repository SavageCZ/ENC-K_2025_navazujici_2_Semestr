package co.jeee.krypto_navazujici;

import co.jeee.krypto_navazujici.model.CryptoOperation;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

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
        return Map.of("encrypted", cryptoService.encrypt(req.get("text")));
    }

    @PostMapping("/decrypt")
    public Map<String, String> decrypt(@RequestBody Map<String, String> req) throws Exception {
        return Map.of("decrypted", cryptoService.decrypt(req.get("encrypted")));
    }

    @GetMapping("/history")
    public List<CryptoOperation> getHistory() {
        return cryptoService.getHistory();
    }
}