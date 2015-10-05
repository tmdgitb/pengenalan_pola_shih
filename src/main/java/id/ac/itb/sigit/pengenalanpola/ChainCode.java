package id.ac.itb.sigit.pengenalanpola;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 03/10/2015.
 */
public class ChainCode {
    private String character;
    private String chainCode;
    private String kodeBelok;
    private List<ChainCode> subChainCode = new ArrayList<>();
    private Integer x;
    private Integer y;

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getChainCode() {
        return chainCode;
    }

    public void setChainCode(String chainCode) {
        this.chainCode = chainCode;
    }

    public String getKodeBelok() {
        return kodeBelok;
    }

    public void setKodeBelok(String kodeBelok) {
        this.kodeBelok = kodeBelok;
    }

    public List<ChainCode> getSubChainCode() {
        return subChainCode;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    /**
     * Resample a chain code to desired length.
     * @param length
     * @return
     */
    public static String resample(String source, int length) {
        String target = "";
        for (int i = 0; i < length; i++) {
            final char chr = source.charAt(i * source.length() / length);
            target += chr;
        }
        return target;
    }

    /**
     *
     * @param actual
     * @param training
     * @return Confidence (0..1).
     */
    public static double match(String actual, String training) {
        Preconditions.checkArgument(actual.length() == training.length(),
                "Actual is %s characters must match training of %s characters");
        int matchedCount = 0;
        for (int i = 0; i < actual.length(); i++) {
            if (actual.charAt(i) == training.charAt(i)) {
                matchedCount++;
            }
        }
        return matchedCount * 1.0 / actual.length();
    }
}
