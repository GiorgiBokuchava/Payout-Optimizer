package com.example.kursi_ge_payout_optimizer.algorithm;

import java.math.BigDecimal;
import java.util.List;

public record OptimizationResult(List<PayoutCandidate> selectedPayouts, BigDecimal totalFloatConsumed,
        BigDecimal totalAgentCommission) {
}