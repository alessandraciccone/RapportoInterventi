package acicone.RapportoInterventi.payloads;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String nome,
        String cognome,
        String email,
         String role
) {
}
