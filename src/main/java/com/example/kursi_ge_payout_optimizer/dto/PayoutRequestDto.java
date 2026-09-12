package com.example.kursi_ge_payout_optimizer.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PayoutRequestDto(
        @NotBlank String requestReference,

        @NotNull @Positive BigDecimal payoutAmount,

        @NotNull @Positive BigDecimal agentCommission

) {
}