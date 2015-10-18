package id.ac.itb.sigit.pengenalanpola;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 18/10/2015.
 */
public class ZhangSuenFitur {
    private int bulatan=0;
    private List<ZhangSuenUjung> ujung=new ArrayList<>();
    private  List<ZhangSuenSimpangan> simpangan=new ArrayList<>();

    public int getBulatan() {
        return bulatan;
    }
    public String getBulatanString()
    {
        return String.valueOf(bulatan);
    }

    public void setBulatan(int bulatan) {
        this.bulatan = bulatan;
    }

    public List<ZhangSuenUjung> getUjung() {
        return ujung;
    }
    public  String getUjungString()
    {
        if(ujung==null)
        {
            return "";
        }
        String msg=String.valueOf(ujung.size())+"==> ";
        for(int i=0;i<ujung.size();i++)
        {
            msg=msg+"; point x : "+ String.valueOf(ujung.get(i).getEdge().getX())
                    + " ,point y : "+String.valueOf(ujung.get(i).getEdge().getY());
        }

        return msg;
    }

    public List<ZhangSuenSimpangan> getSimpangan() {
        return simpangan;
    }

    public  String getSimpanganString()
    {
        if(simpangan==null)
        {
            return "";
        }
        String msg=String.valueOf(simpangan.size())+"==> ";
        for(int i=0;i<simpangan.size();i++)
        {
            msg=msg+"; point x : "+ String.valueOf(simpangan.get(i).getEdge().getX())
                    + " ,point y : "+String.valueOf(simpangan.get(i).getEdge().getY());
        }

        return msg;
    }

}
