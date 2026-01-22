package acicone.RapportoInterventi.service;

import acicone.RapportoInterventi.entities.Subscription;
import acicone.RapportoInterventi.entities.SubscriptionStatus;
import acicone.RapportoInterventi.entities.User;
import acicone.RapportoInterventi.exceptions.BadRequestExceptions;
import acicone.RapportoInterventi.exceptions.ConflictException;
import acicone.RapportoInterventi.exceptions.NotFoundException;
import acicone.RapportoInterventi.payloads.SubscriptionRequestDTO;
import acicone.RapportoInterventi.payloads.SubscriptionResponseDTO;
import acicone.RapportoInterventi.payloads.SubscriptionUpdateDTO;
import acicone.RapportoInterventi.repositories.SubscriptionRepository;
import acicone.RapportoInterventi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Value("${app.subscription.trial-days:7}")
    private int trialDays;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public SubscriptionResponseDTO createSubscription(UUID userId) {
        // Verifica che l'utente esista
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        // Verifica che l'utente non abbia già una subscription
        if (subscriptionRepository.findByUserId(userId).isPresent()) {
            throw new ConflictException("L'utente ha già una subscription attiva");
        }

        // Crea subscription con trial
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setStatus(SubscriptionStatus.TRIAL);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusDays(trialDays));
        subscription.setTrailUsed(true);

        Subscription saved = subscriptionRepository.save(subscription);
        return mapToResponseDTO(saved);
    }


    @Transactional
    public SubscriptionResponseDTO activateSubscription(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription non trovata"));

        // Verifica che sia in trial o scaduta
        if (subscription.getStatus() != SubscriptionStatus.TRIAL &&
                subscription.getStatus() != SubscriptionStatus.EXPIRED) {
            throw new BadRequestExceptions("La subscription è già attiva o cancellata");
        }

        // Attiva per 30 giorni
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));

        Subscription updated = subscriptionRepository.save(subscription);
        return mapToResponseDTO(updated);
    }


    @Transactional
    public SubscriptionResponseDTO renewSubscription(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription non trovata"));

        if (subscription.getStatus() == SubscriptionStatus.CANCELED) {
            throw new BadRequestExceptions("Impossibile rinnovare una subscription cancellata");
        }

        // Rinnova per altri 30 giorni dalla data di scadenza attuale
        LocalDate newEndDate = subscription.getEndDate() != null
                ? subscription.getEndDate().plusMonths(1)
                : LocalDate.now().plusMonths(1);

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setEndDate(newEndDate);

        Subscription updated = subscriptionRepository.save(subscription);
        return mapToResponseDTO(updated);
    }

    // ======================
    // CANCEL SUBSCRIPTION
    // ======================
    @Transactional
    public SubscriptionResponseDTO cancelSubscription(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription non trovata"));

        subscription.setStatus(SubscriptionStatus.CANCELED);

        Subscription updated = subscriptionRepository.save(subscription);
        return mapToResponseDTO(updated);
    }

    // ======================
    // CHECK AND UPDATE EXPIRED SUBSCRIPTIONS
    // ======================
    @Transactional
    public void checkExpiredSubscriptions() {
        List<Subscription> activeSubscriptions = subscriptionRepository.findAll().stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE ||
                        s.getStatus() == SubscriptionStatus.TRIAL)
                .filter(s -> s.getEndDate() != null && s.getEndDate().isBefore(LocalDate.now()))
                .toList();

        activeSubscriptions.forEach(subscription -> {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);
        });
    }

    // ======================
    // GET SUBSCRIPTION BY ID
    // ======================
    public SubscriptionResponseDTO getSubscriptionById(UUID subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription non trovata"));
        return mapToResponseDTO(subscription);
    }


    public SubscriptionResponseDTO getSubscriptionByUserId(UUID userId) {
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Subscription non trovata per questo utente"));
        return mapToResponseDTO(subscription);
    }


    public List<SubscriptionResponseDTO> getAllSubscriptions() {
        return subscriptionRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    public List<SubscriptionResponseDTO> getSubscriptionsByStatus(SubscriptionStatus status) {
        List<Subscription> subscriptions = subscriptionRepository.findByStatus(status.name());
        return subscriptions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    public boolean userHasActiveSubscription(UUID userId) {
        return subscriptionRepository.findByUserId(userId)
                .map(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE ||
                        sub.getStatus() == SubscriptionStatus.TRIAL)
                .orElse(false);
    }

    public boolean userUsedTrial(UUID userId) {
        return subscriptionRepository.findByUserId(userId)
                .map(Subscription::isTrailUsed)
                .orElse(false);
    }


    public Subscription getSubscriptionEntity(UUID subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription non trovata"));
    }


    @Transactional
    public SubscriptionResponseDTO updateSubscription(UUID subscriptionId, SubscriptionUpdateDTO dto) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription non trovata"));

        if (dto.startDate() != null) {
            subscription.setStartDate(dto.startDate());
        }

        if (dto.endDate() != null) {
            subscription.setEndDate(dto.endDate());
        }

        Subscription updated = subscriptionRepository.save(subscription);
        return mapToResponseDTO(updated);
    }


    @Transactional
    public void deleteSubscription(UUID subscriptionId) {
        if (!subscriptionRepository.existsById(subscriptionId)) {
            throw new NotFoundException("Subscription non trovata");
        }
        subscriptionRepository.deleteById(subscriptionId);
    }


    private SubscriptionResponseDTO mapToResponseDTO(Subscription subscription) {
        return new SubscriptionResponseDTO(
                subscription.getId(),
                subscription.getUser().getId(),
                subscription.getStartDate(),
                subscription.getEndDate()
        );
    }
}