package com.example.kursi_ge_payout_optimizer.algorithm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PayoutOptimizer {

    public OptimizationResult optimize(
            BigDecimal availablePayoutFloat,
            List<PayoutCandidate> candidates) {
        List<PayoutCandidate> currentSelection = new ArrayList<>();

        return findBest(
                candidates,
                0,
                availablePayoutFloat,
                currentSelection,
                BigDecimal.ZERO,
                BigDecimal.ZERO);
    }

    private OptimizationResult findBest(
            List<PayoutCandidate> candidates,
            int index,
            BigDecimal remainingFloat,
            List<PayoutCandidate> currentSelection,
            BigDecimal currentFloatConsumed,
            BigDecimal currentCommission) {
        // Base case: every candidate has been considered.
        if (index == candidates.size()) {
            return new OptimizationResult(
                    List.copyOf(currentSelection),
                    currentFloatConsumed,
                    currentCommission);
        }

        PayoutCandidate candidate = candidates.get(index);

        OptimizationResult bestWithoutCandidate = findBest(
                candidates,
                index + 1,
                remainingFloat,
                currentSelection,
                currentFloatConsumed,
                currentCommission);

        if (candidate.payoutAmount().compareTo(remainingFloat) > 0) {
            return bestWithoutCandidate;
        }

        currentSelection.add(candidate);

        OptimizationResult bestWithCandidate = findBest(
                candidates,
                index + 1,
                remainingFloat.subtract(candidate.payoutAmount()),
                currentSelection,
                currentFloatConsumed.add(candidate.payoutAmount()),
                currentCommission.add(candidate.agentCommission()));

        currentSelection.remove(currentSelection.size() - 1);

        if (bestWithCandidate.totalAgentCommission()
                .compareTo(bestWithoutCandidate.totalAgentCommission()) > 0) {
            return bestWithCandidate;
        }

        return bestWithoutCandidate;
    }

    public static void main(String[] args) {
        PayoutOptimizer optimizer = new PayoutOptimizer();

        List<PayoutCandidate> candidates = List.of(
                new PayoutCandidate("req1", new BigDecimal("100"), new BigDecimal("10")),
                new PayoutCandidate("req2", new BigDecimal("200"), new BigDecimal("20")),
                new PayoutCandidate("req3", new BigDecimal("150"), new BigDecimal("15")),
                new PayoutCandidate("req4", new BigDecimal("50"), new BigDecimal("5")));

        BigDecimal availableFloat = new BigDecimal("300");

        OptimizationResult result = optimizer.optimize(availableFloat, candidates);

        System.out.println("Selected Payouts:");
        for (PayoutCandidate candidate : result.selectedPayouts()) {
            System.out.println(candidate.requestReference() + " - Amount: " + candidate.payoutAmount()
                    + ", Commission: " + candidate.agentCommission());
        }
        System.out.println("Total Float Consumed: " + result.totalFloatConsumed());
        System.out.println("Total Agent Commission: " + result.totalAgentCommission());
    }
}