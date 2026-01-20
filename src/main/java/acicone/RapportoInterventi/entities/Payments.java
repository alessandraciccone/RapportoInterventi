package acicone.RapportoInterventi.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String currency;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;
    @Column(nullable = false)
    private String paypalTransaction;
    @Column(nullable = false)
    private LocalDateTime paymentDate;
    @ManyToOne
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;
    public Payments() {
    }

    public Payments(UUID id, BigDecimal amount, String currency, PaymentMethod method, String paypalTransaction, LocalDateTime paymentDate, Subscription subscription) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.method = method;
        this.paypalTransaction = paypalTransaction;
        this.paymentDate = paymentDate;
        this.subscription = subscription;
    }

    public UUID getId() {
        return id;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public String getPaypalTransaction() {
        return paypalTransaction;
    }

    public void setPaypalTransaction(String paypalTransaction) {
        this.paypalTransaction = paypalTransaction;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "Payments{" +
                "id=" + id +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", method=" + method +
                ", paypalTransaction='" + paypalTransaction + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }
}
