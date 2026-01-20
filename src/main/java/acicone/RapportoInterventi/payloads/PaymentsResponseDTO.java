package acicone.RapportoInterventi.payloads;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentsResponseDTO(
        UUID id,
        BigDecimal amount,
        String currency,
        String method,
        String paypalTransaction,
        UUID subscriptionId
) {
}
