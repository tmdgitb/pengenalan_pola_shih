package id.ac.itb.sigit.pengenalanpola;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sigit on 22/09/2015.
 */

@SpringBootApplication
@Profile("ChainCodeAZ")
public class ChainCodeAZ implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ChainCodeAZ.class);

    int fontFace = opencv_core.FONT_HERSHEY_PLAIN;
    double fontScale = 20;
    int thickness = 10;
    int[] baseline = {0};
    List<CharDef> charDefs = new ArrayList<>();

    public static void main(String[] args) {
        new SpringApplicationBuilder(ChainCodeAZ.class).profiles("ChainCodeAZ")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (char ch = 33; ch <= 126; ch++) {
            if (ch == '!' || ch == '"' || ch == '%' || ch == ':' || ch == ';' || ch == '=' || ch == '?') { // skip multi-chaincode for now
                continue;
            }

            try {
                if (ch == 106) {
                    int i = 0;
                }
                //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                opencv_core.Size textsize = opencv_core.getTextSize(String.valueOf(ch), fontFace, fontScale, thickness, baseline);
                int heightImg = (int) textsize.height();
                int widthImg = (int) textsize.width();
                opencv_core.Mat source = new opencv_core.Mat(heightImg * 2, widthImg * 2, opencv_core.CV_8UC1, new opencv_core.Scalar(250));
                //FIXME: opencv_core.putText(source, String.valueOf(ch), new opencv_core.Point(20, heightImg + (heightImg / 2)), fontFace, fontScale, new opencv_core.Scalar(0), thickness);
                final String filename = "character/" + String.valueOf(ch) + ".png";
                log.info("Writing {} {} ...", filename, source);
                opencv_highgui.imwrite(filename, source);

                prosesChainCode(source, String.valueOf(ch));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // save training data chain codes to JSON
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        final File charDefsFile = new File("training/trainings.CharDef.json");
        log.info("Writing {} training data chain codes to {} ...", charDefs.size(), charDefsFile);
        mapper.writeValue(charDefsFile, charDefs);
    }

    private void prosesChainCode(opencv_core.Mat img, String msg) {
        final ByteIndexer idx = img.createIndexer();
        try {
            ChainCodeConverter chainCodeConverter = new ChainCodeConverter(img, idx, msg);
            charDefs.add(chainCodeConverter.getChainCode());
        } finally {
            idx.release();
        }
    }
}
