package id.ac.itb.sigit.pengenalanpola;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import javax.inject.Inject;
import java.io.File;

import static id.ac.itb.sigit.pengenalanpola.Histogram.histToJson;

@SpringBootApplication
@Profile("histogramapp")
public class HistogramApp implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(HistogramApp.class);

    static {
        log.info("java.library.path = {}", System.getProperty("java.library.path"));
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(HistogramApp.class).profiles("histogramapp")
                .web(false)
                .run(args);
    }

    @Inject
    private Histogram histogram;

    @Override
    public void run(String... args) throws Exception {
        final opencv_core.Mat imgMat = histogram.loadInput(new File("Beach.jpg"));

        int jumlahWarna;
        int rImage[];
        int gImage[];
        int bImage[];
        int grayScaleImage[];
        final opencv_core.Mat newImage = imgMat.clone();
        final ByteIndexer idx = imgMat.createIndexer();
        final ByteIndexer newIdx = newImage.createIndexer();
        try {
//            byte[] imagByte = new byte[3];
//            idx.get(0, 0, imagByte);
//            log.info("Image {}", imagByte);

            boolean colorCounts[][][] = new boolean[256][256][256];
            jumlahWarna = 0;
            rImage = new int[256];
            gImage = new int[256];
            bImage = new int[256];
            grayScaleImage = new int[256];

            byte[] imagByte = new byte[3];
            byte[] fGamaByte = getFGamaByte();

            for (int y = 0; y < imgMat.rows(); y++) {
//                opencv_core.Mat scanline = imgMat.row(i);
                for (int x = 0; x < imgMat.cols(); x++) {
                    byte[] newImagByte = new byte[3];
                    idx.get(y, x, imagByte);
                    int b = Byte.toUnsignedInt(imagByte[0]);
                    int g = Byte.toUnsignedInt(imagByte[1]);
                    int r = Byte.toUnsignedInt(imagByte[2]);

                    log.trace("Jumlah Warna R{} G{} B {}", r, g, b);

                    if (!colorCounts[r][g][b]) {
                        jumlahWarna++;
                    }
                    colorCounts[r][g][b] = true;

                    rImage[r]++;
                    gImage[g]++;
                    bImage[b]++;
                    int grayScale = Math.round((r + g + b) / 3f);
                    grayScaleImage[grayScale]++;

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

        log.info("Jumlah Warna {}", jumlahWarna);

        final String markerToReplace = "[{color: 0, frequency: 60}, {color: 128, frequency: 80}, {color: 172, frequency: 80}]";
        final String histogramTpl = IOUtils.toString(HistogramApp.class.getResource("/BarChart.html"));

        //Red
        final String strR = histToJson(rImage);
        final String outR = histogramTpl.replace(markerToReplace, strR);
        final File fileR = new File("histogram_r.html");
        log.info("Writing histogram red to {} ...", fileR.getAbsoluteFile());
        FileUtils.write(fileR, outR);

        //Blue
        final String strB = histToJson(bImage);
        final String outB = histogramTpl.replace(markerToReplace, strB);
        final File fileB = new File("histogram_b.html");
        log.info("Writing histogram blue to {} ...", fileB.getAbsoluteFile());
        FileUtils.write(fileB, outB);

        //Green
        final String strG = histToJson(gImage);
        final String outG = histogramTpl.replace(markerToReplace, strG);
        final File fileG = new File("histogram_g.html");
        log.info("Writing histogram green to {} ...", fileG.getAbsoluteFile());
        FileUtils.write(fileG, outG);

        //Gray
        final String strGray = histToJson(grayScaleImage);
        final String outGray = histogramTpl.replace(markerToReplace, strGray);
        final File fileGray = new File("histogram_grayscale.html");
        log.info("Writing histogram green to {} ...", fileGray.getAbsoluteFile());
        FileUtils.write(fileGray, outGray);


        // opencv_highgui.imwrite("../../images/Gray_Image.jpg", newImage);

        opencv_highgui.imwrite("D:/Gray_Image.jpg", newImage);
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

}
