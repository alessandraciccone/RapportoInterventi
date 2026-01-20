package acicone.RapportoInterventi.payloads;

import java.time.LocalDate;

public record SubscriptionUpdateDTO(
        LocalDate startDate,
        LocalDate endDate
) {
}
