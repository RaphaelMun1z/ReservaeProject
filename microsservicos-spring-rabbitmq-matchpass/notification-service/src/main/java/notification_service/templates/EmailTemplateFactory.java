package notification_service.templates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailTemplateFactory {

    private static final String COR_PRINCIPAL = "#ff624d";
    private static final String COR_SUCESSO = "#10b981";
    private static final String COR_ATENCAO = "#f59e0b";
    private static final String COR_ERRO = "#ef4444";
    private static final String COR_INFORMACAO = "#3b82f6";
    private static final String COR_AVALIACAO = "#facc15";

    private final String frontendUrl;

    public EmailTemplateFactory(
        @Value("${notification.frontend-url:http://localhost:5500}") String frontendUrl
    ) {
        this.frontendUrl = removerBarraFinal(frontendUrl);
    }

    public EmailTemplate compraConfirmada(
        String nome,
        String pedidoId,
        String evento,
        String dataEvento,
        String valorTotal
    ) {
        String assunto = "Compra confirmada — " + evento;

        String html = montarTemplate(
            "Compra segura",
            "✓",
            COR_SUCESSO,
            "Tudo certo!",
            nome,
            """
                Sua compra foi confirmada com sucesso. Os ingressos já estão vinculados à sua conta e estão disponíveis para consulta.
                """,
            List.of(
                new EmailDetail("Pedido", pedidoId, false),
                new EmailDetail("Evento", evento, true),
                new EmailDetail("Data", dataEvento, false),
                new EmailDetail("Valor total", valorTotal, false)
            ),
            "Ver ingressos",
            frontendUrl + "/pages/user/my-tickets.html",
            """
                Guarde este e-mail como comprovante da sua compra.
                """
        );

        return new EmailTemplate(assunto, html);
    }

    public EmailTemplate pagamentoPendente(
        String nome,
        String pedidoId,
        String evento,
        String valorTotal,
        String prazoPagamento,
        String linkPagamento
    ) {
        String assunto = "Pagamento pendente — pedido " + pedidoId;

        String html = montarTemplate(
            "Aguardando pagamento",
            "!",
            COR_ATENCAO,
            "Finalize seu pagamento",
            nome,
            """
                Seu pedido foi criado, mas o pagamento ainda não foi confirmado. Conclua o pagamento dentro do prazo para manter sua reserva.
                """,
            List.of(
                new EmailDetail("Pedido", pedidoId, false),
                new EmailDetail("Evento", evento, true),
                new EmailDetail("Valor", valorTotal, false),
                new EmailDetail("Prazo", prazoPagamento, true)
            ),
            "Finalizar pagamento",
            linkPagamento,
            """
                Após o prazo informado, a reserva poderá ser liberada automaticamente.
                """
        );

        return new EmailTemplate(assunto, html);
    }

    public EmailTemplate pagamentoRecusado(
        String nome,
        String pedidoId,
        String evento,
        String motivo,
        String linkPagamento
    ) {
        String assunto = "Não foi possível aprovar seu pagamento";

        String html = montarTemplate(
            "Pagamento não aprovado",
            "×",
            COR_ERRO,
            "Pagamento recusado",
            nome,
            """
                Não foi possível concluir o pagamento do seu pedido. Você pode tentar novamente utilizando o mesmo ou outro método de pagamento.
                """,
            List.of(
                new EmailDetail("Pedido", pedidoId, false),
                new EmailDetail("Evento", evento, true),
                new EmailDetail("Motivo", motivo, false)
            ),
            "Tentar novamente",
            linkPagamento,
            """
                Nenhuma cobrança foi confirmada para esta tentativa.
                """
        );

        return new EmailTemplate(assunto, html);
    }

    public EmailTemplate ingressoDisponivel(
        String nome,
        String pedidoId,
        String evento,
        String setor,
        String quantidade
    ) {
        String assunto = "Seu ingresso já está disponível";

        String html = montarTemplate(
            "Ingresso liberado",
            "🎟",
            COR_PRINCIPAL,
            "Prepare-se para o evento",
            nome,
            """
                Seu ingresso foi gerado e já pode ser acessado pela sua carteira digital. Apresente o QR Code na entrada do evento.
                """,
            List.of(
                new EmailDetail("Pedido", pedidoId, false),
                new EmailDetail("Evento", evento, true),
                new EmailDetail("Setor", setor, false),
                new EmailDetail("Quantidade", quantidade, false)
            ),
            "Abrir minha carteira",
            frontendUrl + "/pages/user/my-tickets.html",
            """
                Evite compartilhar o QR Code. Ele é pessoal e será validado na entrada.
                """
        );

        return new EmailTemplate(assunto, html);
    }

    public EmailTemplate eventoAlterado(
        String nome,
        String evento,
        String informacaoAnterior,
        String novaInformacao,
        String descricaoAlteracao
    ) {
        String assunto = "Atualização importante sobre " + evento;

        String html = montarTemplate(
            "Evento atualizado",
            "i",
            COR_INFORMACAO,
            "Houve uma alteração",
            nome,
            descricaoAlteracao,
            List.of(
                new EmailDetail("Evento", evento, true),
                new EmailDetail("Informação anterior", informacaoAnterior, false),
                new EmailDetail("Nova informação", novaInformacao, true)
            ),
            "Ver detalhes do evento",
            frontendUrl + "/pages/public/shows.html",
            """
                Seu ingresso continua válido, salvo indicação diferente na página do evento.
                """
        );

        return new EmailTemplate(assunto, html);
    }

    public EmailTemplate eventoCancelado(
        String nome,
        String evento,
        String pedidoId,
        String orientacaoReembolso
    ) {
        String assunto = "Evento cancelado — " + evento;

        String html = montarTemplate(
            "Cancelamento",
            "×",
            COR_ERRO,
            "O evento foi cancelado",
            nome,
            """
                Lamentamos informar que o evento foi cancelado. Consulte abaixo as informações relacionadas ao seu pedido e ao processo de reembolso.
                """,
            List.of(
                new EmailDetail("Evento", evento, true),
                new EmailDetail("Pedido", pedidoId, false),
                new EmailDetail("Reembolso", orientacaoReembolso, false)
            ),
            "Consultar meu pedido",
            frontendUrl + "/pages/user/profile.html",
            """
                O prazo de estorno pode variar conforme o método de pagamento e a instituição financeira.
                """
        );

        return new EmailTemplate(assunto, html);
    }

    public EmailTemplate lembreteEvento(
        String nome,
        String evento,
        String data,
        String horario,
        String local,
        String setor
    ) {
        String assunto = "É amanhã: " + evento;

        String html = montarTemplate(
            "Lembrete de evento",
            "♫",
            COR_PRINCIPAL,
            "O palco está quase pronto",
            nome,
            """
                Seu evento está chegando. Confira os dados abaixo e deixe seu ingresso preparado para a entrada.
                """,
            List.of(
                new EmailDetail("Evento", evento, true),
                new EmailDetail("Data", data, false),
                new EmailDetail("Horário", horario, false),
                new EmailDetail("Local", local, false),
                new EmailDetail("Setor", setor, false)
            ),
            "Abrir ingresso",
            frontendUrl + "/pages/user/my-tickets.html",
            """
                Recomendamos chegar com antecedência e verificar previamente as regras de acesso do local.
                """
        );

        return new EmailTemplate(assunto, html);
    }

    public EmailTemplate solicitarAvaliacao(
        String nome,
        String evento,
        String pedidoId
    ) {
        String assunto = "Como foi sua experiência com a Music Tour?";

        String linkAvaliacao = frontendUrl
            + "/pages/checkout/review.html?orderId="
            + codificarParametro(pedidoId);

        String html = montarTemplate(
            "Sua opinião importa",
            "★",
            COR_AVALIACAO,
            "Avalie sua experiência",
            nome,
            """
                Queremos saber como foi seu processo de compra na plataforma. Sua avaliação nos ajuda a melhorar cada etapa da experiência.
                """,
            List.of(
                new EmailDetail("Evento", evento, true),
                new EmailDetail("Pedido", pedidoId, false)
            ),
            "Enviar avaliação",
            linkAvaliacao,
            """
                A avaliação é rápida e leva apenas alguns segundos.
                """
        );

        return new EmailTemplate(assunto, html);
    }

    private String montarTemplate(
        String etiqueta,
        String icone,
        String corEstado,
        String titulo,
        String nome,
        String mensagem,
        List<EmailDetail> detalhes,
        String textoBotao,
        String linkBotao,
        String observacao
    ) {
        return """
            <!doctype html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta name="color-scheme" content="dark">
                <title>%s</title>
            </head>
            
            <body style="
                margin: 0;
                padding: 0;
                background-color: #09090b;
                color: #f3f4f6;
                font-family: Montserrat, Arial, Helvetica, sans-serif;
            ">
                <table
                    role="presentation"
                    width="100%%"
                    cellspacing="0"
                    cellpadding="0"
                    border="0"
                    style="
                        width: 100%%;
                        background-color: #09090b;
                    "
                >
                    <tr>
                        <td align="center" style="padding: 32px 16px;">
            
                            <table
                                role="presentation"
                                width="100%%"
                                cellspacing="0"
                                cellpadding="0"
                                border="0"
                                style="
                                    width: 100%%;
                                    max-width: 600px;
                                "
                            >
                                <tr>
                                    <td style="padding: 0 4px 20px;">
                                        <table
                                            role="presentation"
                                            width="100%%"
                                            cellspacing="0"
                                            cellpadding="0"
                                            border="0"
                                        >
                                            <tr>
                                                <td valign="middle">
                                                    <table
                                                        role="presentation"
                                                        width="48"
                                                        height="48"
                                                        cellspacing="0"
                                                        cellpadding="0"
                                                        border="0"
                                                        style="
                                                            width: 48px;
                                                            height: 48px;
                                                            background-color: %s;
                                                            border-radius: 50%%;
                                                        "
                                                    >
                                                        <tr>
                                                            <td
                                                                align="center"
                                                                valign="middle"
                                                                style="
                                                                    color: #ffffff;
                                                                    font-size: 15px;
                                                                    font-weight: 900;
                                                                    letter-spacing: -1px;
                                                                "
                                                            >
                                                                tkts
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
            
                                                <td align="right" valign="middle">
                                                    <span style="
                                                        color: #71717a;
                                                        font-size: 11px;
                                                        font-weight: 700;
                                                        letter-spacing: 1.4px;
                                                        text-transform: uppercase;
                                                    ">
                                                        Music Tour
                                                    </span>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
            
                                <tr>
                                    <td>
                                        <table
                                            role="presentation"
                                            width="100%%"
                                            cellspacing="0"
                                            cellpadding="0"
                                            border="0"
                                            style="
                                                width: 100%%;
                                                background-color: #18181b;
                                                border: 1px solid #27272a;
                                                border-radius: 24px;
                                                overflow: hidden;
                                            "
                                        >
                                            <tr>
                                                <td
                                                    align="center"
                                                    style="
                                                        padding: 40px 32px 20px;
                                                        background-color: #18181b;
                                                    "
                                                >
                                                    <table
                                                        role="presentation"
                                                        width="88"
                                                        height="88"
                                                        cellspacing="0"
                                                        cellpadding="0"
                                                        border="0"
                                                        style="
                                                            width: 88px;
                                                            height: 88px;
                                                            background-color: %s1a;
                                                            border: 1px solid %s55;
                                                            border-radius: 50%%;
                                                        "
                                                    >
                                                        <tr>
                                                            <td
                                                                align="center"
                                                                valign="middle"
                                                                style="
                                                                    color: %s;
                                                                    font-size: 43px;
                                                                    font-weight: 800;
                                                                "
                                                            >
                                                                %s
                                                            </td>
                                                        </tr>
                                                    </table>
            
                                                    <p style="
                                                        margin: 22px 0 10px;
                                                        color: %s;
                                                        font-size: 10px;
                                                        font-weight: 800;
                                                        letter-spacing: 1.7px;
                                                        text-transform: uppercase;
                                                    ">
                                                        %s
                                                    </p>
            
                                                    <h1 style="
                                                        margin: 0;
                                                        color: #ffffff;
                                                        font-size: 30px;
                                                        font-weight: 900;
                                                        line-height: 1.2;
                                                        letter-spacing: -0.8px;
                                                    ">
                                                        %s
                                                    </h1>
                                                </td>
                                            </tr>
            
                                            <tr>
                                                <td style="padding: 12px 32px 34px;">
                                                    <p style="
                                                        margin: 0 0 14px;
                                                        color: #f4f4f5;
                                                        font-size: 16px;
                                                        font-weight: 700;
                                                        line-height: 1.6;
                                                    ">
                                                        Olá, %s!
                                                    </p>
            
                                                    <p style="
                                                        margin: 0 0 26px;
                                                        color: #a1a1aa;
                                                        font-size: 14px;
                                                        font-weight: 500;
                                                        line-height: 1.8;
                                                    ">
                                                        %s
                                                    </p>
            
                                                    %s
            
                                                    <table
                                                        role="presentation"
                                                        cellspacing="0"
                                                        cellpadding="0"
                                                        border="0"
                                                        align="center"
                                                        style="margin: 30px auto 0;"
                                                    >
                                                        <tr>
                                                            <td
                                                                align="center"
                                                                style="
                                                                    background-color: %s;
                                                                    border-radius: 12px;
                                                                "
                                                            >
                                                                <a
                                                                    href="%s"
                                                                    target="_blank"
                                                                    style="
                                                                        display: inline-block;
                                                                        padding: 16px 28px;
                                                                        color: #ffffff;
                                                                        font-size: 13px;
                                                                        font-weight: 800;
                                                                        letter-spacing: 0.4px;
                                                                        text-decoration: none;
                                                                    "
                                                                >
                                                                    %s
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </table>
            
                                                    <table
                                                        role="presentation"
                                                        width="100%%"
                                                        cellspacing="0"
                                                        cellpadding="0"
                                                        border="0"
                                                        style="
                                                            width: 100%%;
                                                            margin-top: 28px;
                                                            background-color: #111113;
                                                            border-left: 3px solid %s;
                                                            border-radius: 8px;
                                                        "
                                                    >
                                                        <tr>
                                                            <td style="padding: 16px 18px;">
                                                                <p style="
                                                                    margin: 0;
                                                                    color: #71717a;
                                                                    font-size: 11px;
                                                                    font-weight: 600;
                                                                    line-height: 1.7;
                                                                ">
                                                                    %s
                                                                </p>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
            
                                <tr>
                                    <td align="center" style="padding: 22px 16px 0;">
                                        <p style="
                                            margin: 0 0 7px;
                                            color: #52525b;
                                            font-size: 10px;
                                            font-weight: 700;
                                            letter-spacing: 1.3px;
                                            text-transform: uppercase;
                                        ">
                                            Music Tour
                                        </p>
            
                                        <p style="
                                            margin: 0;
                                            color: #3f3f46;
                                            font-size: 10px;
                                            line-height: 1.6;
                                        ">
                                            Esta mensagem foi enviada automaticamente. Não é necessário respondê-la.
                                        </p>
                                    </td>
                                </tr>
                            </table>
            
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(
            escaparHtml(titulo),
            COR_PRINCIPAL,
            corEstado,
            corEstado,
            corEstado,
            escaparHtml(icone),
            corEstado,
            escaparHtml(etiqueta),
            escaparHtml(titulo),
            escaparHtml(nome),
            formatarTexto(mensagem),
            montarDetalhes(detalhes, corEstado),
            COR_PRINCIPAL,
            escaparAtributo(linkBotao),
            escaparHtml(textoBotao),
            corEstado,
            formatarTexto(observacao)
        );
    }

    private String montarDetalhes(
        List<EmailDetail> detalhes,
        String corEstado
    ) {
        if (detalhes == null || detalhes.isEmpty()) {
            return "";
        }

        StringBuilder linhas = new StringBuilder();

        for (int i = 0; i < detalhes.size(); i++) {
            EmailDetail detalhe = detalhes.get(i);
            boolean ultimaLinha = i == detalhes.size() - 1;

            String corValor = detalhe.destaque()
                ? corEstado
                : "#f4f4f5";

            String borda = ultimaLinha
                ? ""
                : "border-bottom: 1px solid #303036;";

            linhas.append("""
                <tr>
                    <td style="
                        padding: 17px 19px;
                        %s
                    ">
                        <p style="
                            margin: 0 0 6px;
                            color: #71717a;
                            font-size: 9px;
                            font-weight: 800;
                            letter-spacing: 1.3px;
                            text-transform: uppercase;
                        ">
                            %s
                        </p>
                
                        <p style="
                            margin: 0;
                            color: %s;
                            font-size: 14px;
                            font-weight: 750;
                            line-height: 1.5;
                            word-break: break-word;
                        ">
                            %s
                        </p>
                    </td>
                </tr>
                """.formatted(
                borda,
                escaparHtml(detalhe.rotulo()),
                corValor,
                escaparHtml(detalhe.valor())
            ));
        }

        return """
            <table
                role="presentation"
                width="100%%"
                cellspacing="0"
                cellpadding="0"
                border="0"
                style="
                    width: 100%%;
                    background-color: #111113;
                    border: 1px solid #303036;
                    border-radius: 14px;
                    overflow: hidden;
                "
            >
                %s
            </table>
            """.formatted(linhas);
    }

    private String formatarTexto(String valor) {
        return escaparHtml(valor)
            .replace("\r\n", "<br>")
            .replace("\n", "<br>");
    }

    private String escaparHtml(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    private String escaparAtributo(String valor) {
        return escaparHtml(valor);
    }

    private String codificarParametro(String valor) {
        if (valor == null) {
            return "";
        }

        return java.net.URLEncoder.encode(
            valor,
            java.nio.charset.StandardCharsets.UTF_8
        );
    }

    private String removerBarraFinal(String valor) {
        if (valor == null || valor.isBlank()) {
            return "";
        }

        return valor.endsWith("/")
            ? valor.substring(0, valor.length() - 1)
            : valor;
    }
}