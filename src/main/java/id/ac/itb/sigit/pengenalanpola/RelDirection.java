package id.ac.itb.sigit.pengenalanpola;

/**
 * Relative direction, using <strong>Directional Freeman Chain Code of Eigth Directions (DFCCE)</strong>,
 * described in <a href="">Y.K. Liu, B. Zalik, An efficient chain code with Huffman coding, Pattern
 Recognition 38 (4) (2005) 553–557</a>.
 * See also: http://www.mind.ilstu.edu/curriculum/chain_codes_intro/chain_codes_intro.php
 * Created by ceefour on 16/10/2015.
 */
public enum RelDirection {
    F((byte) 0, '↥'),
    FL((byte) 1, '↖'),
    FR((byte) 2, '↗'),
    L((byte) 3, '↰'),
    R((byte) 4, '↱'),
    BL((byte) 5, '↙'),
    BR((byte) 6, '↘'),
    B((byte) 7, '↧');

    private byte dfcce;
    private char text;

    RelDirection(byte dfcce, char text) {
        this.dfcce = dfcce;
        this.text = text;
    }

    public byte getDfcce() {
        return dfcce;
    }

    public char getText() {
        return text;
    }

    @Override
    public String toString() {
        return String.valueOf(text);
    }
}
