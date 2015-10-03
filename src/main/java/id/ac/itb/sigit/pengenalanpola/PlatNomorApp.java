package id.ac.itb.sigit.pengenalanpola;

import org.opencv.core.Mat;
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
 * Created by Sigit on 02/10/2015.
 */
@SpringBootApplication
@Profile("platnomorapp")
public class PlatNomorApp implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PlatNomorApp.class);


    public static void main(String[] args) {
        new SpringApplicationBuilder(PlatNomorApp.class).profiles("platnomorapp")
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        List<String> hasilPengenalan=new ArrayList<>();

        //===================================Data Training======================================//
        List<ChainCode> dataTraining=new ArrayList<>();
        List<String> stringTraining=new ArrayList<>();//B 14 IA -- 04.16
        stringTraining.add("platB.jpg");
        stringTraining.add("plat1.jpg");
        stringTraining.add("plat4.jpg");
        stringTraining.add("plati.jpg");
        stringTraining.add("platA.jpg");
        stringTraining.add("platTgl0.jpg");
        stringTraining.add("platTgl6.jpg");

        for(int i=0;i<stringTraining.size();i++)
        {
            final File imageFile = new File(stringTraining.get(i));
            log.info("Processing image file '{}' ...", imageFile);
            final Mat imgMat = Highgui.imread(imageFile.getPath(), Highgui.CV_LOAD_IMAGE_GRAYSCALE);
            log.info("Image mat: rows={} cols={}", imgMat.rows(), imgMat.cols());
            ChainCodeWhiteConverter chainCodeWhiteConverter=new ChainCodeWhiteConverter(imgMat,"plat");
            List<ChainCode> data = chainCodeWhiteConverter.getChainCode();
            data.get(0).setCharacter(stringTraining.get(i));
            dataTraining.add(data.get(0));
        }


        //===================================Data Plat======================================//
        final File imageFile = new File("Plat_Nomor.jpg");//AA_1.jpg
        log.info("Processing image file '{}' ...", imageFile);
        final Mat imgMat = Highgui.imread(imageFile.getPath(), Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        log.info("Image mat: rows={} cols={}", imgMat.rows(), imgMat.cols());
        ChainCodeWhiteConverter chainCodeWhiteConverter=new ChainCodeWhiteConverter(imgMat,"plat");
        List<ChainCode> dataPlat = chainCodeWhiteConverter.getChainCode();

        //===================================Cek Data======================================//

        for (int i=0;i<dataPlat.size();i++)
        {
            ChainCode charPlat=dataPlat.get(i);

            for(int j=0;j<dataTraining.size();j++)
            {
                if( charPlat.getKodeBelok().equals(dataTraining.get(j).getKodeBelok()))
                {
                    hasilPengenalan.add(charPlat.getCharacter());
                    break;
                }
            }
        }



    }
}
