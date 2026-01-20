package acicone.RapportoInterventi.payloads;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReportUpdateDTO(
        Integer anno,
        LocalDate dataIntervento,
        LocalTime oraInizio,
        LocalTime oraFine,
        String note,
        String fileBase64
) {
}
