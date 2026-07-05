package ticket_service.services;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class QrCodeService {

    public String generateQrCodeHash(
        String orderId,
        String orderItemId,
        String reservationId,
        String userId,
        String eventId,
        String sectorId
    ) {
        String rawData = orderId
            + "|"
            + orderItemId
            + "|"
            + reservationId
            + "|"
            + userId
            + "|"
            + eventId
            + "|"
            + sectorId
            + "|"
            + UUID.randomUUID();

        return UUID.nameUUIDFromBytes(
            rawData.getBytes(StandardCharsets.UTF_8)
        ).toString();
    }
}