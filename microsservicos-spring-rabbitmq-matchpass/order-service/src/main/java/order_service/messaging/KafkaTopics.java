package order_service.messaging;

public final class KafkaTopics {
    public static final String ORDER_RESERVATION_REQUESTED =
        "matchpass.order.reservation-requested.v1";

    public static final String INVENTORY_RESERVATION_RESULT =
        "matchpass.inventory.reservation-result.v1";

    private KafkaTopics() {
    }
}