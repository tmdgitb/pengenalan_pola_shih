package id.ac.itb.sigit.pengenalanpola;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

import javax.inject.Inject;
import java.io.File;

/**
 * Created by Sigit on 18/09/2015.
 */
@SpringBootApplication
@Profile("chaincodeapp")
public class ChainCodeApp implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ChainCodeApp.class);

    @Inject
    private ChainCodeService chainCodeService;

    /**
     * ////////////////////////
     */

    public static void main(String[] args) {
        new SpringApplicationBuilder(ChainCodeApp.class).profiles("chaincodeapp")
                .web(false)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        final File imageFile = new File("AA.jpg");//AA_1.jpg
        chainCodeService.loadInput(imageFile, 1);
        final Geometry data = chainCodeService.getGeometries().get(0);
        data.setCharacter("1");

        log.info("Chaincode char #{} = {}", data.getCharacter(), data.getChainCodeFcce());
    }

}