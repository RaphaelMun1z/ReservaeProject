package order_service.entities;

import jakarta.persistence.*;
import order_service.entities.enums.OrderStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "payment_url", length = 1000)
    private String paymentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatusEnum status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
    }

    public Order(
        String eventId,
        String userId,
        BigDecimal totalAmount,
        OrderStatusEnum status
    ) {
        this.eventId = eventId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
    }

    public void addItems(List<OrderItem> items) {
        items.forEach(this::addItem);
    }

    public void updateStatus(OrderStatusEnum status) {
        this.status = status;
    }

    public void attachPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public void attachReservationIds(List<String> reservationIds) {
        if (reservationIds == null || reservationIds.size() != items.size()) {
            throw new IllegalArgumentException(
                "A quantidade de reservas não corresponde à quantidade de itens do pedido."
            );
        }

        for (int index = 0; index < items.size(); index++) {
            items.get(index).attachReservation(reservationIds.get(index));
        }
    }

    public int getTotalTicketsQuantity() {
        return items.stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return id != null && id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}