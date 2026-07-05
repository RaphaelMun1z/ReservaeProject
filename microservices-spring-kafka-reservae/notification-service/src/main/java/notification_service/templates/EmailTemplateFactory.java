package notification_service.templates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class EmailTemplateFactory {

    private static final String COR_PRINCIPAL = "#5b6ee1";
    private static final String COR_SUCESSO = "#10b981";
    private static final String COR_ATENCAO = "#f59e0b";
    private static final String COR_ERRO = "#ef4444";
    private static final String COR_INFORMACAO = "#3b82f6";
    private static final String COR_AVALIACAO = "#facc15";

    private final String frontendUrl;

    public EmailTemplateFactory(@Value("${notification.frontend-url:http://localhost:5500}") String frontendUrl) {
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
            "Compra confirmada",
            "✓",
            COR_SUCESSO,
            "Tudo certo!",
            nome,
            """
                Sua compra foi confirmada com sucesso. Os ingressos já estão vinculados à sua conta e disponíveis para consulta.
                """,
            List.of(
                new EmailDetail(
                    "Pedido",
                    pedidoId,
                    false
                ),
                new EmailDetail(
                    "Evento",
                    evento,
                    true
                ),
                new EmailDetail(
                    "Data",
                    dataEvento,
                    false
                ),
                new EmailDetail(
                    "Valor total",
                    valorTotal,
                    false
                )
            ),
            "Ver ingressos",
            frontendUrl + "/pages/user/my-tickets.html",
            """
                Guarde este e-mail como comprovante da sua compra.
                """
        );

        return new EmailTemplate(
            assunto,
            html
        );
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
                new EmailDetail(
                    "Pedido",
                    pedidoId,
                    false
                ),
                new EmailDetail(
                    "Evento",
                    evento,
                    true
                ),
                new EmailDetail(
                    "Valor",
                    valorTotal,
                    false
                ),
                new EmailDetail(
                    "Prazo",
                    prazoPagamento,
                    true
                )
            ),
            "Finalizar pagamento",
            linkPagamento,
            """
                Após o prazo informado, a reserva poderá ser liberada automaticamente.
                """
        );

        return new EmailTemplate(
            assunto,
            html
        );
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
                new EmailDetail(
                    "Pedido",
                    pedidoId,
                    false
                ),
                new EmailDetail(
                    "Evento",
                    evento,
                    true
                ),
                new EmailDetail(
                    "Motivo",
                    motivo,
                    false
                )
            ),
            "Tentar novamente",
            linkPagamento,
            """
                Nenhuma cobrança foi confirmada para esta tentativa.
                """
        );

        return new EmailTemplate(
            assunto,
            html
        );
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
                new EmailDetail(
                    "Pedido",
                    pedidoId,
                    false
                ),
                new EmailDetail(
                    "Evento",
                    evento,
                    true
                ),
                new EmailDetail(
                    "Setor",
                    setor,
                    false
                ),
                new EmailDetail(
                    "Quantidade",
                    quantidade,
                    false
                )
            ),
            "Abrir minha carteira",
            frontendUrl + "/pages/user/my-tickets.html",
            """
                Evite compartilhar o QR Code. Ele é pessoal e será validado na entrada.
                """
        );

        return new EmailTemplate(
            assunto,
            html
        );
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
                new EmailDetail(
                    "Evento",
                    evento,
                    true
                ),
                new EmailDetail(
                    "Informação anterior",
                    informacaoAnterior,
                    false
                ),
                new EmailDetail(
                    "Nova informação",
                    novaInformacao,
                    true
                )
            ),
            "Ver detalhes do evento",
            frontendUrl + "/pages/public/shows.html",
            """
                Seu ingresso continua válido, salvo indicação diferente na página do evento.
                """
        );

        return new EmailTemplate(
            assunto,
            html
        );
    }

    public EmailTemplate eventoCancelado(String nome, String evento, String pedidoId, String orientacaoReembolso) {
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
                new EmailDetail(
                    "Evento",
                    evento,
                    true
                ),
                new EmailDetail(
                    "Pedido",
                    pedidoId,
                    false
                ),
                new EmailDetail(
                    "Reembolso",
                    orientacaoReembolso,
                    false
                )
            ),
            "Consultar meu pedido",
            frontendUrl + "/pages/user/profile.html",
            """
                O prazo de estorno pode variar conforme o método de pagamento e a instituição financeira.
                """
        );

        return new EmailTemplate(
            assunto,
            html
        );
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
                new EmailDetail(
                    "Evento",
                    evento,
                    true
                ),
                new EmailDetail(
                    "Data",
                    data,
                    false
                ),
                new EmailDetail(
                    "Horário",
                    horario,
                    false
                ),
                new EmailDetail(
                    "Local",
                    local,
                    false
                ),
                new EmailDetail(
                    "Setor",
                    setor,
                    false
                )
            ),
            "Abrir ingresso",
            frontendUrl + "/pages/user/my-tickets.html",
            """
                Recomendamos chegar com antecedência e verificar previamente as regras de acesso do local.
                """
        );

        return new EmailTemplate(
            assunto,
            html
        );
    }

    public EmailTemplate solicitarAvaliacao(String nome, String evento, String pedidoId) {
        String assunto = "Como foi sua experiência com a Music Tour?";

        String linkAvaliacao = frontendUrl + "/pages/checkout/review.html?orderId=" + codificarParametro(pedidoId);

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
                new EmailDetail(
                    "Evento",
                    evento,
                    true
                ),
                new EmailDetail(
                    "Pedido",
                    pedidoId,
                    false
                )
            ),
            "Enviar avaliação",
            linkAvaliacao,
            """
                A avaliação é rápida e leva apenas alguns segundos.
                """
        );

        return new EmailTemplate(
            assunto,
            html
        );
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
                <meta name="color-scheme" content="light">
                <title>%s</title>
            </head>
            
            <body style="
                margin: 0;
                padding: 0;
                background-color: #dfe3ea;
                color: #111111;
                font-family: Arial, Helvetica, sans-serif;
            ">
                <table
                    role="presentation"
                    width="100%%"
                    cellspacing="0"
                    cellpadding="0"
                    border="0"
                    style="
                        width: 100%%;
                        background-color: #dfe3ea;
                    "
                >
                    <tr>
                        <td align="center" style="padding: 30px 12px;">
            
                            <table
                                role="presentation"
                                width="100%%"
                                cellspacing="0"
                                cellpadding="0"
                                border="0"
                                style="
                                    width: 100%%;
                                    max-width: 650px;
                                    background-color: #ffffff;
                                    box-shadow: 0 8px 30px rgba(41, 50, 90, 0.15);
                                "
                            >
            
                                <tr>
                                    <td
                                        align="center"
                                        style="
                                            padding: 48px 30px;
                                            background-color: %s;
                                            color: #ffffff;
                                        "
                                    >
                                        <table
                                            role="presentation"
                                            cellspacing="0"
                                            cellpadding="0"
                                            border="0"
                                            style="margin: 0 auto 24px;"
                                        >
                                            <tr>
                                                <td
                                                    align="center"
                                                    valign="middle"
                                                    style="
                                                        width: 84px;
                                                        height: 84px;
                                                        background-color: rgba(255,255,255,0.16);
                                                        border-radius: 50%%;
                                                        color: #ffffff;
                                                        font-size: 40px;
                                                        font-weight: 800;
                                                        line-height: 84px;
                                                    "
                                                >
                                                    %s
                                                </td>
                                            </tr>
                                        </table>
            
                                        <h1
                                            style="
                                                margin: 0;
                                                color: #ffffff;
                                                font-size: 46px;
                                                line-height: 1.08;
                                                font-weight: 800;
                                                letter-spacing: -1.2px;
                                            "
                                        >
                                            %s
                                        </h1>
            
                                        <p
                                            style="
                                                margin: 24px 0 0;
                                                color: #ffffff;
                                                font-size: 13px;
                                                font-weight: 700;
                                                letter-spacing: 1px;
                                                text-transform: uppercase;
                                            "
                                        >
                                            %s
                                        </p>
                                    </td>
                                </tr>
            
                                <tr>
                                    <td
                                        align="center"
                                        style="
                                            padding: 46px 42px 40px;
                                            background-color: #ffffff;
                                        "
                                    >
                                        <h2
                                            style="
                                                margin: 0 0 18px;
                                                color: #090909;
                                                font-size: 30px;
                                                line-height: 1.2;
                                                font-weight: 800;
                                            "
                                        >
                                            Olá, %s!
                                        </h2>
            
                                        <p
                                            style="
                                                margin: 0 auto;
                                                max-width: 500px;
                                                color: #181818;
                                                font-size: 17px;
                                                line-height: 1.65;
                                                font-weight: 500;
                                            "
                                        >
                                            %s
                                        </p>
                                    </td>
                                </tr>
            
                                <tr>
                                    <td
                                        style="
                                            padding: 40px 34px;
                                            background-color: #f5f7fc;
                                        "
                                    >
                                        <h2
                                            style="
                                                margin: 0 0 28px;
                                                color: #0c0c0d;
                                                font-size: 31px;
                                                line-height: 1.2;
                                                font-weight: 800;
                                                text-align: center;
                                            "
                                        >
                                            Resumo
                                        </h2>
            
                                        %s
                                    </td>
                                </tr>
            
                                <tr>
                                    <td
                                        align="center"
                                        style="
                                            padding: 46px 42px 48px;
                                            background-color: #ffffff;
                                        "
                                    >
                                        <table
                                            role="presentation"
                                            cellspacing="0"
                                            cellpadding="0"
                                            border="0"
                                            align="center"
                                        >
                                            <tr>
                                                <td
                                                    align="center"
                                                    style="
                                                        background-color: %s;
                                                        border-radius: 10px;
                                                    "
                                                >
                                                    <a
                                                        href="%s"
                                                        target="_blank"
                                                        style="
                                                            display: inline-block;
                                                            min-width: 190px;
                                                            padding: 15px 24px;
                                                            color: #ffffff;
                                                            font-size: 14px;
                                                            font-weight: 700;
                                                            text-align: center;
                                                            text-decoration: none;
                                                        "
                                                    >
                                                        %s
                                                    </a>
                                                </td>
                                            </tr>
                                        </table>
            
                                        <p
                                            style="
                                                margin: 28px auto 0;
                                                max-width: 500px;
                                                color: #555555;
                                                font-size: 12px;
                                                line-height: 1.65;
                                            "
                                        >
                                            %s
                                        </p>
                                    </td>
                                </tr>
            
                                <tr>
                                    <td
                                        align="center"
                                        style="
                                            padding: 34px 28px;
                                            background-color: #f1f4fa;
                                        "
                                    >
                                        <p
                                            style="
                                                margin: 0 0 8px;
                                                color: #737987;
                                                font-size: 10px;
                                                line-height: 1.5;
                                            "
                                        >
                                            Music Tour — Plataforma de eventos e ingressos
                                        </p>
            
                                        <p
                                            style="
                                                margin: 0;
                                                color: #9398a3;
                                                font-size: 9px;
                                                line-height: 1.5;
                                            "
                                        >
                                            Esta mensagem foi enviada automaticamente.
                                            Não é necessário respondê-la.
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
            corEstado,
            escaparHtml(icone),
            escaparHtml(titulo),
            escaparHtml(etiqueta),
            escaparHtml(nome),
            formatarTexto(mensagem),
            montarDetalhes(
                detalhes,
                corEstado
            ),
            COR_PRINCIPAL,
            escaparAtributo(linkBotao),
            escaparHtml(textoBotao),
            formatarTexto(observacao)
        );
    }

    private String montarDetalhes(List<EmailDetail> detalhes, String corEstado) {
        if (detalhes == null || detalhes.isEmpty()) {
            return "";
        }

        StringBuilder linhas = new StringBuilder();

        for (int i = 0; i < detalhes.size(); i++) {
            EmailDetail detalhe = detalhes.get(i);
            boolean ultimaLinha = i == detalhes.size() - 1;

            String corValor = detalhe.destaque() ? corEstado : "#111111";

            String borda = ultimaLinha ? "" : "border-bottom: 1px solid #e3e7ef;";

            linhas.append("""
                              <tr>
                                  <td style="
                                      padding: 18px 20px;
                                      %s
                                  ">
                                      <p
                                          style="
                                              margin: 0 0 6px;
                                              color: #777c88;
                                              font-size: 10px;
                                              font-weight: 700;
                                              letter-spacing: 1px;
                                              text-transform: uppercase;
                                          "
                                      >
                                          %s
                                      </p>
                              
                                      <p
                                          style="
                                              margin: 0;
                                              color: %s;
                                              font-size: 15px;
                                              font-weight: 800;
                                              line-height: 1.5;
                                              word-break: break-word;
                                          "
                                      >
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
                    background-color: #ffffff;
                    border-radius: 13px;
                    box-shadow: 0 3px 13px rgba(30, 45, 90, 0.06);
                    overflow: hidden;
                "
            >
                %s
            </table>
            """.formatted(linhas);
    }

    private String formatarTexto(String valor) {
        return escaparHtml(valor).replace(
            "\r\n",
            "<br>"
        ).replace(
            "\n",
            "<br>"
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

    private String codificarParametro(String valor) {
        if (valor == null) {
            return "";
        }

        return URLEncoder.encode(
            valor,
            StandardCharsets.UTF_8
        );
    }

    private String removerBarraFinal(String valor) {
        if (valor == null || valor.isBlank()) {
            return "";
        }

        return valor.endsWith("/") ? valor.substring(
            0,
            valor.length() - 1
        ) : valor;
    }
}