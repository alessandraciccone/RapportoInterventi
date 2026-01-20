package acicone.RapportoInterventi.payloads;

import jakarta.validation.constraints.Email;

public record CompanyUpdateDTO(
        String ragioneSociale,
      @Email String email

) {
}
