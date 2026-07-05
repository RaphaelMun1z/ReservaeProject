package notification_service.services;

import notification_service.messaging.event.PaymentConfirmedNotificationRequestedEvent;
import notification_service.messaging.event.PaymentFailedNotificationRequestedEvent;
import notification_service.messaging.event.PaymentPendingNotificationRequestedEvent;
import notification_service.messaging.event.TicketGeneratedEvent;
import notification_service.templates.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private static final String CUSTOMER_EMAIL_MOCK = "raphaelmunizvarela@gmail.com";
    private static final String CUSTOMER_NAME_MOCK = "Nome do comprador";

    private final RestClient restClient;
    private final EmailTemplateFactory templateFactory;
    private final PaymentEmailTemplateFactory paymentTemplateFactory;
    private final QrCodeImageService qrCodeImageService;
    private final String senderEmail;
    private final String senderName;

    public NotificationService(
        RestClient.Builder restClientBuilder,
        EmailTemplateFactory templateFactory,
        PaymentEmailTemplateFactory paymentTemplateFactory,
        QrCodeImageService qrCodeImageService,
        @Value("${brevo.api.url}") String apiUrl,
        @Value("${brevo.api.key}") String apiKey,
        @Value("${notification.email.sender-email}") String senderEmail,
        @Value("${notification.email.sender-name}") String senderName
    ) {
        this.restClient = restClientBuilder
            .baseUrl(apiUrl)
            .defaultHeader(
                "api-key",
                apiKey
            )
            .defaultHeader(
                "accept",
                MediaType.APPLICATION_JSON_VALUE
            )
            .build();

        this.templateFactory = templateFactory;
        this.paymentTemplateFactory = paymentTemplateFactory;
        this.qrCodeImageService = qrCodeImageService;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    public void enviarPagamentoPendente(PaymentPendingNotificationRequestedEvent event) {
        List<EmailItem> itens = event.items()
            .stream()
            .map(item -> new EmailItem(
                item.imageUrl(),
                event.eventName(),
                item.sectorName(),
                item.quantity(),
                formatMoney(item.unitPrice()),
                formatMoney(item.subtotal())
            ))
            .toList();

        EmailTemplate template = paymentTemplateFactory.criarPagamentoPendente(
            event.customerName(),
            event.orderId(),
            formatDateTime(event.paymentExpiresAt()),
            formatMoney(event.totalAmount()),
            itens,
            formatMoney(event.totalAmount()),
            event.paymentUrl(),
            event.orderUrl(),
            senderName
        );

        enviarTemplate(
            event.customerEmail(),
            event.customerName(),
            template
        );

        logger.info(
            "E-mail de pagamento pendente enviado para {} associado ao pedido {}.",
            event.customerEmail(),
            event.orderId()
        );
    }

    public void enviarCompraConfirmada(PaymentConfirmedNotificationRequestedEvent event) {
        EmailTemplate template = templateFactory.compraConfirmada(
            event.customerName(),
            event.orderId(),
            event.eventName(),
            event.eventDate(),
            formatMoney(event.totalAmount())
        );

        enviarTemplate(
            event.customerEmail(),
            event.customerName(),
            template
        );

        logger.info(
            "E-mail de compra confirmada enviado para {} associado ao pedido {}.",
            event.customerEmail(),
            event.orderId()
        );
    }

    public void enviarPagamentoRecusado(PaymentFailedNotificationRequestedEvent event) {
        EmailTemplate template = templateFactory.pagamentoRecusado(
            event.customerName(),
            event.orderId(),
            event.eventName(),
            event.reason(),
            event.paymentUrl()
        );

        enviarTemplate(
            event.customerEmail(),
            event.customerName(),
            template
        );

        logger.info(
            "E-mail de pagamento recusado/expirado enviado para {} associado ao pedido {}.",
            event.customerEmail(),
            event.orderId()
        );
    }

    public void enviarIngressosGerados(TicketGeneratedEvent event) {
        List<EmailAttachment> attachments = event.items()
            .stream()
            .map(item -> new EmailAttachment(
                "ticket-" + item.ticketId() + ".png",
                qrCodeImageService.generateQrCodeBase64(item.qrCodeHash())
            ))
            .toList();

        EmailTemplate template = new EmailTemplate(
            "Seus ingressos estão disponíveis",
            buildTicketsGeneratedHtml(event)
        );

        enviarTemplate(
            CUSTOMER_EMAIL_MOCK,
            CUSTOMER_NAME_MOCK,
            template,
            attachments
        );

        logger.info(
            "E-mail com {} ingresso(s) enviado para o pedido {}.",
            event.items().size(),
            event.orderId()
        );
    }

    public void enviarIngressoDisponivel(
        String destinatario,
        String nome,
        String pedidoId,
        String evento,
        String setor,
        String quantidade
    ) {
        EmailTemplate template = templateFactory.ingressoDisponivel(
            nome,
            pedidoId,
            evento,
            setor,
            quantidade
        );

        enviarTemplate(
            destinatario,
            nome,
            template
        );
    }

    public void enviarEventoAlterado(
        String destinatario,
        String nome,
        String evento,
        String informacaoAnterior,
        String novaInformacao,
        String descricaoAlteracao
    ) {
        EmailTemplate template = templateFactory.eventoAlterado(
            nome,
            evento,
            informacaoAnterior,
            novaInformacao,
            descricaoAlteracao
        );

        enviarTemplate(
            destinatario,
            nome,
            template
        );
    }

    public void enviarEventoCancelado(
        String destinatario,
        String nome,
        String evento,
        String pedidoId,
        String orientacaoReembolso
    ) {
        EmailTemplate template = templateFactory.eventoCancelado(
            nome,
            evento,
            pedidoId,
            orientacaoReembolso
        );

        enviarTemplate(
            destinatario,
            nome,
            template
        );
    }

    public void enviarLembreteEvento(
        String destinatario,
        String nome,
        String evento,
        String data,
        String horario,
        String local,
        String setor
    ) {
        EmailTemplate template = templateFactory.lembreteEvento(
            nome,
            evento,
            data,
            horario,
            local,
            setor
        );

        enviarTemplate(
            destinatario,
            nome,
            template
        );
    }

    public void enviarSolicitacaoAvaliacao(
        String destinatario,
        String nome,
        String evento,
        String pedidoId
    ) {
        EmailTemplate template = templateFactory.solicitarAvaliacao(
            nome,
            evento,
            pedidoId
        );

        enviarTemplate(
            destinatario,
            nome,
            template
        );
    }

    private void enviarTemplate(
        String destinatario,
        String nomeDestinatario,
        EmailTemplate template
    ) {
        enviarTemplate(
            destinatario,
            nomeDestinatario,
            template,
            List.of()
        );
    }

    private void enviarTemplate(
        String destinatario,
        String nomeDestinatario,
        EmailTemplate template,
        List<EmailAttachment> attachments
    ) {
        Map<String, Object> corpo = new HashMap<>();

        corpo.put(
            "sender",
            Map.of(
                "name",
                senderName,
                "email",
                senderEmail
            )
        );

        corpo.put(
            "to",
            List.of(Map.of(
                "name",
                nomeDestinatario,
                "email",
                destinatario
            ))
        );

        corpo.put(
            "subject",
            template.assunto()
        );

        corpo.put(
            "htmlContent",
            template.html()
        );

        if (attachments != null && !attachments.isEmpty()) {
            corpo.put(
                "attachment",
                attachments.stream()
                    .map(attachment -> Map.of(
                        "name",
                        attachment.name(),
                        "content",
                        attachment.content()
                    ))
                    .toList()
            );
        }

        restClient.post()
            .uri("/smtp/email")
            .contentType(MediaType.APPLICATION_JSON)
            .body(corpo)
            .retrieve()
            .toBodilessEntity();
    }

    private String buildTicketsGeneratedHtml(TicketGeneratedEvent event) {
        StringBuilder ticketsHtml = new StringBuilder();

        for (int index = 0; index < event.items().size(); index++) {
            var item = event.items().get(index);

            ticketsHtml.append("""
                <tr>
                    <td style="padding: 12px; border-bottom: 1px solid #e5e7eb;">
                        <strong>Ingresso %d</strong><br>
                        Ticket ID: %s<br>
                        Setor: %s<br>
                        Tipo: %s<br>
                        QR Code: anexado neste e-mail
                    </td>
                </tr>
                """.formatted(
                index + 1,
                item.ticketId(),
                item.sectorId(),
                item.ticketType()
            ));
        }

        return """
            <div style="font-family: Arial, sans-serif; background: #f9fafb; padding: 24px;">
                <div style="max-width: 720px; margin: auto; background: #ffffff; border-radius: 12px; padding: 24px;">
                    <h1 style="margin-top: 0;">Seus ingressos foram gerados</h1>
                    <p>Pedido: <strong>%s</strong></p>
                    <p>Os QR Codes dos seus ingressos estão anexados neste e-mail.</p>
            
                    <table style="width: 100%%; border-collapse: collapse;">
                        %s
                    </table>
            
                    <p style="margin-top: 24px; color: #6b7280;">
                        Apresente o QR Code correspondente na entrada do evento.
                    </p>
                </div>
            </div>
            """.formatted(
            event.orderId(),
            ticketsHtml
        );
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "R$ 0,00";
        }

        return java.text.NumberFormat
            .getCurrencyInstance(new Locale("pt", "BR"))
            .format(value);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Não informado";
        }

        return dateTime.format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }
}