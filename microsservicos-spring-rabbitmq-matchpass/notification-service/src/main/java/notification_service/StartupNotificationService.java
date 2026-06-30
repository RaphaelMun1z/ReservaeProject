package notification_service;

import notification_service.services.NotificationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StartupNotificationService {

    @Bean
    CommandLineRunner testarEmail(NotificationService notificationService) {
        return args -> {
            try {
                notificationService.enviarCompraConfirmada(
                    "raphaelmunizvarela@gmail.com",
                    "Raphael",
                    "bd90d7ef-e443-4c48-94f7-254ff20dd5d6",
                    "Music Tour Festival",
                    "18 de julho de 2026, às 20h",
                    "R$ 189,90"
                );

                notificationService.enviarPagamentoRecusado(
                    "raphaelmunizvarela@gmail.com",
                    "Raphael",
                    "bd90d7ef-e443-4c48-94f7-254ff20dd5d6",
                    "Music Tour Festival",
                    "Pagamento não autorizado pela instituição financeira",
                    "https://musictour.com/checkout/pagamento"
                );

                notificationService.enviarSolicitacaoAvaliacao(
                    "raphaelmunizvarela@gmail.com",
                    "Raphael",
                    "Music Tour Festival",
                    "bd90d7ef-e443-4c48-94f7-254ff20dd5d6"
                );

                System.out.println("E-mail enviado com sucesso.");
            } catch (Exception exception) {
                System.err.println(
                    "Falha ao enviar o e-mail: " + exception.getMessage()
                );
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(StartupNotificationService.class, args);
    }

}
