package co.jeee.krypto_navazujici.aes;

import co.jeee.krypto_navazujici.aes.constants.SBox;
import org.springframework.stereotype.Service;

@Service
public class AESServiceImpl implements AESService {

    /**
     * Provádí substituci bajtů v matici AES.
     * Každý bajt je nahrazen jiným hodnotou podle předem daného pole S-box.
     * Toto pole je pevně definované v normě AES (není náhodné).
     * Zajišťuje nelinearitu a zvyšuje bezpečnost algoritmu.
     *
     * @param state 4x4 matice stavových bajtů AES
     * @return nová 4x4 matice se substituovanými bajty
     */
    public byte[][] subBytes(byte[][] state) {
        byte[][] result = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int unsigned = state[row][col] & 0xFF;
                result[row][col] = SBox.SBOX[unsigned];
            }
        }
        return result;
    }

    /**
     * Posunuje řádky matice AES doleva.
     * První řádek zůstává nezměněný, druhý se posune o 1, třetí o 2 atd.
     * Cílem je rozptýlit (diffuse) hodnoty přes matici.
     *
     * @param state 4x4 matice
     * @return matice s posunutými řádky
     */
    public byte[][] shiftRows(byte[][] state) {
        byte[][] result = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[row][col] = state[row][(col + row) % 4];
            }
        }
        return result;
    }

    /**
     * Míchá sloupce matice AES pomocí lineární transformace v tzv. Galoisově tělese.
     * Každý sloupec se chápe jako polynom a násobí se předem daným polynomem:
     * {03}x^3 + {01}x^2 + {01}x + {02}.
     * Hodnoty jako 0x02 a 0x03 nejsou náhodné – jsou definovány normou AES.
     * Tato operace zajišťuje další rozptýlení (difúzi) bajtů.
     *
     * @param state vstupní 4x4 matice
     * @return výstupní 4x4 matice s promíchanými sloupci
     */
    public byte[][] mixColumns(byte[][] state) {
        byte[][] result = new byte[4][4];

        for (int col = 0; col < 4; col++) {
            result[0][col] = (byte) (gmul(0x02, state[0][col]) ^ gmul(0x03, state[1][col]) ^ state[2][col] ^ state[3][col]);
            result[1][col] = (byte) (state[0][col] ^ gmul(0x02, state[1][col]) ^ gmul(0x03, state[2][col]) ^ state[3][col]);
            result[2][col] = (byte) (state[0][col] ^ state[1][col] ^ gmul(0x02, state[2][col]) ^ gmul(0x03, state[3][col]));
            result[3][col] = (byte) (gmul(0x03, state[0][col]) ^ state[1][col] ^ state[2][col] ^ gmul(0x02, state[3][col]));
        }

        return result;
    }

    /**
     * Provádí násobení dvou bajtů v tzv. Galoisově tělese GF(2^8),
     * kde se všechny operace dělají s přetékáním po 8 bitech (mod 256).
     * Používá se v metodě mixColumns pro násobení s konstantami (např. 0x02, 0x03).
     * Používá se ireducibilní polynom: x^8 + x^4 + x^3 + x + 1 (což odpovídá hex 0x11B).
     *
     * @param a konstanta (např. 0x02 nebo 0x03)
     * @param b bajt z matice AES
     * @return výsledek násobení v Galoisově tělese
     */
    private byte gmul(int a, byte b) {
        int result = 0;
        int value = b & 0xFF;
        for (int i = 0; i < 8; i++) {
            if ((a & 1) != 0) {
                result ^= value;
            }
            boolean hiBitSet = (value & 0x80) != 0;
            value = (value << 1) & 0xFF;
            if (hiBitSet) {
                value ^= 0x1B;
            }
            a >>>= 1;
        }
        return (byte) result;
    }

    /**
     * Provádí operaci AddRoundKey – každý bajt matice se XORuje s příslušným bajtem klíče.
     * Tato operace se používá na začátku a na konci každého kola v AES.
     *
     * XOR operace (^) je reverzibilní – proto je ideální pro šifrování i dešifrování.
     *
     * @param state 4x4 matice aktuálního stavu
     * @param roundKey 4x4 matice klíče pro dané kolo
     * @return nová 4x4 matice po aplikaci XOR s kulovým klíčem
     */
    public byte[][] addRoundKey(byte[][] state, byte[][] roundKey) {
        byte[][] result = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[row][col] = (byte) (state[row][col] ^ roundKey[row][col]);
            }
        }
        return result;
    }

    /**
     * Provede šifrování vstupního 16-bajtového plaintextu pomocí 16-bajtového klíče.
     * Využívá jednotlivé kroky AES: AddRoundKey, SubBytes, ShiftRows, MixColumns.
     * Počet kol je fixně 10 (AES-128).
     *
     * @param plaintext vstupní data (16 bajtů)
     * @param key šifrovací klíč (16 bajtů)
     * @return šifrovaná data (16 bajtů)
     */
    @Override
    public byte[] encrypt(byte[] plaintext, byte[] key) {
        if (plaintext.length != 16 || key.length != 16) {
            throw new IllegalArgumentException("AES podporuje pouze 16-bajtový vstup a klíč.");
        }

        AESState state = new AESState(plaintext);
        byte[][][] roundKeys = KeyExpansion.expandKey(key); // 11 klíčů po 4x4 bajtech

        state.setState(addRoundKey(state.getState(), roundKeys[0]));

        for (int round = 1; round <= 9; round++) {
            state.setState(subBytes(state.getState()));
            state.setState(shiftRows(state.getState()));
            state.setState(mixColumns(state.getState()));
            state.setState(addRoundKey(state.getState(), roundKeys[round]));
        }

        state.setState(subBytes(state.getState()));
        state.setState(shiftRows(state.getState()));
        state.setState(addRoundKey(state.getState(), roundKeys[10]));

        return state.toByteArray();
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] key) {
        if (ciphertext.length != 16 || key.length != 16) {
            throw new IllegalArgumentException("AES podporuje pouze 16-bajtový vstup a klíč.");
        }

        AESState state = new AESState(ciphertext);
        byte[][][] roundKeys = KeyExpansion.expandKey(key);

        // Začíná posledním kolem: přidání kulového klíče, shiftRows a subBytes
        state.setState(addRoundKey(state.getState(), roundKeys[10]));
        state.setState(invShiftRows(state.getState()));
        state.setState(invSubBytes(state.getState()));

        // 9 mezikoul – invMixColumns, invShiftRows, invSubBytes
        for (int round = 9; round >= 1; round--) {
            state.setState(addRoundKey(state.getState(), roundKeys[round]));
            state.setState(invMixColumns(state.getState()));
            state.setState(invShiftRows(state.getState()));
            state.setState(invSubBytes(state.getState()));
        }

        // Finální AddRoundKey
        state.setState(addRoundKey(state.getState(), roundKeys[0]));

        return state.toByteArray();
    }

    private byte[][] invSubBytes(byte[][] state) {
        byte[][] result = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int unsigned = state[row][col] & 0xFF;
                result[row][col] = SBox.INV_SBOX[unsigned];
            }
        }
        return result;
    }

    private byte[][] invShiftRows(byte[][] state) {
        byte[][] result = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                result[row][col] = state[row][(col - row + 4) % 4];
            }
        }
        return result;
    }

    private byte[][] invMixColumns(byte[][] state) {
        byte[][] result = new byte[4][4];

        for (int col = 0; col < 4; col++) {
            result[0][col] = (byte) (gmul(0x0e, state[0][col]) ^ gmul(0x0b, state[1][col]) ^ gmul(0x0d, state[2][col]) ^ gmul(0x09, state[3][col]));
            result[1][col] = (byte) (gmul(0x09, state[0][col]) ^ gmul(0x0e, state[1][col]) ^ gmul(0x0b, state[2][col]) ^ gmul(0x0d, state[3][col]));
            result[2][col] = (byte) (gmul(0x0d, state[0][col]) ^ gmul(0x09, state[1][col]) ^ gmul(0x0e, state[2][col]) ^ gmul(0x0b, state[3][col]));
            result[3][col] = (byte) (gmul(0x0b, state[0][col]) ^ gmul(0x0d, state[1][col]) ^ gmul(0x09, state[2][col]) ^ gmul(0x0e, state[3][col]));
        }

        return result;
    }
}