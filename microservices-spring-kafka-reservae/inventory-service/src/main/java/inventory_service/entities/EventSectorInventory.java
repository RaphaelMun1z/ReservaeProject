package inventory_service.entities;

import inventory_service.exceptions.models.BusinessException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("event_sector_inventory")
public class EventSectorInventory {
    @Id
    private String id;

    @NotBlank(message = "O ID do evento é obrigatório.")
    private String eventId;

    @NotBlank(message = "O ID do setor é obrigatório.")
    private String sectorId;

    @Positive(message = "A capacidade do setor deve ser maior que zero.")
    private int capacity;

    @PositiveOrZero(message = "A quantidade reservada não pode ser negativa.")
    private int reservedQuantity;

    @PositiveOrZero(message = "A quantidade vendida não pode ser negativa.")
    private int soldQuantity;

    protected EventSectorInventory() {
    }

    public EventSectorInventory(
        String eventId,
        String sectorId,
        int capacity
    ) {
        this.id = buildId(eventId, sectorId);
        this.eventId = eventId;
        this.sectorId = sectorId;
        this.capacity = capacity;
        this.reservedQuantity = 0;
        this.soldQuantity = 0;
    }

    public static String buildId(String eventId, String sectorId) {
        return eventId + ":" + sectorId;
    }

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getSectorId() {
        return sectorId;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public int getAvailableTicketsAmount() {
        return capacity - reservedQuantity - soldQuantity;
    }

    public void reserve(int quantity) {
        validatePositiveQuantity(quantity);

        if (getAvailableTicketsAmount() < quantity) {
            throw new BusinessException("Não há ingressos suficientes disponíveis para este setor.");
        }

        this.reservedQuantity += quantity;
    }

    public void confirmSale(int quantity) {
        validatePositiveQuantity(quantity);

        if (reservedQuantity < quantity) {
            throw new BusinessException("Não há ingressos reservados suficientes para confirmar a venda.");
        }

        this.reservedQuantity -= quantity;
        this.soldQuantity += quantity;
    }

    public void releaseReservation(int quantity) {
        validatePositiveQuantity(quantity);

        if (reservedQuantity < quantity) {
            throw new BusinessException("Não há ingressos reservados suficientes para liberar.");
        }

        this.reservedQuantity -= quantity;
    }

    private void validatePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("A quantidade de ingressos deve ser maior que zero.");
        }
    }
}