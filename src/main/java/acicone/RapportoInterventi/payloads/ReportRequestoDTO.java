package acicone.RapportoInterventi.payloads;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReportRequestoDTO(
        @NotNull UUID userId,
        @NotNull UUID  companyId,
        @NotNull Integer anno,
        @NotNull LocalDate dataIntervento,
        @NotNull LocalTime oraInizio,
        @NotNull LocalTime oraFine,
        String note,
        String fileBase64
) {
}
