package hackhub.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HackHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(HackHubApplication.class, args);
        System.out.println("HackHub Application Started! URL: http://localhost:8080");
    }
}
