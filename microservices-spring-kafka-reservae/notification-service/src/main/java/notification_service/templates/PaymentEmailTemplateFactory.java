package notification_service.templates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentEmailTemplateFactory {

    private final String paymentBackgroundUrl;
    private final String paymentHeaderIllustrationUrl;
    private final String paymentFooterIllustrationUrl;
    private final String instagramIconUrl;

    public PaymentEmailTemplateFactory(
        @Value("${notification.email.assets.payment-background-url}") String paymentBackgroundUrl,
        @Value("${notification.email.assets.payment-header-illustration-url}") String paymentHeaderIllustrationUrl,
        @Value("${notification.email.assets.payment-footer-illustration-url}") String paymentFooterIllustrationUrl,
        @Value("${notification.email.assets.instagram-icon-url}") String instagramIconUrl
    ) {
        this.paymentBackgroundUrl = paymentBackgroundUrl;
        this.paymentHeaderIllustrationUrl = paymentHeaderIllustrationUrl;
        this.paymentFooterIllustrationUrl = paymentFooterIllustrationUrl;
        this.instagramIconUrl = instagramIconUrl;
    }

    private static final String ITEM_TEMPLATE = """
        <tr>
            <td style="padding: 0 0 16px;">
                <table
                    role="presentation"
                    width="100%"
                    cellspacing="0"
                    cellpadding="0"
                    border="0"
                    style="
                        width: 100%;
                        background-color: #ffffff;
                        border-radius: 15px;
                        border-collapse: separate;
                        box-shadow: 0 4px 15px rgba(35, 47, 85, 0.06);
                    "
                >
                    <tr>
                        <td
                            valign="middle"
                            style="
                                padding: 18px 20px;
                                font-family: Montserrat, Arial, sans-serif;
                            "
                        >
                            <p
                                style="
                                    margin: 0 0 6px;
                                    color: #7a7f8c;
                                    font-size: 10px;
                                    line-height: 1.3;
                                "
                            >
                                Item
                            </p>
        
                            <p
                                style="
                                    margin: 0 0 8px;
                                    color: #080808;
                                    font-size: 16px;
                                    line-height: 1.35;
                                    font-weight: 800;
                                "
                            >
                                {{NOME_ITEM}}
                            </p>
        
                            <p
                                style="
                                    margin: 0 0 4px;
                                    color: #6b707d;
                                    font-size: 11px;
                                    line-height: 1.5;
                                "
                            >
                                {{DESCRICAO_ITEM}}
                            </p>
        
                            <p
                                style="
                                    margin: 0;
                                    color: #6b707d;
                                    font-size: 11px;
                                    line-height: 1.5;
                                "
                            >
                                Quantidade: {{QUANTIDADE}}
                                &nbsp;&nbsp; Total: {{PRECO_TOTAL}}
                            </p>
                        </td>
        
                        <td
                            width="135"
                            valign="middle"
                            align="right"
                            style="
                                width: 135px;
                                padding: 18px 20px 18px 8px;
                                font-family: Montserrat, Arial, sans-serif;
                            "
                        >
                            <p
                                style="
                                    margin: 0 0 6px;
                                    color: #7a7f8c;
                                    font-size: 10px;
                                    line-height: 1.3;
                                "
                            >
                                Preço
                            </p>
        
                            <p
                                style="
                                    margin: 0;
                                    color: #101329;
                                    font-size: 16px;
                                    line-height: 1.35;
                                    font-weight: 800;
                                "
                            >
                                {{PRECO_UNITARIO}}
                            </p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        """;

    private static final String TEMPLATE = """
        <!doctype html>
        <html lang="pt-BR">
        <head>
            <meta charset="UTF-8">
            <meta
                name="viewport"
                content="width=device-width, initial-scale=1.0"
            >
            <meta name="color-scheme" content="light">
            <meta name="supported-color-schemes" content="light">
        
            <title>{{TITULO_DOCUMENTO}}</title>
        
            <link
                href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700;800&display=swap"
                rel="stylesheet"
            >
        </head>
        
        <body
            style="
                margin: 0;
                padding: 0;
                background-color: #cfd3da;
                color: #090909;
                font-family: Montserrat, Arial, Helvetica, sans-serif;
            "
        >
            <table
                role="presentation"
                width="100%"
                cellspacing="0"
                cellpadding="0"
                border="0"
                style="
                    width: 100%;
                    background-color: #cfd3da;
                "
            >
                <tr>
                    <td align="center" style="padding: 36px 12px;">
                        <table
                            role="presentation"
                            width="100%"
                            cellspacing="0"
                            cellpadding="0"
                            border="0"
                            style="
                                width: 100%;
                                max-width: 650px;
                                background-color: #ffffff;
                                border-collapse: separate;
                                box-shadow: 0 10px 35px rgba(23, 31, 64, 0.15);
                            "
                        >
        
                            <!-- CABEÇALHO -->
                            <tr>
                                <td
                                    align="center"
                                    background="{{PAYMENT_BACKGROUND_URL}}"
                                    style="
                                        padding: 44px 35px 50px;
                                        background-color: #5e71ec;
                                        background-image: url('{{PAYMENT_BACKGROUND_URL}}');
                                        background-repeat: no-repeat;
                                        background-position: center;
                                        background-size: cover;
                                        color: #ffffff;
                                    "
                                >
                                    <img
                                        src="{{PAYMENT_HEADER_ILLUSTRATION_URL}}"
                                        alt="Reservae"
                                        width="180"
                                        style="
                                            display: block;
                                            width: 180px;
                                            max-width: 70%;
                                            height: auto;
                                            margin: 0 auto 24px;
                                            border: 0;
                                            outline: none;
                                            text-decoration: none;
                                        "
                                    >
        
                                    <p
                                        style="
                                            margin: 0 0 18px;
                                            color: #eef0ff;
                                            font-size: 14px;
                                            line-height: 1.4;
                                            font-weight: 700;
                                            text-align: center;
                                            letter-spacing: 1.8px;
                                            text-transform: uppercase;
                                        "
                                    >
                                        Reservae
                                    </p>
        
                                    <h1
                                        style="
                                            margin: 0;
                                            color: #ffffff;
                                            font-size: 54px;
                                            line-height: 1.06;
                                            font-weight: 800;
                                            letter-spacing: -1.8px;
                                            text-align: center;
                                        "
                                    >
                                        Pagamento<br>
                                        Pendente
                                    </h1>
        
                                    <p
                                        style="
                                            margin: 31px 0 8px;
                                            color: #ffffff;
                                            font-size: 18px;
                                            line-height: 1.4;
                                            font-weight: 700;
                                            text-align: center;
                                        "
                                    >
                                        Valor pendente:
                                        {{VALOR_PENDENTE}}
                                    </p>
        
                                    <p
                                        style="
                                            margin: 0;
                                            color: #eef0ff;
                                            font-size: 13px;
                                            line-height: 1.5;
                                            font-weight: 500;
                                            text-align: center;
                                        "
                                    >
                                        Pedido: #{{PEDIDO_ID}}
                                        &nbsp;|&nbsp;
                                        Vencimento: {{DATA_VENCIMENTO}}
                                    </p>
                                </td>
                            </tr>
        
                            <!-- SAUDAÇÃO -->
                            <tr>
                                <td
                                    align="center"
                                    style="
                                        padding: 50px 48px 48px;
                                        background-color: #ffffff;
                                    "
                                >
                                    <h2
                                        style="
                                            margin: 0 0 17px;
                                            color: #080808;
                                            font-size: 30px;
                                            line-height: 1.25;
                                            font-weight: 800;
                                            text-align: center;
                                        "
                                    >
                                        Olá, {{NOME}}!
                                    </h2>
        
                                    <p
                                        style="
                                            margin: 0 auto;
                                            max-width: 500px;
                                            color: #121212;
                                            font-size: 18px;
                                            line-height: 1.55;
                                            font-weight: 500;
                                            text-align: center;
                                        "
                                    >
                                        Este é um lembrete de que o pagamento
                                        do pedido
                                        <strong>#{{PEDIDO_ID}}</strong>,
                                        com vencimento em
                                        <strong>{{DATA_VENCIMENTO}}</strong>,
                                        ainda está pendente.
                                    </p>
        
                                    <p
                                        style="
                                            margin: 28px auto 0;
                                            max-width: 450px;
                                            color: #4f4f52;
                                            font-size: 13px;
                                            line-height: 1.7;
                                            font-weight: 400;
                                            text-align: center;
                                        "
                                    >
                                        Realize o pagamento dentro do prazo
                                        para manter a reserva dos seus
                                        ingressos. Confira abaixo os dados
                                        do pedido.
                                    </p>
                                </td>
                            </tr>
        
                            <!-- RESUMO DOS ITENS -->
                            <tr>
                                <td
                                    style="
                                        padding: 43px 34px 38px;
                                        background-color: #f4f6fb;
                                    "
                                >
                                    <h2
                                        style="
                                            margin: 0 0 31px;
                                            color: #090909;
                                            font-size: 34px;
                                            line-height: 1.2;
                                            font-weight: 800;
                                            text-align: center;
                                        "
                                    >
                                        Resumo dos itens
                                    </h2>
        
                                    <table
                                        role="presentation"
                                        width="100%"
                                        cellspacing="0"
                                        cellpadding="0"
                                        border="0"
                                        style="width: 100%;"
                                    >
                                        {{ITENS}}
                                    </table>
        
                                    <table
                                        role="presentation"
                                        width="100%"
                                        cellspacing="0"
                                        cellpadding="0"
                                        border="0"
                                        style="
                                            width: 100%;
                                            margin-top: 14px;
                                        "
                                    >
                                        <tr>
                                            <td
                                                align="right"
                                                style="
                                                    padding: 12px 5px 0;
                                                    font-family: Montserrat, Arial, sans-serif;
                                                "
                                            >
                                                <p
                                                    style="
                                                        margin: 0 0 7px;
                                                        color: #666c7a;
                                                        font-size: 10px;
                                                        line-height: 1.4;
                                                    "
                                                >
                                                    Valor total do pedido
                                                </p>
        
                                                <p
                                                    style="
                                                        margin: 0;
                                                        color: #11142b;
                                                        font-size: 30px;
                                                        line-height: 1.1;
                                                        font-weight: 800;
                                                    "
                                                >
                                                    {{VALOR_TOTAL}}
                                                </p>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
        
                            <!-- TEXTO FINAL -->
                            <tr>
                                <td
                                    align="center"
                                    style="
                                        padding: 49px 42px 51px;
                                        background-color: #ffffff;
                                    "
                                >
                                    <p
                                        style="
                                            margin: 0 auto;
                                            max-width: 520px;
                                            color: #111111;
                                            font-size: 24px;
                                            line-height: 1.5;
                                            font-weight: 500;
                                            text-align: center;
                                        "
                                    >
                                        Caso o pagamento já tenha sido
                                        realizado, desconsidere esta mensagem.
                                    </p>
        
                                    <p
                                        style="
                                            margin: 20px auto 0;
                                            max-width: 510px;
                                            color: #181818;
                                            font-size: 18px;
                                            line-height: 1.55;
                                            font-weight: 500;
                                            text-align: center;
                                        "
                                    >
                                        Caso contrário, conclua o pagamento
                                        para garantir seus ingressos.
                                    </p>
        
                                    <p
                                        style="
                                            margin: 21px 0 0;
                                            color: #55565b;
                                            font-size: 12px;
                                            line-height: 1.6;
                                            text-align: center;
                                        "
                                    >
                                        Agradecemos pela sua atenção.
                                    </p>
        
                                    <!-- BOTÕES -->
                                    <table
                                        role="presentation"
                                        cellspacing="0"
                                        cellpadding="0"
                                        border="0"
                                        align="center"
                                        style="margin: 35px auto 0;"
                                    >
                                        <tr>
                                            <td
                                                align="center"
                                                style="padding-right: 7px;"
                                            >
                                                <a
                                                    href="{{LINK_PAGAMENTO}}"
                                                    target="_blank"
                                                    style="
                                                        display: inline-block;
                                                        min-width: 160px;
                                                        padding: 15px 22px;
                                                        background-color: #1746dd;
                                                        border: 2px solid #1746dd;
                                                        border-radius: 9px;
                                                        color: #ffffff;
                                                        font-size: 14px;
                                                        line-height: 1.2;
                                                        font-weight: 700;
                                                        text-align: center;
                                                        text-decoration: none;
                                                    "
                                                >
                                                    Pagar agora
                                                </a>
                                            </td>
        
                                            <td
                                                align="center"
                                                style="padding-left: 7px;"
                                            >
                                                <a
                                                    href="{{LINK_PEDIDO}}"
                                                    target="_blank"
                                                    style="
                                                        display: inline-block;
                                                        min-width: 160px;
                                                        padding: 15px 22px;
                                                        background-color: #ffffff;
                                                        border: 2px solid #1746dd;
                                                        border-radius: 9px;
                                                        color: #1746dd;
                                                        font-size: 14px;
                                                        line-height: 1.2;
                                                        font-weight: 700;
                                                        text-align: center;
                                                        text-decoration: none;
                                                    "
                                                >
                                                    Ver pedido
                                                </a>
                                            </td>
                                        </tr>
                                    </table>
        
                                    <p
                                        style="
                                            margin: 41px 0 0;
                                            color: #151515;
                                            font-size: 14px;
                                            line-height: 1.65;
                                            text-align: center;
                                        "
                                    >
                                        Atenciosamente,<br>
                                        <strong>{{NOME_REMETENTE}}</strong>
                                    </p>
                                </td>
                            </tr>
        
                            <!-- RODAPÉ -->
                            <tr>
                                <td
                                    align="center"
                                    style="
                                        padding: 38px 28px 34px;
                                        background-color: #f1f3f8;
                                    "
                                >
                                    <img
                                        src="{{PAYMENT_FOOTER_ILLUSTRATION_URL}}"
                                        alt="Reservae"
                                        width="120"
                                        style="
                                            display: block;
                                            width: 120px;
                                            max-width: 60%;
                                            height: auto;
                                            margin: 0 auto 18px;
                                            border: 0;
                                            outline: none;
                                            text-decoration: none;
                                        "
                                    >
        
                                    <p
                                        style="
                                            margin: 0 0 7px;
                                            color: #737987;
                                            font-size: 10px;
                                            line-height: 1.5;
                                            text-align: center;
                                            font-weight: 700;
                                        "
                                    >
                                        Reservae — Plataforma de eventos
                                        e ingressos
                                    </p>
        
                                    <p
                                        style="
                                            margin: 0 0 12px;
                                            color: #8b909b;
                                            font-size: 9px;
                                            line-height: 1.5;
                                            text-align: center;
                                        "
                                    >
                                        Política de Privacidade |
                                        Gerenciar notificações
                                    </p>
        
                                    <table
                                        role="presentation"
                                        cellspacing="0"
                                        cellpadding="0"
                                        border="0"
                                        align="center"
                                        style="margin: 0 auto 12px;"
                                    >
                                        <tr>
                                            <td align="center">
                                                <a
                                                    href="{{LINK_INSTAGRAM}}"
                                                    target="_blank"
                                                    style="text-decoration: none;"
                                                >
                                                    <img
                                                        src="{{INSTAGRAM_ICON_URL}}"
                                                        alt="Instagram"
                                                        width="22"
                                                        height="22"
                                                        style="
                                                            display: block;
                                                            width: 22px;
                                                            height: 22px;
                                                            border: 0;
                                                            outline: none;
                                                            text-decoration: none;
                                                        "
                                                    >
                                                </a>
                                            </td>
                                        </tr>
                                    </table>
        
                                    <p
                                        style="
                                            margin: 0;
                                            color: #a0a4ad;
                                            font-size: 8px;
                                            line-height: 1.5;
                                            text-align: center;
                                        "
                                    >
                                        Esta mensagem foi enviada
                                        automaticamente.
                                    </p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """;

    public EmailTemplate criarPagamentoPendente(
        String nomeDestinatario,
        String pedidoId,
        String dataVencimento,
        String valorPendente,
        List<EmailItem> itens,
        String valorTotal,
        String linkPagamento,
        String linkPedido,
        String nomeRemetente
    ) {
        String assunto = "Pagamento pendente — Pedido #" + pedidoId;

        String html = TEMPLATE.replace(
            "{{TITULO_DOCUMENTO}}",
            escaparHtml(assunto)
        ).replace(
            "{{PAYMENT_BACKGROUND_URL}}",
            escaparAtributo(paymentBackgroundUrl)
        ).replace(
            "{{PAYMENT_HEADER_ILLUSTRATION_URL}}",
            escaparAtributo(paymentHeaderIllustrationUrl)
        ).replace(
            "{{PAYMENT_FOOTER_ILLUSTRATION_URL}}",
            escaparAtributo(paymentFooterIllustrationUrl)
        ).replace(
            "{{INSTAGRAM_ICON_URL}}",
            escaparAtributo(instagramIconUrl)
        ).replace(
            "{{LINK_INSTAGRAM}}",
            "#"
        ).replace(
            "{{NOME}}",
            escaparHtml(nomeDestinatario)
        ).replace(
            "{{PEDIDO_ID}}",
            escaparHtml(pedidoId)
        ).replace(
            "{{DATA_VENCIMENTO}}",
            escaparHtml(dataVencimento)
        ).replace(
            "{{VALOR_PENDENTE}}",
            escaparHtml(valorPendente)
        ).replace(
            "{{ITENS}}",
            montarItens(itens)
        ).replace(
            "{{VALOR_TOTAL}}",
            escaparHtml(valorTotal)
        ).replace(
            "{{LINK_PAGAMENTO}}",
            escaparAtributo(linkPagamento)
        ).replace(
            "{{LINK_PEDIDO}}",
            escaparAtributo(linkPedido)
        ).replace(
            "{{NOME_REMETENTE}}",
            escaparHtml(nomeRemetente)
        );

        return new EmailTemplate(
            assunto,
            html
        );
    }

    private String montarItens(List<EmailItem> itens) {
        if (itens == null || itens.isEmpty()) {
            return """
                <tr>
                    <td
                        align="center"
                        style="
                            padding: 32px 20px;
                            color: #7c8190;
                            font-family: Montserrat, Arial, sans-serif;
                            font-size: 14px;
                        "
                    >
                        Nenhum item disponível para exibição.
                    </td>
                </tr>
                """;
        }

        StringBuilder html = new StringBuilder();

        for (EmailItem item : itens) {
            String linha = ITEM_TEMPLATE.replace(
                "{{NOME_ITEM}}",
                escaparHtml(item.nome())
            ).replace(
                "{{DESCRICAO_ITEM}}",
                escaparHtml(item.descricao())
            ).replace(
                "{{QUANTIDADE}}",
                String.valueOf(item.quantidade())
            ).replace(
                "{{PRECO_UNITARIO}}",
                escaparHtml(item.precoUnitario())
            ).replace(
                "{{PRECO_TOTAL}}",
                escaparHtml(item.precoTotal())
            );

            html.append(linha);
        }

        return html.toString();
    }

    private String escaparHtml(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.replace(
            "&",
            "&amp;"
        ).replace(
            "<",
            "&lt;"
        ).replace(
            ">",
            "&gt;"
        ).replace(
            "\"",
            "&quot;"
        ).replace(
            "'",
            "&#39;"
        );
    }

    private String escaparAtributo(String valor) {
        return escaparHtml(valor);
    }
}