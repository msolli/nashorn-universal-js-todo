package no.smallinternet.universaljstodo;

import net.matlux.NreplServerSpring;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@Controller
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile("dev")
    public NreplServerSpring repl() {
        return new NreplServerSpring(1112);
    }
}
