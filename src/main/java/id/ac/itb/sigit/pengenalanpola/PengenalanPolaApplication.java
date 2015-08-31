package id.ac.itb.sigit.pengenalanpola;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.Arrays;

@SpringBootApplication
public class PengenalanPolaApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(PengenalanPolaApplication.class);
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        SpringApplication.run(PengenalanPolaApplication.class, args);
    }

    protected static class ChartData {
        public int color;
        public int frequency;

        public ChartData(int color, int frequency) {
            this.color = color;
            this.frequency = frequency;
        }
    }

    protected static ObjectMapper MAPPER = new ObjectMapper();

    protected static String histToJson(int[] hist) throws JsonProcessingException {
        ChartData[] data = new ChartData[hist.length];
        for (int i = 0; i < hist.length; i++) {
            data[i] = new ChartData(i, hist[i]);
        }
        return MAPPER.writeValueAsString(data);
    }

    @Override
    public void run(String... args) throws Exception {

        final File imageFile= new File("B.jpg");
        log.info("Processing image file '{}' ...", imageFile);
        final Mat imgMat = Highgui.imread(imageFile.getPath());
        log.info("Image mat: rows={} cols={}", imgMat.rows(), imgMat.cols());

        byte[] imagByte=new byte[3];
        imgMat.get(0, 0, imagByte);
        log.info("Image {}", imagByte);

        boolean colorCounts[][][] = new boolean[256][256][256];
        int jumlahWarna=0;
        int rImage[]=new int[256];
        int gImage[]=new int[256];
        int bImage[]=new int[256];
        int grayScaleImage[]=new int[256];

        for (int i=0;i<imgMat.rows();i++)
        {
            Mat scanline = imgMat.row(i);

            for (int j=0;j<imgMat.cols();j++)
            {
                scanline.get(0,j,imagByte);
                int b = imagByte[0] & 0xff;
                int g = imagByte[1] & 0xff;
                int r = imagByte[2] & 0xff;

                log.trace("Jumlah Warna R{} G{} B {}", r, g, b);

                if (!colorCounts[r][g][b])
                {
                    jumlahWarna++;
                }
               colorCounts[r][g][b] = true;

               rImage[r]++;
               gImage[g]++;
               bImage[b]++;
               int grayScale = Math.round((r + g + b)/3f);
               grayScaleImage[grayScale]++;
            }
        }

        log.info("Jumlah Warna {}", jumlahWarna);

        final String markerToReplace = "[{color: 0, frequency: 60}, {color: 128, frequency: 80}, {color: 172, frequency: 80}]";
        final String histogramTpl = IOUtils.toString(PengenalanPolaApplication.class.getResource("/BarChart.html"));

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

    }
}
