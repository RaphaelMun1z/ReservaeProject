package notification_service;

import notification_service.services.NotificationService;
import notification_service.templates.EmailItem;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class StartupNotificationService {

    public static void main(String[] args) {
        SpringApplication.run(
            StartupNotificationService.class,
            args
        );
    }

    @Bean
    CommandLineRunner testarEmail(NotificationService notificationService) {
        return args -> {
            String email = "ygor_marangoni@ufu.br";
            try {
                notificationService.enviarCompraConfirmada(
                    email,
                    "Raphael",
                    "bd90d7ef-e443-4c48-94f7-254ff20dd5d6",
                    "Music Tour Festival",
                    "18 de julho de 2026, às 20h",
                    "R$ 189,90"
                );

                notificationService.enviarPagamentoRecusado(
                    email,
                    "Raphael",
                    "bd90d7ef-e443-4c48-94f7-254ff20dd5d6",
                    "Music Tour Festival",
                    "Pagamento não autorizado pela instituição financeira",
                    "https://musictour.com/checkout/pagamento"
                );

                notificationService.enviarSolicitacaoAvaliacao(
                    email,
                    "Raphael",
                    "Music Tour Festival",
                    "bd90d7ef-e443-4c48-94f7-254ff20dd5d6"
                );

                List<EmailItem> itens = List.of(
                    new EmailItem(
                        "https://seu-dominio.com/images/eventos/music-tour.jpg",
                        "Music Tour Festival",
                        "Ingresso — Pista Premium",
                        2,
                        "R$ 189,90",
                        "R$ 379,80"
                    ),
                    new EmailItem(
                        "https://seu-dominio.com/images/eventos/camarote.jpg",
                        "Acesso ao Camarote",
                        "Experiência VIP",
                        1,
                        "R$ 249,90",
                        "R$ 249,90"
                    )
                );

                notificationService.enviarPagamentoPendente(
                    email,
                    "Raphael",
                    "123",
                    "10 de julho de 2026",
                    "R$ 629,70",
                    itens,
                    "R$ 629,70",
                    "https://musictour.com/checkout/pagamento/123",
                    "https://musictour.com/pedidos/123"
                );

                System.out.println("E-mail enviado com sucesso.");
            } catch (Exception exception) {
                System.err.println("Falha ao enviar o e-mail: " + exception.getMessage());
            }
        };
    }

}
