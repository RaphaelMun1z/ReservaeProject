package notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartupNotificationService {

    public static void main(String[] args) {
        SpringApplication.run(
            StartupNotificationService.class,
            args
        );
    }

}
