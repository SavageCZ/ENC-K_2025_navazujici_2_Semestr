package co.jeee.krypto_navazujici.repository;

import co.jeee.krypto_navazujici.model.CryptoOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoOperationRepository extends JpaRepository<CryptoOperation, Long> {

}