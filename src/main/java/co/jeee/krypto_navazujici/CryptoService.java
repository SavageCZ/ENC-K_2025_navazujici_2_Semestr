package co.jeee.krypto_navazujici;


import org.springframework.lang.NonNull;

public interface CryptoService {

    @NonNull
    String hash(@NonNull String raw);

    boolean verify(@NonNull String raw, @NonNull String hash);

    @NonNull
    String encrypt(@NonNull String text) throws Exception;

    @NonNull
    String decrypt(@NonNull String encrypted) throws Exception;
}
