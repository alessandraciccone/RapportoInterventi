package acicone.RapportoInterventi.payloads;

import java.util.UUID;

public record CompanyResponseDTO(
        UUID id,
        String ragioneSociale,
        String email
) {
}
