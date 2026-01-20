package acicone.RapportoInterventi.payloads;

import java.math.BigDecimal;

public record PaymentsUpdateDTO(
        BigDecimal amount,
        String currency,
        String method,
        String paypalTransaction
) {
}
