package acicone.RapportoInterventi.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanyRequestDTO(
        @NotBlank String ragioneSociale,
        @NotBlank @Email String email
) {
}
