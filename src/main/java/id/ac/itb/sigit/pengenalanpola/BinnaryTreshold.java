package id.ac.itb.sigit.pengenalanpola;


import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;

/**
 * Created by Sigit A on 9/20/2015.
 */
public class BinnaryTreshold {
    private float treshold = 125;
    private int max;
    private int min;

    public int getMax() {
        return this.max;
    }

    public int getMin() {
        return this.min;
    }

    public float getTreshold() {
        return treshold;
    }

    public void setTreshold(float treshold) {
        this.treshold = treshold;
    }

    public LookupTable createBinaryLookup() {
        LookupTable lp = new LookupTable();
        lp.setSinglelookup();
        for (int i = 0; i < 256; i++) {
            if (i < treshold) {
                lp.getSinglelookup()[i] = 1;
            } else {
                lp.getSinglelookup()[i] = 0;
            }
        }
        return lp;
    }

    public Mat getBinaryImage(Mat image, LookupTable lp) {
        int row = image.rows();
        int col = image.cols();
        Mat output = image.clone();
        int temp1 = 0, temp0 = 0;
        int colour[] = new int[1];
        final ByteIndexer outputIdx = output.createIndexer();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                byte[] tinyimg = new byte[1];
                outputIdx.get(i, j, tinyimg);

                colour[0] = grayScale(tinyimg);
                colour[0] = lp.getSinglelookup()[colour[0]];
                outputIdx.put(i, j, tinyimg);
                if (colour[0] == 1) {
                    temp1++;
                } else {
                    temp0++;
                }
            }
        }
        if (temp0 > temp1) {
            this.max = 0;
            this.min = 1;
        } else {
            this.max = 1;
            this.min = 0;
        }
        return output;
    }

    public Mat getInvers(Mat image) {
        ByteIndexer binIdx=image.createIndexer();
        int row = image.rows();
        int col = image.cols();
            byte colour[] = new byte[1];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    byte[] tinyimg = new byte[1];
                    binIdx.get(i, j, tinyimg);
                    if (tinyimg[0] == 1) {
                        colour[0] = 0;
                    } else {
                        colour[0] = 1;
                    }
                    binIdx.put(i, j, colour[0]);
                }
              //  System.out.println();
            }
        //}
        return image;
    }

    public static int grayScale(byte[] tinyb) {
        return Byte.toUnsignedInt(tinyb[0]);
    }

    public void cek(Mat image) {
        int row = image.rows();
        int col = image.cols();
        ByteIndexer binIdx=image.createIndexer();;
        int colour[] = new int[1];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                byte[] tinyimg = new byte[1];
                binIdx.get(0, j, tinyimg);
                System.out.print(tinyimg[0] + ",");
            }
            System.out.println();
        }
    }
}
