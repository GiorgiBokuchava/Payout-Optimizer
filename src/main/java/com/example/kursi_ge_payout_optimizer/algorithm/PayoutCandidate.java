package com.example.kursi_ge_payout_optimizer.algorithm;

import java.math.BigDecimal;

public record PayoutCandidate(
                String requestReference,
                BigDecimal payoutAmount,
                BigDecimal agentCommission) {
}