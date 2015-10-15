package id.ac.itb.sigit.pengenalanpola;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * Created by ceefour on 13/10/2015.
 */
@Service
public class Histogram {

    public static ObjectMapper MAPPER = new ObjectMapper();

    private static Logger log = LoggerFactory.getLogger(Histogram.class);

    public static class ChartDataC3 {
        public List<Object[]> columns = new ArrayList<>();

        public ChartDataC3() {
        }

        public void addSeries(String color, int[] hist) {
            Object[] series = new Object[257];
            series[0] = color;
            for (int i = 0; i < hist.length; i++) {
                series[1 + i] = hist[i] == 0 ? 0 : Math.log10(hist[i]);
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

    public static String histToJsonC3(String color, int[] hist) {
        try {
            ChartDataC3 data = new ChartDataC3();
            data.addSeries(color, hist);
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error JSON histogram", e);
        }
    }

    public static String histToJsonC3(int[] grayscale, int[] red, int[] green, int[] blue) {
        try {
            ChartDataC3 data = new ChartDataC3();
            data.addSeries("grayscale", grayscale);
            data.addSeries("red", red);
            data.addSeries("green", green);
            data.addSeries("blue", blue);
            return MAPPER.writeValueAsString(data.columns);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error JSON histogram", e);
        }
    }

    private Mat origMat;
    private Mat grayMat;
    private int uniqueColorCount;
    private int red[];
    private int green[];
    private int blue[];
    private int grayscale[];

    public Mat loadInput(File imageFile) {
        log.info("Processing image file '{}' ...", imageFile);
        origMat = opencv_highgui.imread(imageFile.getPath());
        log.info("Image mat: rows={} cols={}", origMat.rows(), origMat.cols());
        return origMat;
    }

    public Mat loadInput(String contentType, byte[] inputBytes) {
        log.info("Processing input image {}: {} bytes ...", contentType, inputBytes.length);
        origMat = opencv_highgui.imdecode(new Mat(inputBytes), opencv_highgui.CV_LOAD_IMAGE_COLOR);
        log.info("Image mat: rows={} cols={}", origMat.rows(), origMat.cols());
        return origMat;
    }

    public Mat getOrigMat() {
        return origMat;
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
        final ByteIndexer idx = origMat.createIndexer();
        final ByteIndexer newIdx = grayMat.createIndexer();
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

                    newIdx.put(y, x, newImagByte);
                }
            }
        } finally {
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

    public Mat getGrayMat() {
        return grayMat;
    }
}
