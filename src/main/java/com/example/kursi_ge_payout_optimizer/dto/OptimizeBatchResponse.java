package com.example.kursi_ge_payout_optimizer.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OptimizeBatchResponse(
        UUID batchId,
        List<SelectedPayoutDto> selectedPayouts,
        BigDecimal totalFloatConsumed,
        BigDecimal totalAgentCommission,
        Instant createdAt) {

}
