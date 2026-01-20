package acicone.RapportoInterventi.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @Size(max=50) String nome,
        @Size(max=50) String cognome,
        @Email String email,
        Boolean isAdmin
) {
}
