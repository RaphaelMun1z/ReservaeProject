package inventory_service.dtos.res;

public record EventSectorInventoryResponseDTO(
    String id,
    String eventId,
    String sectorId,
    int capacity,
    int reservedQuantity,
    int soldQuantity,
    int availableQuantity,
    String environment
) {
}