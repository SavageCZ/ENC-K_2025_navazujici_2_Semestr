package co.jeee.krypto_navazujici;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CryptoController {

    private final PasswordEncoder encoder = new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
    private SecretKey aesKey;
    private byte[] iv;

    @PostConstruct
    public void init() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        aesKey = keyGen.generateKey();
        iv = new byte[12];
        new SecureRandom().nextBytes(iv);
    }

    @PostMapping("/hash")
    public Map<String, String> hash(@RequestBody Map<String, String> req) {
        String raw = req.get("raw");
        return Map.of("hash", encoder.encode(raw));
    }

    @PostMapping("/verify")
    public Map<String, Boolean> verify(@RequestBody Map<String, String> req) {
        return Map.of("valid", encoder.matches(req.get("raw"), req.get("hash")));
    }

    @PostMapping("/encrypt")
    public Map<String, String> encrypt(@RequestBody Map<String, String> req) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
        byte[] encrypted = cipher.doFinal(req.get("text").getBytes(StandardCharsets.UTF_8));
        byte[] all = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, all, 0, iv.length);
        System.arraycopy(encrypted, 0, all, iv.length, encrypted.length);
        return Map.of("encrypted", Base64.getEncoder().encodeToString(all));
    }

    @PostMapping("/decrypt")
    public Map<String, String> decrypt(@RequestBody Map<String, String> req) throws Exception {
        byte[] all = Base64.getDecoder().decode(req.get("encrypted"));
        byte[] localIv = new byte[12];
        byte[] cipherText = new byte[all.length - 12];
        System.arraycopy(all, 0, localIv, 0, 12);
        System.arraycopy(all, 12, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, localIv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
        byte[] decrypted = cipher.doFinal(cipherText);
        return Map.of("decrypted", new String(decrypted, StandardCharsets.UTF_8));
    }
}