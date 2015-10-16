package id.ac.itb.sigit.pengenalanpola;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

// https://issues.apache.org/jira/browse/WICKET-6002
@SpringBootApplication(exclude = MultipartAutoConfiguration.class)
@Profile("daemonApp")
public class DaemonApp implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DaemonApp.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(DaemonApp.class)
                .profiles("daemonApp")
                .web(true)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Please open using browser: http://localhost:8080/");
    }
}
