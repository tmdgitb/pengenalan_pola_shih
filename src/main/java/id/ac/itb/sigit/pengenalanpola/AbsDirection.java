package id.ac.itb.sigit.pengenalanpola;

/**
 * Standardized Absolute Freeman Chain Code of Eight Directions (FCCE) http://www.cs.mcgill.ca/~jsatta/pr644/tut644.html
 * See also: http://www.mind.ilstu.edu/curriculum/chain_codes_intro/chain_codes_intro.php
 * Created by ceefour on 16/10/2015.
 */
public enum AbsDirection {
    E((byte) 0, '→'),
    NE((byte) 1, '↗'),
    N((byte) 2, '↑'),
    NW((byte) 3, '↖'),
    W((byte) 4, '←'),
    SW((byte) 5, '↙'),
    S((byte) 6, '↓'),
    SE((byte) 7, '↘');

    private byte fcce;
    private char text;

    AbsDirection(byte fcce, char text) {
        this.fcce = fcce;
        this.text = text;
    }

    public byte getFcce() {
        return fcce;
    }

    public char getText() {
        return text;
    }

    @Override
    public String toString() {
        return String.valueOf(text);
    }
}
