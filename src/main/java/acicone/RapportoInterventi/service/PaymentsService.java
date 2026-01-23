package acicone.RapportoInterventi.service;

import acicone.RapportoInterventi.entities.PaymentMethod;
import acicone.RapportoInterventi.entities.Payments;
import acicone.RapportoInterventi.entities.Subscription;
import acicone.RapportoInterventi.entities.SubscriptionStatus;
import acicone.RapportoInterventi.exceptions.BadRequestExceptions;
import acicone.RapportoInterventi.exceptions.ConflictException;
import acicone.RapportoInterventi.exceptions.NotFoundException;
import acicone.RapportoInterventi.payloads.PaymentsRequestDTO;
import acicone.RapportoInterventi.payloads.PaymentsResponseDTO;
import acicone.RapportoInterventi.payloads.PaymentsUpdateDTO;
import acicone.RapportoInterventi.repositories.PaymentsRepository;
import acicone.RapportoInterventi.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;

    public PaymentsService(
            PaymentsRepository paymentsRepository,
            SubscriptionRepository subscriptionRepository,
            SubscriptionService subscriptionService
    ) {
        this.paymentsRepository = paymentsRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionService = subscriptionService;
    }
    public PaymentsResponseDTO getPaymentByPaypalTransaction(String transactionId) {
        Payments payment = paymentsRepository.findByPaypalTransaction(transactionId)
                .orElseThrow(() -> new NotFoundException("Pagamento non trovato per questa transazione"));
        return mapToResponseDTO(payment);
    }

    // ======================
    // CREATE PAYMENT
    // ======================
    @Transactional
    public PaymentsResponseDTO createPayment(PaymentsRequestDTO dto) {

        Subscription subscription = subscriptionRepository.findById(dto.subscriptionId())
                .orElseThrow(() -> new NotFoundException("Subscription non trovata"));

        if (subscription.getStatus() == SubscriptionStatus.CANCELED) {
            throw new BadRequestExceptions("Subscription cancellata");
        }

        if (paymentsRepository.findByPaypalTransaction(dto.paypalTransaction()).isPresent()) {
            throw new ConflictException("Transazione PayPal già registrata");
        }

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(dto.method().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestExceptions("Metodo di pagamento non valido");
        }

        Payments payment = new Payments();
        payment.setAmount(dto.amount());
        payment.setCurrency(dto.currency());
        payment.setMethod(method);
        payment.setPaypalTransaction(dto.paypalTransaction());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setSubscription(subscription);

        Payments saved = paymentsRepository.save(payment);

        // 🔥 EVENTO DI DOMINIO
        switch (subscription.getStatus()) {
            case TRIAL, EXPIRED ->
                    subscriptionService.activateSubscription(subscription.getId());
            case ACTIVE ->
                    subscriptionService.renewSubscription(subscription.getId());
        }

        return mapToResponseDTO(saved);
    }

    // ======================
    // GET / LIST
    // ======================
    public PaymentsResponseDTO getPaymentById(UUID id) {
        return mapToResponseDTO(
                paymentsRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Pagamento non trovato"))
        );
    }

    public List<PaymentsResponseDTO> getAllPayments() {
        return paymentsRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentsResponseDTO> getPaymentsBySubscription(UUID subscriptionId) {
        return paymentsRepository.findBySubscriptionId(subscriptionId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ======================
    // UPDATE (ADMIN)
    // ======================
    @Transactional
    public PaymentsResponseDTO updatePayment(UUID id, PaymentsUpdateDTO dto) {

        Payments payment = paymentsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pagamento non trovato"));

        if (dto.amount() != null) payment.setAmount(dto.amount());
        if (dto.currency() != null) payment.setCurrency(dto.currency());

        if (dto.method() != null) {
            payment.setMethod(PaymentMethod.valueOf(dto.method().toUpperCase()));
        }

        if (dto.paypalTransaction() != null) {
            payment.setPaypalTransaction(dto.paypalTransaction());
        }

        return mapToResponseDTO(paymentsRepository.save(payment));
    }

    // ======================
    // DELETE (ADMIN)
    // ======================
    @Transactional
    public void deletePayment(UUID id) {
        if (!paymentsRepository.existsById(id)) {
            throw new NotFoundException("Pagamento non trovato");
        }
        paymentsRepository.deleteById(id);
    }

    // ======================
    // MAPPER
    // ======================
    private PaymentsResponseDTO mapToResponseDTO(Payments p) {
        return new PaymentsResponseDTO(
                p.getId(),
                p.getAmount(),
                p.getCurrency(),
                p.getMethod().name(),
                p.getPaypalTransaction(),
                p.getSubscription().getId()
        );
    }
}
