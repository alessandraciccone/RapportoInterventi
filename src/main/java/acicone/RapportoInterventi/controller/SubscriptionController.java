package acicone.RapportoInterventi.controller;

import acicone.RapportoInterventi.entities.SubscriptionStatus;
import acicone.RapportoInterventi.payloads.SubscriptionRequestDTO;
import acicone.RapportoInterventi.payloads.SubscriptionResponseDTO;
import acicone.RapportoInterventi.payloads.SubscriptionUpdateDTO;
import acicone.RapportoInterventi.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // ======================
    // GET ALL SUBSCRIPTIONS
    // ======================
    @GetMapping
    public ResponseEntity<List<SubscriptionResponseDTO>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    // ======================
    // GET SUBSCRIPTION BY ID
    // ======================
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDTO> getSubscriptionById(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }

    // ======================
    // GET SUBSCRIPTION BY USER
    // ======================
    @GetMapping("/user/{userId}")
    public ResponseEntity<SubscriptionResponseDTO> getSubscriptionByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionByUserId(userId));
    }

    // ======================
    // GET SUBSCRIPTIONS BY STATUS
    // ======================
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SubscriptionResponseDTO>> getSubscriptionsByStatus(@PathVariable SubscriptionStatus status) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByStatus(status));
    }

    // ======================
    // CREATE SUBSCRIPTION (TRIAL)
    // ======================
    @PostMapping("/create/{userId}")
    public ResponseEntity<SubscriptionResponseDTO> createSubscription(@PathVariable UUID userId) {
        SubscriptionResponseDTO response = subscriptionService.createSubscription(userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ======================
    // ACTIVATE SUBSCRIPTION
    // ======================
    @PutMapping("/{id}/activate")
    public ResponseEntity<SubscriptionResponseDTO> activateSubscription(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.activateSubscription(id));
    }

    // ======================
    // RENEW SUBSCRIPTION
    // ======================
    @PutMapping("/{id}/renew")
    public ResponseEntity<SubscriptionResponseDTO> renewSubscription(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.renewSubscription(id));
    }

    // ======================
    // CANCEL SUBSCRIPTION
    // ======================
    @PutMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionResponseDTO> cancelSubscription(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(id));
    }

    // ======================
    // UPDATE SUBSCRIPTION (dates)
    // ======================
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDTO> updateSubscription(
            @PathVariable UUID id,
            @Valid @RequestBody SubscriptionUpdateDTO dto) {
        return ResponseEntity.ok(subscriptionService.updateSubscription(id, dto));
    }

    // ======================
    // DELETE SUBSCRIPTION
    // ======================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable UUID id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
