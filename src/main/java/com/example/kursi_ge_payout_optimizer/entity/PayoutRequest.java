package com.example.kursi_ge_payout_optimizer.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payout_requests")
public class PayoutRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private PayoutBatch batch;

    @Column(name = "request_reference", nullable = false)
    private String requestReference;

    @Column(name = "payout_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal payoutAmount;

    @Column(name = "agent_commission", nullable = false, precision = 19, scale = 2)
    private BigDecimal agentCommission;

    @Column(nullable = false)
    private boolean selected;

    protected PayoutRequest() {
    }

    public PayoutRequest(
            String requestReference,
            BigDecimal payoutAmount,
            BigDecimal agentCommission,
            boolean selected) {
        this.requestReference = requestReference;
        this.payoutAmount = payoutAmount;
        this.agentCommission = agentCommission;
        this.selected = selected;
    }

    void setBatch(PayoutBatch batch) {
        this.batch = batch;
    }

    public Long getId() {
        return id;
    }

    public String getRequestReference() {
        return requestReference;
    }

    public BigDecimal getPayoutAmount() {
        return payoutAmount;
    }

    public BigDecimal getAgentCommission() {
        return agentCommission;
    }

    public boolean isSelected() {
        return selected;
    }
}