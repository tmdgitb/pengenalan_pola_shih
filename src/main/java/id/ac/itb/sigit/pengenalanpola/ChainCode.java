package id.ac.itb.sigit.pengenalanpola;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 03/10/2015.
 */
public class ChainCode {
    private String character;
    private String chainCode;
    private String kodeBelok;
    private List<ChainCode> subChainCode=new ArrayList<>();

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
}
