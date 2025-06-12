package co.jeee.krypto_navazujici;

import co.jeee.krypto_navazujici.aes.AESService;
import co.jeee.krypto_navazujici.aes.AESUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.data.domain.Sort;

import co.jeee.krypto_navazujici.repository.CryptoOperationRepository;
import co.jeee.krypto_navazujici.model.CryptoOperation;

@Service
public class CryptoServiceImpl implements CryptoService {

    private final PasswordEncoder encoder = new Argon2PasswordEncoder(16, 32, 1, 8192, 2);

    private final CryptoOperationRepository operationRepo;
    private final AESService aesService;

    public CryptoServiceImpl(CryptoOperationRepository operationRepo, AESService aesService) {
        this.operationRepo = operationRepo;
        this.aesService = aesService;
    }

    @NonNull
    @Override
    public String hash(@NonNull String raw) {
        String hashed = encoder.encode(raw);
        operationRepo.save(new CryptoOperation("HASH", raw, hashed, java.time.LocalDateTime.now()));
        return hashed;
    }

    @Override
    public boolean verify(@NonNull String raw, @NonNull String hash) {
        boolean result = encoder.matches(raw, hash);
        operationRepo.save(new CryptoOperation("VERIFY", raw, String.valueOf(result), java.time.LocalDateTime.now()));
        return result;
    }

    @NonNull
    @Override
    public String encrypt(@NonNull String text, @NonNull byte[] key) throws Exception {
        byte[] inputBytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = aesService.encrypt(inputBytes, key);
        String hex = AESUtils.toHex(encrypted);
        operationRepo.save(new CryptoOperation("ENCRYPT", text, hex, java.time.LocalDateTime.now()));
        return hex;
    }

    @NonNull
    @Override
    public String decrypt(@NonNull String encrypted, @NonNull byte[] key) throws Exception {
        byte[] decoded = AESUtils.fromHex(encrypted);
        byte[] decrypted = aesService.decrypt(decoded, key);
        String decryptedText = new String(decrypted, StandardCharsets.UTF_8);
        operationRepo.save(new CryptoOperation("DECRYPT", encrypted, decryptedText, java.time.LocalDateTime.now()));
        return decryptedText;
    }

    @Override
    public List<CryptoOperation> getHistory() {
        return operationRepo.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
}