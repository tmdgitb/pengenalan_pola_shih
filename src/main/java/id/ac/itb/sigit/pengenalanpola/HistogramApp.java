package id.ac.itb.sigit.pengenalanpola;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
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

        final String markerToReplace = "[{color: 0, frequency: 60}, {color: 128, frequency: 80}, {color: 172, frequency: 80}]";
        final String histogramTpl = IOUtils.toString(HistogramApp.class.getResource("/BarChart.html"));

        //Red
        final String strR = histToJson(histogram.getRed());
        final String outR = histogramTpl.replace(markerToReplace, strR);
        final File fileR = new File("histogram_r.html");
        log.info("Writing histogram red to {} ...", fileR.getAbsoluteFile());
        FileUtils.write(fileR, outR);

        //Blue
        final String strB = histToJson(histogram.getBlue());
        final String outB = histogramTpl.replace(markerToReplace, strB);
        final File fileB = new File("histogram_b.html");
        log.info("Writing histogram blue to {} ...", fileB.getAbsoluteFile());
        FileUtils.write(fileB, outB);

        //Green
        final String strG = histToJson(histogram.getGreen());
        final String outG = histogramTpl.replace(markerToReplace, strG);
        final File fileG = new File("histogram_g.html");
        log.info("Writing histogram green to {} ...", fileG.getAbsoluteFile());
        FileUtils.write(fileG, outG);

        //Gray
        final String strGray = histToJson(histogram.getGrayscale());
        final String outGray = histogramTpl.replace(markerToReplace, strGray);
        final File fileGray = new File("histogram_grayscale.html");
        log.info("Writing histogram green to {} ...", fileGray.getAbsoluteFile());
        FileUtils.write(fileGray, outGray);

        // opencv_highgui.imwrite("../../images/Gray_Image.jpg", newImage);
        //opencv_highgui.imwrite("histogram_grayscale.jpg", histogram.getGrayMat());
    }

}
