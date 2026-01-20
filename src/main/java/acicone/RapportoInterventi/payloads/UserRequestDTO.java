package acicone.RapportoInterventi.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank @Size(max=50) String nome,
        @NotBlank @Size(max=50) String cognome,
        @NotBlank @Email String email,
boolean isAdmin
) {
}
