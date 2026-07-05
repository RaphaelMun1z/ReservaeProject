package notification_service.services;

import notification_service.messaging.event.PaymentSessionCreatedEvent;
import notification_service.templates.EmailItem;
import notification_service.templates.EmailTemplate;
import notification_service.templates.EmailTemplateFactory;
import notification_service.templates.PaymentEmailTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final RestClient restClient;
    private final EmailTemplateFactory templateFactory;
    private final PaymentEmailTemplateFactory paymentTemplateFactory;
    private final String senderEmail;
    private final String senderName;

    public NotificationService(
        RestClient.Builder restClientBuilder,
        EmailTemplateFactory templateFactory,
        PaymentEmailTemplateFactory paymentTemplateFactory,
        @Value("${brevo.api.url}") String apiUrl,
        @Value("${brevo.api.key}") String apiKey,
        @Value("${notification.email.sender-email}") String senderEmail,
        @Value("${notification.email.sender-name}") String senderName
    ) {
        this.restClient = restClientBuilder.baseUrl(apiUrl).defaultHeader(
            "api-key",
            apiKey
        ).defaultHeader(
            "accept",
            MediaType.APPLICATION_JSON_VALUE
        ).build();

        this.templateFactory = templateFactory;
        this.paymentTemplateFactory = paymentTemplateFactory;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    public void enviarCompraConfirmada(
        String destinatario,
        String nome,
        String pedidoId,
        String evento,
        String dataEvento,
        String valorTotal
    ) {
        EmailTemplate template = templateFactory.compraConfirmada(
            nome,
            pedidoId,
            evento,
            dataEvento,
            valorTotal
        );

        enviarTemplate(
            destinatario,
            nome,
            template
        );
    }

    public void enviarPagamentoPendente(PaymentSessionCreatedEvent event) {
        String nomeDestinatario = "Nome destinatário";
        String dataVencimento = "Data de vencimento";
        String valorPendente = "Valor pendente";
        List<EmailItem> itens = new ArrayList<>();
        itens.add(
            new EmailItem(
                "https://cdn-icons-png.flaticon.com/512/708/708904.png",
                "Ingresso Show do Natanzinho Lima",
                "Camarote - Arena Santos",
                1,
                "R$ 499,90",
                "R$ 499,90"
            )
        );
        String valorTotal = "Valor Total";
        String linkPedido = "Link Pedido";

        EmailTemplate template = paymentTemplateFactory.criarPagamentoPendente(
            nomeDestinatario,
            event.orderId(),
            dataVencimento,
            valorPendente,
            itens,
            valorTotal,
            event.paymentUrl(),
            linkPedido,
            senderName
        );

        String destinatario = "raphaelmunizvarela@gmail.com";
        enviarTemplate(
            destinatario,
            nomeDestinatario,
            template
        );

        logger.info(
            "E-mail enviado para {} associado ao pedido {}.",
            destinatario,
            event.orderId()
        );
    }

    public void enviarPagamentoPendente(
        String destinatario,
        String nomeDestinatario,
        String pedidoId,
        String dataVencimento,
        String valorPendente,
        List<EmailItem> itens,
        String valorTotal,
        String linkPagamento,
        String linkPedido
    ) {
        EmailTemplate template = paymentTemplateFactory.criarPagamentoPendente(
            nomeDestinatario,
            pedidoId,
            dataVencimento,
            valorPendente,
            itens,
            valorTotal,
            linkPagamento,
            linkPedido,
            senderName
        );

        enviarTemplate(
            destinatario,
            nomeDestinatario,
            template
        );
    }

    public void enviarPagamentoRecusado(
        String destinatario,
        String nome,
        String pedidoId,
        String evento,
        String motivo,
        String linkPagamento
    ) {
        EmailTemplate template = templateFactory.pagamentoRecusado(
            nome,
            pedidoId,
            evento,
            motivo,
            linkPagamento
        );

        enviarTemplate(
            destinatario,
            nome,
            template
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

    public void enviarSolicitacaoAvaliacao(String destinatario, String nome, String evento, String pedidoId) {
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

    private void enviarTemplate(String destinatario, String nomeDestinatario, EmailTemplate template) {
        Map<String, Object> corpo = Map.of(
            "sender",
            Map.of(
                "name",
                senderName,
                "email",
                senderEmail
            ),
            "to",
            List.of(Map.of(
                "name",
                nomeDestinatario,
                "email",
                destinatario
            )),
            "subject",
            template.assunto(),
            "htmlContent",
            template.html()
        );

        restClient.post().uri("/smtp/email").contentType(MediaType.APPLICATION_JSON).body(corpo).retrieve()
            .toBodilessEntity();
    }
}