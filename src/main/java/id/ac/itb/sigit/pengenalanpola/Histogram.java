package id.ac.itb.sigit.pengenalanpola;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * Created by ceefour on 13/10/2015.
 */
public class Histogram implements Serializable, Histogramable {

    public static ObjectMapper MAPPER = new ObjectMapper();

    private static Logger log = LoggerFactory.getLogger(Histogram.class);

    public enum AxisScale {
        LINEAR,
        LOG10,
        /**
         * WRONG
         */
        LOG2
    }

    public static class ChartDataC3 {
        public List<Object[]> columns = new ArrayList<>();

        public ChartDataC3() {
        }

        public void addSeries(AxisScale scale, String color, int[] hist) {
            Object[] series = new Object[257];
            series[0] = color;
            for (int i = 0; i < hist.length; i++) {
                if (scale == AxisScale.LOG10) {
                    series[1 + i] = hist[i] == 0 ? 0 : Math.log10(hist[i]);
                } else if (scale == AxisScale.LOG2) { // WRONG VALUE!
                    series[1 + i] = hist[i] == 0 ? 0 : Math.log10(hist[i]) / Math.log10(2.0);
                } else {
                    series[1 + i] = hist[i];
                }
            }
            columns.add(series);
        }
    }

    public static class ChartData {
        public int color;
        public int frequency;

        public ChartData(int color, int frequency) {
            this.color = color;
            this.frequency = frequency;
        }
    }

    public static String histToJson(int[] hist) {
        try {
            ChartData[] data = new ChartData[hist.length];
            for (int i = 0; i < hist.length; i++) {
                data[i] = new ChartData(i, hist[i]);
            }
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error JSON histogram", e);
        }
    }

    public static String histToJsonC3(AxisScale scale, String color, int[] hist) {
        try {
            ChartDataC3 data = new ChartDataC3();
            data.addSeries(scale, color, hist);
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error JSON histogram", e);
        }
    }

    public static String histToJsonC3(AxisScale scale, int[] grayscale, int[] red, int[] green, int[] blue) {
        try {
            ChartDataC3 data = new ChartDataC3();
            data.addSeries(scale, "grayscale", grayscale);
            data.addSeries(scale, "red", red);
            data.addSeries(scale, "green", green);
            data.addSeries(scale, "blue", blue);
            return MAPPER.writeValueAsString(data.columns);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error JSON histogram", e);
        }
    }

//    private Mat origMat;
//    private Mat grayMat;
    private int uniqueColorCount;
    private int grayscale[];
    private int red[];
    private int green[];
    private int blue[];
    private int grayscaleCumulative[];
    private int redCumulative[];
    private int greenCumulative[];
    private int blueCumulative[];

    public Mat loadInput(File imageFile) {
        log.info("Processing image file '{}' ...", imageFile);
        final Mat origMat = opencv_highgui.imread(imageFile.getPath());
        log.info("Image mat: rows={} cols={}", origMat.rows(), origMat.cols());
        run(origMat);
        return origMat;
    }

    public Mat loadInput(String contentType, byte[] inputBytes) {
        log.info("Processing input image {}: {} bytes ...", contentType, inputBytes.length);
        final Mat origMat = opencv_highgui.imdecode(new Mat(inputBytes), opencv_highgui.CV_LOAD_IMAGE_COLOR);
        log.info("Image mat: rows={} cols={}", origMat.rows(), origMat.cols());
        run(origMat);
        return origMat;
    }

    public Mat loadInput(Mat inputMat) {
        final Mat origMat = inputMat.clone();
        log.info("Image mat: rows={} cols={}", origMat.rows(), origMat.cols());
        run(origMat);
        return origMat;
    }

//    public Mat getOrigMat() {
//        return origMat;
//    }

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

    protected void run(Mat origMat) {
        final Mat grayMat = origMat.clone();
        final ByteIndexer idx = origMat.createIndexer();
        final ByteIndexer grayIdx = grayMat.createIndexer();
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
            grayscaleCumulative = new int[256];
            redCumulative = new int[256];
            greenCumulative = new int[256];
            blueCumulative = new int[256];

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

                    log.trace("Jumlah Warna R{} G{} B {}", r, g, b);

                    if (!colorCounts[r][g][b]) {
                        uniqueColorCount++;
                    }
                    colorCounts[r][g][b] = true;

                    red[r]++;
                    green[g]++;
                    blue[b]++;
                    int grayScale = Math.round((r + g + b) / 3f);
                    grayscale[grayScale]++;

                    //fgamma
                    newImagByte[0] = fGamaByte[b];//(byte) fGama(b);
                    newImagByte[1] = fGamaByte[g];//(byte) fGama(g);
                    newImagByte[2] = fGamaByte[r];//(byte) fGama(r);

                    grayIdx.put(y, x, newImagByte);
                }
            }

            int mass = origMat.cols() * origMat.rows();
            int sum;
            //calculate the scale factor
            float pxScale = (float) 255.0 / mass;

            //make CDF = cumulative distribution function
            sum = 0;
            for (int i = 0; i < grayscale.length; i++){
                sum += grayscale[i];
                int value = (int) (pxScale * sum);
                if (value > 255) { value = 255; }
                grayscaleCumulative[i] = value;
            }
            sum = 0;
            for (int i = 0; i < red.length; i++){
                sum += red[i];
                int value = (int) (pxScale * sum);
                if (value > 255) { value = 255; }
                redCumulative[i] = value;
            }
            sum = 0;
            for (int i = 0; i < green.length; i++){
                sum += green[i];
                int value = (int) (pxScale * sum);
                if (value > 255) { value = 255; }
                greenCumulative[i] = value;
            }
            sum = 0;
            for (int i = 0; i < blue.length; i++){
                sum += blue[i];
                int value = (int) (pxScale * sum);
                if (value > 255) { value = 255; }
                blueCumulative[i] = value;
            }
        } finally {
            grayIdx.release();
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

    public int[] getGrayscaleCumulative() {
        return grayscaleCumulative;
    }

    public int[] getRedCumulative() {
        return redCumulative;
    }

    public int[] getGreenCumulative() {
        return greenCumulative;
    }

    public int[] getBlueCumulative() {
        return blueCumulative;
    }

    @Override
    public int[] getCumulative() {
        return new int[0];
    }

//    public Mat getGrayMat() {
//        return grayMat;
//    }
}
