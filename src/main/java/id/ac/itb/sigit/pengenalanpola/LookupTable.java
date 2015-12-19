package id.ac.itb.sigit.pengenalanpola;

/**
 * Created by Sigit A on 9/18/2015.
 */
public class LookupTable {
    private int lookuplv[][] ;
    private int singlelookup[] ;

    public int[][] getLookuplv() {
        return lookuplv;
    }

    public void setLookuplv() {
        this.lookuplv = new int[4][256];
    }

    public void setLookuplvValue(int lv, int index, int value){
        lookuplv[lv][index] = value;
    }

    public int[] getSinglelookup() {
        return singlelookup ;
    }

    public void setSinglelookup() {
        this.singlelookup = new int[256];
    }
}
