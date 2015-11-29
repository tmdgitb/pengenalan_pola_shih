package id.ac.itb.sigit.pengenalanpola;


import org.bytedeco.javacpp.opencv_core;

/**
 * Created by Sigit A on 9/18/2015.
 */
public class Histogram2 {
    public int lv[][] = new int[4][256];//0 = R, 1 = G, 2 = B, 4 = Gray
    byte distribution[] = new byte[256 * 256 * 256];
    public int uniqueColor;
    public int totalPix;

    public byte[] getDistribution(){
        return distribution;
    }

    public String getJsonC3String() {
        String value = "";
        for (int i = 0; i <= 3; i++) {
            value = value + "[";
            if (i==0){
                value = value +"'red',";
            }else if(i==1){
                value = value + "'green',";
            }else if(i==2){
                value = value + "'blue',";
            }else if(i==3){
                value = value + "'grayscale',";
            }
            for (int j = 0; j <= 255; j++) {
                if (j<255){
                    value = value+lv[i][j]+",";
                }else{
                    value = value+lv[i][j];
                }
            }
            if (i < 3) {
                value = value + "],";
            } else {
                value = value + "]";
            }
        }
        return value;
    }

    public void setHistogram(opencv_core.Mat image) {
        int row = image.rows();
        int col = image.cols();
        totalPix = row*col;
        distribution = new byte[256 * 256 * 256];
        lv = new int[4][256];
        uniqueColor = 0;
        int colour[] = new int[3];
        for (int i = 0; i < row; i++) {
            opencv_core.Mat scnLine = image.row(i);
            for (int j = 0; j < col; j++) {
                byte[] tinyimg = new byte[3];
                scnLine.get(0, j, tinyimg);
                colour[0] = Byte.toUnsignedInt(tinyimg[0]);
                colour[1] = Byte.toUnsignedInt(tinyimg[1]);
                colour[2] = Byte.toUnsignedInt(tinyimg[2]);
                byte temp = distribution[colour[0] * 256 * 256 + colour[1] * 256 + colour[2]];
                distribution[colour[0] * 256 * 256 + colour[1] * 256 + colour[2]] = 1;
                if (distribution[colour[0] * 256 * 256 + colour[1] * 256 + colour[2]] == 1 && temp == 0)
                    uniqueColor++;
                lv[0][colour[0]]++;
                lv[1][colour[1]]++;
                lv[2][colour[2]]++;
                lv[3][(int) ((colour[0] + colour[1] + colour[2]) / 3)]++;
            }
        }
    }


}
