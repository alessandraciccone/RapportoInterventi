package acicone.RapportoInterventi.payloads;

import java.util.UUID;

public record RegistrationResponseDTO(
        UUID userId,
        String email,
        String message
) {
}
