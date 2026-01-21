package acicone.RapportoInterventi.payloads;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReportResponseDTO(
        UUID id,
        UUID userId,
        CompanyResponseDTO companyResponseDTO,
        Integer anno,
        LocalDate dataIntervento,
        LocalTime oraInizio,
        LocalTime oraFine,
        String note,
        String fileUrl
) {

}
