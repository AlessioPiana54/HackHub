package hackhub.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HackHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(HackHubApplication.class, args);
        System.out.println("HackHub Application Started! URL: http://localhost:8080");
    }
}
