package acicone.RapportoInterventi.service;

import acicone.RapportoInterventi.entities.PaymentMethod;
import acicone.RapportoInterventi.entities.Payments;
import acicone.RapportoInterventi.entities.Subscription;
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

    public PaymentsService(PaymentsRepository paymentsRepository,
                           SubscriptionRepository subscriptionRepository) {
        this.paymentsRepository = paymentsRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    // ======================
    // CREATE PAYMENT
    // ======================
    @Transactional
    public PaymentsResponseDTO createPayment(PaymentsRequestDTO dto) {
        // Verifica che la subscription esista
        Subscription subscription = subscriptionRepository.findById(dto.subscriptionId())
                .orElseThrow(() -> new NotFoundException("Subscription non trovata"));

        // Verifica che la transazione PayPal non sia già stata registrata (evita duplicati)
        if (paymentsRepository.findByPaypalTransaction(dto.paypalTransaction()).isPresent()) {
            throw new ConflictException("Transazione PayPal già registrata");
        }

        // Valida il metodo di pagamento
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(dto.method().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestExceptions("Metodo di pagamento non valido: " + dto.method());
        }

        // Crea il pagamento
        Payments payment = new Payments();
        payment.setAmount(dto.amount());
        payment.setCurrency(dto.currency());
        payment.setMethod(paymentMethod);
        payment.setPaypalTransaction(dto.paypalTransaction());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setSubscription(subscription);

        Payments saved = paymentsRepository.save(payment);
        return mapToResponseDTO(saved);
    }

    // ======================
    // GET PAYMENT BY ID
    // ======================
    public PaymentsResponseDTO getPaymentById(UUID paymentId) {
        Payments payment = paymentsRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Pagamento non trovato"));
        return mapToResponseDTO(payment);
    }

    // ======================
    // GET ALL PAYMENTS
    // ======================
    public List<PaymentsResponseDTO> getAllPayments() {
        return paymentsRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ======================
    // GET PAYMENTS BY SUBSCRIPTION
    // ======================
    public List<PaymentsResponseDTO> getPaymentsBySubscription(UUID subscriptionId) {
        // Verifica che la subscription esista
        if (!subscriptionRepository.existsById(subscriptionId)) {
            throw new NotFoundException("Subscription non trovata");
        }

        List<Payments> payments = paymentsRepository.findBySubscriptionId(subscriptionId);
        return payments.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ======================
    // GET PAYMENT BY PAYPAL TRANSACTION
    // ======================
    public PaymentsResponseDTO getPaymentByPaypalTransaction(String transactionId) {
        Payments payment = paymentsRepository.findByPaypalTransaction(transactionId)
                .orElseThrow(() -> new NotFoundException("Pagamento non trovato per questa transazione"));
        return mapToResponseDTO(payment);
    }

    // ======================
    // UPDATE PAYMENT (limitato, normalmente i pagamenti non si modificano)
    // ======================
    @Transactional
    public PaymentsResponseDTO updatePayment(UUID paymentId, PaymentsUpdateDTO dto) {
        Payments payment = paymentsRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Pagamento non trovato"));

        // Aggiorna solo i campi non nulli (uso limitato, i pagamenti di solito sono immutabili)
        if (dto.amount() != null) {
            payment.setAmount(dto.amount());
        }

        if (dto.currency() != null && !dto.currency().isBlank()) {
            payment.setCurrency(dto.currency());
        }

        if (dto.method() != null && !dto.method().isBlank()) {
            try {
                PaymentMethod paymentMethod = PaymentMethod.valueOf(dto.method().toUpperCase());
                payment.setMethod(paymentMethod);
            } catch (IllegalArgumentException e) {
                throw new BadRequestExceptions("Metodo di pagamento non valido: " + dto.method());
            }
        }

        if (dto.paypalTransaction() != null && !dto.paypalTransaction().isBlank()) {
            // Verifica che la nuova transazione non esista già
            paymentsRepository.findByPaypalTransaction(dto.paypalTransaction())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(paymentId)) {
                            throw new ConflictException("Transazione PayPal già registrata");
                        }
                    });
            payment.setPaypalTransaction(dto.paypalTransaction());
        }

        Payments updated = paymentsRepository.save(payment);
        return mapToResponseDTO(updated);
    }

    // ======================
    // DELETE PAYMENT (uso amministrativo)
    // ======================
    @Transactional
    public void deletePayment(UUID paymentId) {
        if (!paymentsRepository.existsById(paymentId)) {
            throw new NotFoundException("Pagamento non trovato");
        }
        paymentsRepository.deleteById(paymentId);
    }

    // ======================
    // VERIFY PAYMENT EXISTS
    // ======================
    public boolean paymentExists(UUID paymentId) {
        return paymentsRepository.existsById(paymentId);
    }

    // ======================
    // VERIFY PAYPAL TRANSACTION EXISTS
    // ======================
    public boolean paypalTransactionExists(String transactionId) {
        return paymentsRepository.findByPaypalTransaction(transactionId).isPresent();
    }

    // ======================
    // HELPER METHOD
    // ======================
    private PaymentsResponseDTO mapToResponseDTO(Payments payment) {
        return new PaymentsResponseDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getMethod().name(),
                payment.getPaypalTransaction(),
                payment.getSubscription().getId()
        );
    }
}