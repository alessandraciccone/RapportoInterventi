package acicone.RapportoInterventi.controller;

import acicone.RapportoInterventi.payloads.PaymentsRequestDTO;
import acicone.RapportoInterventi.payloads.PaymentsResponseDTO;
import acicone.RapportoInterventi.payloads.PaymentsUpdateDTO;
import acicone.RapportoInterventi.service.PaymentsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentsService paymentsService;

    public PaymentController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    // ======================
    // GET ALL PAYMENTS
    // ======================
    @GetMapping
    public ResponseEntity<List<PaymentsResponseDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentsService.getAllPayments());
    }

    // ======================
    // GET PAYMENT BY ID
    // ======================
    @GetMapping("/{id}")
    public ResponseEntity<PaymentsResponseDTO> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentsService.getPaymentById(id));
    }

    // ======================
    // GET PAYMENTS BY SUBSCRIPTION
    // ======================
    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<PaymentsResponseDTO>> getPaymentsBySubscription(@PathVariable UUID subscriptionId) {
        return ResponseEntity.ok(paymentsService.getPaymentsBySubscription(subscriptionId));
    }

    // ======================
    // GET PAYMENT BY PAYPAL TRANSACTION
    // ======================
    @GetMapping("/paypal/{transactionId}")
    public ResponseEntity<PaymentsResponseDTO> getPaymentByPaypalTransaction(@PathVariable String transactionId) {
        return ResponseEntity.ok(paymentsService.getPaymentByPaypalTransaction(transactionId));
    }

    // ======================
    // CREATE PAYMENT
    // ======================
    @PostMapping
    public ResponseEntity<PaymentsResponseDTO> createPayment(@Valid @RequestBody PaymentsRequestDTO dto) {
        PaymentsResponseDTO response = paymentsService.createPayment(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ======================
    // UPDATE PAYMENT
    // ======================
    @PutMapping("/{id}")
    public ResponseEntity<PaymentsResponseDTO> updatePayment(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentsUpdateDTO dto) {
        return ResponseEntity.ok(paymentsService.updatePayment(id, dto));
    }

    // ======================
    // DELETE PAYMENT
    // ======================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentsService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
