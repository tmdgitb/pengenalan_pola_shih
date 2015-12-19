package id.ac.itb.sigit.pengenalanpola;


/**
 * Created by Sigit A on 11/2/2015.
 */
public class ImageSharpness {
    private int[] p;

    public ImageSharpness() {
        p = new int[9];
    }

    public void setPixel(int[] matrix) {
        this.p = matrix;
    }

    public int homogenSharpness() {
        int maxdif = Math.abs(p[0] - p[4]);
        for (int i = 1; i < 9; i++) {
            int currentdif = Math.abs(p[i] - p[4]);
            if (currentdif > maxdif) {
                maxdif = currentdif;
            }
        }
        return maxdif;
    }

    public int differenceSharpness() {
        int maxdif = Math.abs(p[0] - p[8]);
        for (int i = 1; i < 4; i++) {
            int currentdif = Math.abs(p[i] - p[8 - i]);
            if (currentdif > maxdif) {
                maxdif = currentdif;
            }
        }
        return maxdif;
    }
}
