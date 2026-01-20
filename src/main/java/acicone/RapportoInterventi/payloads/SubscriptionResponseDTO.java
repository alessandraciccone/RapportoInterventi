package acicone.RapportoInterventi.payloads;

import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionResponseDTO(
        UUID id,
        UUID userId,
        LocalDate startDate,
        LocalDate endDate
) {
}
