package event_catalog_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_event_sector_pricings", uniqueConstraints = {
    @UniqueConstraint(
        columnNames = {"event_id", "sector_id"}
    )
})
public class EventSectorPricing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @NotNull
    @Column(name = "sector_id", nullable = false)
    private String sectorId;

    @NotNull
    @PositiveOrZero(message = "O preço base não pode ser negativo")
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @NotNull
    @PositiveOrZero(message = "O preço da meia-entrada não pode ser negativo")
    @Column(name = "half_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal halfPrice;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    protected EventSectorPricing() {
    }

    public EventSectorPricing(Event event, String sectorId, BigDecimal basePrice, BigDecimal halfPrice) {
        this.event = event;
        this.sectorId = sectorId;
        this.basePrice = basePrice;
        this.halfPrice = halfPrice;
        this.isAvailable = true;
    }

    public String getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    void setEvent(Event event) {
        this.event = event;
    }

    public String getSectorId() {
        return sectorId;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public BigDecimal getHalfPrice() {
        return halfPrice;
    }

    // --- MÉTODOS DE COMPORTAMENTO ---

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void suspendSales() {
        this.isAvailable = false;
    }

    public void resumeSales() {
        this.isAvailable = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventSectorPricing eventSectorPricing)) return false;
        return id != null && id.equals(eventSectorPricing.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}