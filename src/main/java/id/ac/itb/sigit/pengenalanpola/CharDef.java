package id.ac.itb.sigit.pengenalanpola;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 22/09/2015.
 */
public class CharDef {
    private String character;
    private String chainCode;
    private List<String> subChainCode=new ArrayList<>();

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

    public List<String> getSubChainCode() {
        return subChainCode;
    }


}
