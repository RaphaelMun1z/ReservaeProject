package order_service.entities;

import jakarta.persistence.*;
import order_service.entities.enums.TicketType;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "sector_id", nullable = false)
    private String sectorId;

    @Column(name = "reservation_id")
    private String reservationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type", nullable = false)
    private TicketType ticketType;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "applied_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal appliedPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    protected OrderItem() {
    }

    public OrderItem(
        String sectorId,
        TicketType ticketType,
        int quantity,
        BigDecimal appliedPrice
    ) {
        this.sectorId = sectorId;
        this.ticketType = ticketType;
        this.quantity = quantity;
        this.appliedPrice = appliedPrice;
    }

    public String getId() {
        return id;
    }

    public String getSectorId() {
        return sectorId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAppliedPrice() {
        return appliedPrice;
    }

    public BigDecimal getSubtotal() {
        return appliedPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Order getOrder() {
        return order;
    }

    void setOrder(Order order) {
        this.order = order;
    }

    public void attachReservation(String reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem orderItem)) return false;
        return id != null && id.equals(orderItem.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}