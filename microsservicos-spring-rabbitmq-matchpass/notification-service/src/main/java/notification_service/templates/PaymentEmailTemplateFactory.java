package notification_service.templates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentEmailTemplateFactory {

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
                            width="110"
                            valign="middle"
                            style="
                                width: 110px;
                                padding: 15px 0 15px 15px;
                            "
                        >
                            <img
                                src="{{IMAGEM_URL}}"
                                width="82"
                                height="82"
                                alt="{{IMAGEM_ALT}}"
                                style="
                                    display: block;
                                    width: 82px;
                                    height: 82px;
                                    border: 0;
                                    border-radius: 10px;
                                    object-fit: cover;
                                "
                            >
                        </td>
        
                        <td
                            valign="middle"
                            style="
                                padding: 15px 10px;
                                font-family: Montserrat, Arial, sans-serif;
                            "
                        >
                            <p
                                style="
                                    margin: 0 0 6px;
                                    color: #7a7f8c;
                                    font-size: 9px;
                                    line-height: 1.3;
                                "
                            >
                                Item
                            </p>
        
                            <p
                                style="
                                    margin: 0 0 8px;
                                    color: #080808;
                                    font-size: 15px;
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
                                    font-size: 9px;
                                    line-height: 1.5;
                                "
                            >
                                {{DESCRICAO_ITEM}}
                            </p>
        
                            <p
                                style="
                                    margin: 0;
                                    color: #6b707d;
                                    font-size: 9px;
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
                                padding: 15px 18px 15px 8px;
                                font-family: Montserrat, Arial, sans-serif;
                            "
                        >
                            <p
                                style="
                                    margin: 0 0 6px;
                                    color: #7a7f8c;
                                    font-size: 9px;
                                    line-height: 1.3;
                                "
                            >
                                Preço
                            </p>
        
                            <p
                                style="
                                    margin: 0;
                                    color: #101329;
                                    font-size: 15px;
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
        
                            <!-- CABEÇALHO AZUL -->
                            <tr>
                                <td
                                    align="center"
                                    background="{{BACKGROUND_CABECALHO_URL}}"
                                    style="
                                        padding: 52px 35px 54px;
                                        background-color: #5e71ec;
                                        background-image:
                                            url('{{BACKGROUND_CABECALHO_URL}}');
                                        background-repeat: no-repeat;
                                        background-position: center;
                                        background-size: cover;
                                        color: #ffffff;
                                    "
                                >
                                    <img
                                        src="{{ILUSTRACAO_CABECALHO_URL}}"
                                        width="112"
                                        alt="Pagamento pendente"
                                        style="
                                            display: block;
                                            width: 112px;
                                            max-width: 112px;
                                            height: auto;
                                            margin: 0 auto 27px;
                                            border: 0;
                                            outline: none;
                                            text-decoration: none;
                                        "
                                    >
        
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
                                        src="{{ILUSTRACAO_RODAPE_URL}}"
                                        width="62"
                                        alt="Music Tour"
                                        style="
                                            display: block;
                                            width: 62px;
                                            max-width: 62px;
                                            height: auto;
                                            margin: 0 auto;
                                            border: 0;
                                            outline: none;
                                        "
                                    >
        
                                    {{REDES_SOCIAIS}}
        
                                    <p
                                        style="
                                            margin: 23px 0 7px;
                                            color: #737987;
                                            font-size: 9px;
                                            line-height: 1.5;
                                            text-align: center;
                                        "
                                    >
                                        Music Tour — Plataforma de eventos
                                        e ingressos
                                    </p>
        
                                    <p
                                        style="
                                            margin: 0 0 8px;
                                            color: #8b909b;
                                            font-size: 9px;
                                            line-height: 1.5;
                                            text-align: center;
                                        "
                                    >
                                        Política de Privacidade |
                                        Gerenciar notificações
                                    </p>
        
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
    private final String backgroundCabecalhoUrl;
    private final String ilustracaoCabecalhoUrl;
    private final String ilustracaoRodapeUrl;
    private final String facebookIconUrl;
    private final String linkedinIconUrl;
    private final String xIconUrl;
    private final String instagramIconUrl;
    private final String youtubeIconUrl;
    private final String pinterestIconUrl;

    public PaymentEmailTemplateFactory(
        @Value("${notification.email.assets.payment-background-url}") String backgroundCabecalhoUrl,

        @Value("${notification.email.assets.payment-header-illustration-url}") String ilustracaoCabecalhoUrl,

        @Value("${notification.email.assets.payment-footer-illustration-url}") String ilustracaoRodapeUrl,

        @Value("${notification.email.assets.facebook-icon-url}") String facebookIconUrl,

        @Value("${notification.email.assets.linkedin-icon-url}") String linkedinIconUrl,

        @Value("${notification.email.assets.x-icon-url}") String xIconUrl,

        @Value("${notification.email.assets.instagram-icon-url}") String instagramIconUrl,

        @Value("${notification.email.assets.youtube-icon-url}") String youtubeIconUrl,

        @Value("${notification.email.assets.pinterest-icon-url}") String pinterestIconUrl
    ) {
        this.backgroundCabecalhoUrl = backgroundCabecalhoUrl;
        this.ilustracaoCabecalhoUrl = ilustracaoCabecalhoUrl;
        this.ilustracaoRodapeUrl = ilustracaoRodapeUrl;
        this.facebookIconUrl = facebookIconUrl;
        this.linkedinIconUrl = linkedinIconUrl;
        this.xIconUrl = xIconUrl;
        this.instagramIconUrl = instagramIconUrl;
        this.youtubeIconUrl = youtubeIconUrl;
        this.pinterestIconUrl = pinterestIconUrl;
    }

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
            "{{BACKGROUND_CABECALHO_URL}}",
            escaparAtributo(backgroundCabecalhoUrl)
        ).replace(
            "{{ILUSTRACAO_CABECALHO_URL}}",
            escaparAtributo(ilustracaoCabecalhoUrl)
        ).replace(
            "{{ILUSTRACAO_RODAPE_URL}}",
            escaparAtributo(ilustracaoRodapeUrl)
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
        ).replace(
            "{{REDES_SOCIAIS}}",
            montarRedesSociais()
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
                "{{IMAGEM_URL}}",
                escaparAtributo(item.imagemUrl())
            ).replace(
                "{{IMAGEM_ALT}}",
                escaparAtributo(item.nome())
            ).replace(
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

    private String montarRedesSociais() {
        return """
            <table
                role="presentation"
                cellspacing="0"
                cellpadding="0"
                border="0"
                align="center"
                style="margin: 22px auto 0;"
            >
                <tr>
                    %s
                    %s
                    %s
                    %s
                    %s
                    %s
                </tr>
            </table>
            """.formatted(
            montarIconeSocial(
                "Facebook",
                facebookIconUrl,
                "https://facebook.com"
            ),
            montarIconeSocial(
                "LinkedIn",
                linkedinIconUrl,
                "https://linkedin.com"
            ),
            montarIconeSocial(
                "X",
                xIconUrl,
                "https://x.com"
            ),
            montarIconeSocial(
                "Instagram",
                instagramIconUrl,
                "https://instagram.com"
            ),
            montarIconeSocial(
                "YouTube",
                youtubeIconUrl,
                "https://youtube.com"
            ),
            montarIconeSocial(
                "Pinterest",
                pinterestIconUrl,
                "https://pinterest.com"
            )
        );
    }

    private String montarIconeSocial(String nome, String imagemUrl, String link) {
        if (imagemUrl == null || imagemUrl.isBlank()) {
            return "";
        }

        return """
            <td
                align="center"
                valign="middle"
                style="padding: 0 5px;"
            >
                <a
                    href="%s"
                    target="_blank"
                    title="%s"
                    style="
                        display: inline-block;
                        text-decoration: none;
                    "
                >
                    <img
                        src="%s"
                        width="24"
                        height="24"
                        alt="%s"
                        style="
                            display: block;
                            width: 24px;
                            height: 24px;
                            border: 0;
                            outline: none;
                            text-decoration: none;
                        "
                    >
                </a>
            </td>
            """.formatted(
            escaparAtributo(link),
            escaparAtributo(nome),
            escaparAtributo(imagemUrl),
            escaparAtributo(nome)
        );
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