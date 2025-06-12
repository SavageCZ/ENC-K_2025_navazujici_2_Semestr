package co.jeee.krypto_navazujici.aes;

import lombok.Getter;

/**
 * Třída reprezentující stavovou matici AES (4x4 bajty).
 * Umožňuje převod z a do jednorozměrného pole bajtů.
 */
@Getter
public class AESState {

    /**
     * -- GETTER --
     *  Vrací aktuální stavovou matici.
     *
     */
    private byte[][] state;

    /**
     * Inicializuje stavovou matici z pole 16 bajtů (sloupcově).
     * @param input vstupní pole o délce 16 bajtů
     */
    public AESState(byte[] input) {
        if (input.length != 16) {
            throw new IllegalArgumentException("AESState vyžaduje přesně 16 bajtů.");
        }
        state = new byte[4][4];
        for (int i = 0; i < 16; i++) {
            state[i % 4][i / 4] = input[i];  // sloupcově
        }
    }

    /**
     * Nastavuje novou stavovou matici.
     * @param newState 4x4 matice bajtů
     */
    public void setState(byte[][] newState) {
        if (newState.length != 4 || newState[0].length != 4) {
            throw new IllegalArgumentException("Stav musí být 4x4 matice.");
        }
        this.state = newState;
    }

    /**
     * Vrací aktuální stav jako jednorozměrné pole bajtů (sloupcově).
     * @return pole bajtů o délce 16
     */
    public byte[] toByteArray() {
        byte[] output = new byte[16];
        for (int i = 0; i < 16; i++) {
            output[i] = state[i % 4][i / 4];
        }
        return output;
    }
}
