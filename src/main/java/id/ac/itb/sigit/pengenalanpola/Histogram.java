package id.ac.itb.sigit.pengenalanpola;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by ceefour on 13/10/2015.
 */
@Service
public class Histogram {

    private static Logger log = LoggerFactory.getLogger(Histogram.class);
    private opencv_core.Mat imgMat;

    public static ObjectMapper MAPPER = new ObjectMapper();

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

    public opencv_core.Mat loadInput(File imageFile) {
        log.info("Processing image file '{}' ...", imageFile);
        imgMat = opencv_highgui.imread(imageFile.getPath());
        log.info("Image mat: rows={} cols={}", imgMat.rows(), imgMat.cols());
        return imgMat;
    }

}
