package notification_service.templates;

public record EmailItem(
    String imagemUrl,
    String nome,
    String descricao,
    int quantidade,
    String precoUnitario,
    String precoTotal
) {
}