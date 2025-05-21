package co.jeee.krypto_navazujici.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CryptoOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String operation;
    private String input;
    private String result;
    private LocalDateTime timestamp;

    public CryptoOperation() {}

    public CryptoOperation(String operation, String input, String result, LocalDateTime timestamp) {
        this.operation = operation;
        this.input = input;
        this.result = result;
        this.timestamp = timestamp;
    }
}