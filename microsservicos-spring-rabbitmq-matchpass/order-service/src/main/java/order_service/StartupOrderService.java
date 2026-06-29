package order_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
@EnableFeignClients
public class StartupOrderService {

//    @Bean
//    public CommandLineRunner commandLineRunner(KafkaTemplate<String, String> template) {
//        return args -> template.send("matchpass.pedidos-criados", "dados", "{ json }");
//    }

    public static void main(String[] args) {
        SpringApplication.run(StartupOrderService.class, args);
    }

}
