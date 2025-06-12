package co.jeee.krypto_navazujici.aes.constants;

/**
 * Třída obsahující konstanty Rcon používané při rozšiřování klíče v AES.
 * Hodnoty jsou z tabulky Rijndael key schedule Rcon.
 */
public class Rcon {

    public static final byte[] RCON = {
            (byte) 0x00, // RCON[0] není používán
            (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08,
            (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80,
            (byte) 0x1B, (byte) 0x36
    };
}
