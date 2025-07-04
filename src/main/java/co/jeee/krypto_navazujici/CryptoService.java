package co.jeee.krypto_navazujici;


import co.jeee.krypto_navazujici.model.CryptoOperation;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CryptoService {

    @NonNull
    String hash(@NonNull String raw);

    boolean verify(@NonNull String raw, @NonNull String hash);

    @NonNull
    String encrypt(@NonNull String text, @NonNull byte[] key) throws Exception;

    @NonNull
    String decrypt(@NonNull String encrypted, @NonNull byte[] key) throws Exception;

    List<CryptoOperation> getHistory();
}
