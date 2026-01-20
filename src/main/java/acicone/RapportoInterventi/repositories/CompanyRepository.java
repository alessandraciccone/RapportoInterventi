package acicone.RapportoInterventi.repositories;

import acicone.RapportoInterventi.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    List<Company> findByRagioneSocialeContainingIgnoreCase(String ragioneSociale);
    Optional<Company> findByRagioneSocialeIgnoreCase(String ragioneSociale);
}
