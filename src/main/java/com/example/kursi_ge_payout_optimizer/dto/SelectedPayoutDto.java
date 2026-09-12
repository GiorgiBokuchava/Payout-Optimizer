package com.example.kursi_ge_payout_optimizer.dto;

import java.math.BigDecimal;

public record SelectedPayoutDto(
        String requestReference,
        BigDecimal payoutAmount,
        BigDecimal agentCommission) {
}