package id.ac.itb.sigit.pengenalanpola;

/**
 * Created by Sigit A on 11/13/2015.
 */
public class Konvolutor {
    public static int konvolusi(int[][] np, int[][] p){
        int sum=0;
        for (int i=0 ; i< np.length;i++){
            for (int j=0 ; j<np[i].length;j++){
                sum = sum + (np[i][j]*p[i][j]);
            }
        }
        return Math.abs(sum);
    }
}
