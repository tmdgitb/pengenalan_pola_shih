package id.ac.itb.sigit.pengenalanpola;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
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

    int fontFace = Core.FONT_HERSHEY_PLAIN;
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
        for (char ch=33; ch<=126;ch++)
        {
            if (ch == '!' || ch == '"' || ch == '%' || ch == ':' || ch == ';' || ch == '=' || ch == '?') { // skip multi-chaincode for now
                continue;
            }

            try{
                if(ch==106)
                {
                    int i=0;
                }
                System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
                Size textsize = Core.getTextSize(String.valueOf(ch), fontFace, fontScale, thickness, baseline);
                int heightImg=(int)textsize.height;
                int widthImg=(int)textsize.width;
                Mat source = new Mat(heightImg*2,widthImg*2, CvType.CV_8UC1, new Scalar(250));
                Core.putText(source, String.valueOf(ch), new Point(20, heightImg + (heightImg / 2)), fontFace, fontScale, new Scalar(0), thickness);
                final String filename = "character/" + String.valueOf(ch) + ".png";
                log.info("Writing {} {} ...", filename, source);
                Highgui.imwrite(filename, source);

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

    private void prosesChainCode(Mat img,String msg)
    {
        ChainCodeConverter chainCodeConverter=new ChainCodeConverter(img,msg);
        charDefs.add(chainCodeConverter.getChainCode());
    }
}
