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

    private String sectorId;
    private String ticketId;

    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    private BigDecimal appliedPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public OrderItem() {
    }

    public OrderItem(String sectorId, String ticketId, TicketType ticketType, BigDecimal appliedPrice) {
        this.sectorId = sectorId;
        this.ticketId = ticketId;
        this.ticketType = ticketType;
        this.appliedPrice = appliedPrice;
    }

    public String getId() {
        return id;
    }

    public String getSectorId() {
        return sectorId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public BigDecimal getAppliedPrice() {
        return appliedPrice;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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
