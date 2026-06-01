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

    private String eventId;
    private String sectorId;
    private String seatTag;

    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    private BigDecimal appliedPrice;

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
