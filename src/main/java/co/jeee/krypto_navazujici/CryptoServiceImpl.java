package co.jeee.krypto_navazujici;

import jakarta.annotation.PostConstruct;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoServiceImpl implements CryptoService {

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

    @NonNull
    @Override
    public String hash(@NonNull String raw) {
        return encoder.encode(raw);
    }

    @Override
    public boolean verify(@NonNull String raw, @NonNull String hash) {
        return encoder.matches(raw, hash);
    }

    @NonNull
    @Override
    public String encrypt(@NonNull String text) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        byte[] all = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, all, 0, iv.length);
        System.arraycopy(encrypted, 0, all, iv.length, encrypted.length);
        return Base64.getEncoder().encodeToString(all);
    }

    @NonNull
    @Override
    public String decrypt(@NonNull String encrypted) throws Exception {
        byte[] all = Base64.getDecoder().decode(encrypted);
        byte[] localIv = new byte[12];
        byte[] cipherText = new byte[all.length - 12];
        System.arraycopy(all, 0, localIv, 0, 12);
        System.arraycopy(all, 12, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, localIv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
        byte[] decrypted = cipher.doFinal(cipherText);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}