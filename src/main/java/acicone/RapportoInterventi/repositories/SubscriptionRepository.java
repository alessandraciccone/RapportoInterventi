package acicone.RapportoInterventi.repositories;

import acicone.RapportoInterventi.entities.Subscription;
import acicone.RapportoInterventi.entities.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByUserId(UUID userId);
    List<Subscription> findByStatus(SubscriptionStatus status);
}

