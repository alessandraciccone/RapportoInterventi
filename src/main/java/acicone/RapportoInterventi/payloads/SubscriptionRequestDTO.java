package acicone.RapportoInterventi.payloads;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionRequestDTO(
        @NotNull UUID userId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
        ) {
}
