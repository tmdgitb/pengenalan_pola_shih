package id.ac.itb.sigit.pengenalanpola;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilham on 16/10/2015.
 */
public class HistogramEq {

    public static ObjectMapper MAPPER = new ObjectMapper();

    private static Logger log = LoggerFactory.getLogger(HistogramEq.class);

    private opencv_core.Mat origMat;
    private opencv_core.Mat grayMat;
    private opencv_core.Mat equalizedMat;
    private int uniqueColorCount;
    private int red[];
    private int green[];
    private int blue[];
    private int grayscale[];
    private int red2[];
    private int green2[];
    private int blue2[];
    private int grayscale2[];

    public opencv_core.Mat loadInput(File imageFile) {
        log.info("Processing image file '{}' ...", imageFile);
        origMat = opencv_highgui.imread(imageFile.getPath());
        log.info("Image mat: rows={} cols={}", origMat.rows(), origMat.cols());
        return origMat;
    }

    public opencv_core.Mat loadInput(String contentType, byte[] inputBytes) {
        log.info("Processing input image {}: {} bytes ...", contentType, inputBytes.length);
        origMat = opencv_highgui.imdecode(new opencv_core.Mat(inputBytes), opencv_highgui.CV_LOAD_IMAGE_COLOR);
        log.info("Image mat: rows={} cols={}", origMat.rows(), origMat.cols());
        return origMat;
    }

    public opencv_core.Mat getOrigMat() {
        return origMat;
    }

    public opencv_core.Mat getEqualizedMat() {
        return equalizedMat;
    }

    byte[] getFGamaByte() {
        byte[] fGamaByte = new byte[256];
        for (int i = 0; i < 256; i++) {
            fGamaByte[i] = (byte) fGama(i);
        }
        return fGamaByte;
    }

    int fGama(float indexWarna) {
        double k = 7;
        //  ((indexWarna/255)^(1/k))*255
        return Math.round((float) Math.pow((indexWarna / 255f), 1 / k) * 255f);

    }

    public void run() {
        grayMat = origMat.clone();
        equalizedMat = origMat.clone();

        final ByteIndexer idx = origMat.createIndexer();
        final ByteIndexer newIdx = grayMat.createIndexer();
        final ByteIndexer eqIdx = equalizedMat.createIndexer();
        try {
//            byte[] imagByte = new byte[3];
//            idx.get(0, 0, imagByte);
//            log.info("Image {}", imagByte);

            boolean colorCounts[][][] = new boolean[256][256][256];
            uniqueColorCount = 0;
            red = new int[256];
            green = new int[256];
            blue = new int[256];
            grayscale = new int[256];
            grayscale2 = new int[256];

            byte[] imagByte = new byte[3];
            byte[] fGamaByte = getFGamaByte();

            for (int y = 0; y < origMat.rows(); y++) {
//                opencv_core.Mat scanline = origMat.row(i);
                for (int x = 0; x < origMat.cols(); x++) {
                    byte[] newImagByte = new byte[3];
                    idx.get(y, x, imagByte);
                    int b = Byte.toUnsignedInt(imagByte[0]);
                    int g = Byte.toUnsignedInt(imagByte[1]);
                    int r = Byte.toUnsignedInt(imagByte[2]);

                    red[r]++;
                    green[g]++;
                    blue[b]++;
                    int grayScale = Math.round((r + g + b) / 3f);
                    grayscale[grayScale]++;

                    //fgamma
                    newImagByte[0] = fGamaByte[b];//(byte) fGama(b);
                    newImagByte[1] = fGamaByte[g];//(byte) fGama(g);
                    newImagByte[2] = fGamaByte[r];//(byte) fGama(r);

                    newIdx.put(y, x, newImagByte);
                }
            }

            int mass = origMat.rows() * origMat.cols();
            int sum = 0;

            //calculate the scale factor
            float pxScale = (float) 255.0 / mass;

            //make CDF
            for (int i = 0; i < grayscale2.length; i++){
                sum += grayscale[i];
                int value = (int) (pxScale * sum);
                if (value > 255) { value = 255; }
                grayscale2[i] = value;
            }

            for (int y = 0; y < equalizedMat.rows(); y++) {
//                opencv_core.Mat scanline = origMat.row(i);
                for (int x = 0; x < equalizedMat.cols(); x++) {

                    byte[] newImagByte = new byte[3];
                    idx.get(y, x, imagByte);
                    int b = Byte.toUnsignedInt(imagByte[0]);
                    int g = Byte.toUnsignedInt(imagByte[1]);
                    int r = Byte.toUnsignedInt(imagByte[2]);

                    red[r]++;
                    green[g]++;
                    blue[b]++;
                    int grayScale = Math.round((r + g + b) / 3f);
//                    equalizedMat.put(x, y, grayscale2[grayScale]);

                    //fgamma
//                    newImagByte[0] = fGamaByte[b];//(byte) fGama(b);
//                    newImagByte[1] = fGamaByte[g];//(byte) fGama(g);
//                    newImagByte[2] = fGamaByte[r];//(byte) fGama(r);
                    newImagByte[0] = (byte) grayscale2[grayScale];
                    newImagByte[1] = (byte) grayscale2[grayScale];
                    newImagByte[2] = (byte) grayscale2[grayScale];

                    eqIdx.put(y, x, newImagByte);
                }
            }


        } finally {
            eqIdx.release();
            newIdx.release();
            idx.release();
        }

        log.info("Jumlah Warna {}", uniqueColorCount);
    }

    public int getUniqueColorCount() {
        return uniqueColorCount;
    }

    public int[] getRed() {
        return red;
    }

    public int[] getGreen() {
        return green;
    }

    public int[] getBlue() {
        return blue;
    }

    public int[] getGrayscale() {
        return grayscale;
    }

    public int[] getGrayscale2() {
        return grayscale2;
    }

    public opencv_core.Mat getGrayMat() {
        return grayMat;
    }
}