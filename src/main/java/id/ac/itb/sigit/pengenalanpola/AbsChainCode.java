package id.ac.itb.sigit.pengenalanpola;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ceefour on 16/10/2015.
 */
public class AbsChainCode {

    final List<AbsDirection> dirs;

    public AbsChainCode(List<AbsDirection> dirs) {
        this.dirs = dirs;
    }

    public List<AbsDirection> getDirs() {
        return dirs;
    }

    public String getFcce() {
        return dirs.stream().map(it -> Byte.toString(it.getFcce())).collect(Collectors.joining());
    }

    public String getText() {
        return dirs.stream().map(it -> Character.toString(it.getText())).collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return getText();
    }
}
