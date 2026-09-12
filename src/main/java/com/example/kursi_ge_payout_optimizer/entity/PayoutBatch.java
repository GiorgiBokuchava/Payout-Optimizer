package com.example.kursi_ge_payout_optimizer.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "payout_batches")
public class PayoutBatch {

    @Id
    private UUID id;

    @Column(name = "available_payout_float", nullable = false, precision = 19, scale = 2)
    private BigDecimal availablePayoutFloat;

    @Column(name = "total_float_consumed", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalFloatConsumed;

    @Column(name = "total_agent_commission", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAgentCommission;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PayoutRequest> payoutRequests = new ArrayList<>();

    protected PayoutBatch() {
    }

    public PayoutBatch(
            UUID id,
            BigDecimal availablePayoutFloat,
            BigDecimal totalFloatConsumed,
            BigDecimal totalAgentCommission,
            Instant createdAt) {
        this.id = id;
        this.availablePayoutFloat = availablePayoutFloat;
        this.totalFloatConsumed = totalFloatConsumed;
        this.totalAgentCommission = totalAgentCommission;
        this.createdAt = createdAt;
    }

    public void addPayoutRequest(PayoutRequest request) {
        payoutRequests.add(request);
        request.setBatch(this);
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getAvailablePayoutFloat() {
        return availablePayoutFloat;
    }

    public BigDecimal getTotalFloatConsumed() {
        return totalFloatConsumed;
    }

    public BigDecimal getTotalAgentCommission() {
        return totalAgentCommission;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<PayoutRequest> getPayoutRequests() {
        return payoutRequests;
    }
}