package id.ac.itb.sigit.pengenalanpola;


import org.bytedeco.javacpp.opencv_core;

/**
 * Created by Sigit A on 10/17/2015.
 */
public class Resizer {
    public static opencv_core.Size resizeTo500Max(int x, int y){
        int xcpy = x, ycpy = y;
        if (x>y) {
            xcpy = ((int)((double)x/(double)x)*500);
            ycpy = ((int)((double)y/(double)x)*500);
        }else{
            xcpy = (int)(((double)x/(double)y)*500);
            ycpy = (int)(((double)y/(double)y)*500);
        }
        return new opencv_core.Size(ycpy,xcpy);
    }
}
