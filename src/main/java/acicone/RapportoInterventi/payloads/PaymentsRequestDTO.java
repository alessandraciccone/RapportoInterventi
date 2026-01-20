package acicone.RapportoInterventi.payloads;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentsRequestDTO(
        @NotNull BigDecimal amount,
        @NotNull String currency,
        @NotNull String method,
        @NotNull String paypalTransaction,
        @NotNull UUID subscriptionId
        ) {
}
