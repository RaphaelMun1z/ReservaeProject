package event_catalog_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartupEventCatalogService {

    public static void main(String[] args) {
        SpringApplication.run(
            StartupEventCatalogService.class,
            args
        );
    }

}
