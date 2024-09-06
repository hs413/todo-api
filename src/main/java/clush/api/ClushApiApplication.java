package clush.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class ClushApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClushApiApplication.class, args);
    }

}
