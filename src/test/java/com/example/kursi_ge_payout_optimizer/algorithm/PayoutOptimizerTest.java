package com.example.kursi_ge_payout_optimizer.algorithm;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PayoutOptimizerTest {

    private final PayoutOptimizer optimizer = new PayoutOptimizer();

    @Test
    void selectsCombinationWithMaximumCommission() {
        List<PayoutCandidate> candidates = List.of(
                candidate("PO-3001", "4000", "90"),
                candidate("PO-3002", "6000", "150"),
                candidate("PO-3003", "2500", "55"),
                candidate("PO-3004", "5000", "115"));

        OptimizationResult result = optimizer.optimize(
                bd("12000"),
                candidates);

        assertEquals(bd("11000"), result.totalFloatConsumed());
        assertEquals(bd("265"), result.totalAgentCommission());

        assertEquals(
                List.of("PO-3002", "PO-3004"),
                result.selectedPayouts()
                        .stream()
                        .map(PayoutCandidate::requestReference)
                        .toList());
    }

    @Test
    void returnsEmptySelectionWhenNothingFits() {
        List<PayoutCandidate> candidates = List.of(
                candidate("PO-1", "5000", "100"),
                candidate("PO-2", "6000", "120"));

        OptimizationResult result = optimizer.optimize(
                bd("1000"),
                candidates);

        assertTrue(result.selectedPayouts().isEmpty());
        assertEquals(0, result.totalFloatConsumed().compareTo(BigDecimal.ZERO));
        assertEquals(0, result.totalAgentCommission().compareTo(BigDecimal.ZERO));
    }

    @Test
    void allowsExactFloatLimit() {
        List<PayoutCandidate> candidates = List.of(
                candidate("PO-1", "4000", "90"),
                candidate("PO-2", "6000", "150"));

        OptimizationResult result = optimizer.optimize(
                bd("10000"),
                candidates);

        assertEquals(bd("10000"), result.totalFloatConsumed());
        assertEquals(bd("240"), result.totalAgentCommission());
    }

    private PayoutCandidate candidate(
            String reference,
            String payout,
            String commission) {
        return new PayoutCandidate(
                reference,
                bd(payout),
                bd(commission));
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}