package id.ac.itb.sigit.pengenalanpola;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sigit on 17/10/2015.
 */
public class RelKodeBelok implements Serializable {
    final List<RelDirection> dirs;
    List<RelDirection> newDirs;

    public RelKodeBelok(List<RelDirection> dirs) {
        this.dirs = dirs;
        this.newDirs=penyederhanaanKodeBelok(dirs);
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

    private List<RelDirection> penyederhanaanKodeBelok(List<RelDirection> kodeBelok)
    {
        List<RelDirection> newKodeBelok=new ArrayList<>();
        newKodeBelok.add(kodeBelok.get(0));
        newKodeBelok.add(kodeBelok.get(1));

        for(int i=2;i<kodeBelok.size();i++)
        {
            if(kodeBelok.get(i).getDfcce()==kodeBelok.get(i-2).getDfcce())
            {
                if((i+1)==kodeBelok.size())
                    break;
                if(kodeBelok.get(i+1).getDfcce()==kodeBelok.get(i-1).getDfcce())
                {
                    i++;
                }
                else
                {
                    newKodeBelok.add(kodeBelok.get(i));
                }
            }
            else
            {
                newKodeBelok.add(kodeBelok.get(i));
            }
        }

        return newKodeBelok;
    }

    public String getFcceShort() {
        return newDirs.stream().map(it -> Byte.toString(it.getDfcce())).collect(Collectors.joining());
    }

    public String getTextShort() {
        return newDirs.stream().map(it -> Character.toString(it.getText())).collect(Collectors.joining());
    }


    @Override
    public String toString() {
        return getText();
    }
}
