package acicone.RapportoInterventi.repositories;

import acicone.RapportoInterventi.entities.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, UUID> {
    List<Payments> findBySubscriptionId(UUID subscriptionId);
    Optional<Payments> findByPaypalTransaction(String paypalTransaction);
}
