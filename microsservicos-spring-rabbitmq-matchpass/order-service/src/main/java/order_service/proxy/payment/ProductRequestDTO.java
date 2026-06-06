package order_service.proxy.payment;

public record ProductRequestDTO(
    Long amount,
    Long quantity,
    String name,
    String currency
) {
}
