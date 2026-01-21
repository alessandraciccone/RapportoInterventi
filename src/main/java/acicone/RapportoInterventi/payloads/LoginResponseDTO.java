package acicone.RapportoInterventi.payloads;

import java.util.UUID;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String role,
        UUID userId
) {

}
