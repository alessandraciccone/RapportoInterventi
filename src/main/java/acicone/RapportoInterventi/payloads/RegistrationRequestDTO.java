package acicone.RapportoInterventi.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequestDTO(
        @NotBlank @Size (min=2, max=50)String nome,
        @NotBlank @Size (min=2, max=50)String cognome,
        @NotBlank @Email String email,
        @NotBlank @Size(min=6)String password
) {}