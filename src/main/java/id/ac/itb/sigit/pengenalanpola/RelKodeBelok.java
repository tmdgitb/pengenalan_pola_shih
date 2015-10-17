package id.ac.itb.sigit.pengenalanpola;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sigit on 17/10/2015.
 */
public class RelKodeBelok {
    final List<RelDirection> dirs;

    public RelKodeBelok(List<RelDirection> dirs) {
        this.dirs = dirs;
    }

    public List<RelDirection> getDirs() {
        return dirs;
    }

    public String getFcce() {
        return dirs.stream().map(it -> Byte.toString(it.getDfcce())).collect(Collectors.joining());
    }

    public String getText() {
        return dirs.stream().map(it -> Character.toString(it.getText())).collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return getText();
    }
}
