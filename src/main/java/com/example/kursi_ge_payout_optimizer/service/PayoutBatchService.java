package com.example.kursi_ge_payout_optimizer.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.kursi_ge_payout_optimizer.algorithm.OptimizationResult;
import com.example.kursi_ge_payout_optimizer.algorithm.PayoutCandidate;
import com.example.kursi_ge_payout_optimizer.algorithm.PayoutOptimizer;
import com.example.kursi_ge_payout_optimizer.dto.OptimizeBatchRequest;
import com.example.kursi_ge_payout_optimizer.dto.OptimizeBatchResponse;
import com.example.kursi_ge_payout_optimizer.dto.SelectedPayoutDto;
import com.example.kursi_ge_payout_optimizer.entity.PayoutBatch;
import com.example.kursi_ge_payout_optimizer.entity.PayoutRequest;
import com.example.kursi_ge_payout_optimizer.repository.PayoutBatchRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PayoutBatchService {

    private final PayoutBatchRepository repository;
    private final PayoutOptimizer optimizer;

    public PayoutBatchService(PayoutBatchRepository repository) {
        this.repository = repository;
        this.optimizer = new PayoutOptimizer();
    }

    @Transactional
    public OptimizeBatchResponse optimize(OptimizeBatchRequest request) {
        List<PayoutCandidate> candidates = request.payoutRequests().stream()
                .map(payout -> new PayoutCandidate(payout.requestReference(), payout.payoutAmount(),
                        payout.agentCommission()))
                .toList();

        OptimizationResult result = optimizer.optimize(request.availablePayoutFloat(), candidates);

        UUID batchId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        PayoutBatch batch = new PayoutBatch(batchId, request.availablePayoutFloat(), result.totalFloatConsumed(),
                result.totalAgentCommission(), createdAt);

        for (PayoutCandidate candidate : candidates) {
            boolean selected = result.selectedPayouts().contains(candidate);

            batch.addPayoutRequest(new PayoutRequest(candidate.requestReference(), candidate.payoutAmount(),
                    candidate.agentCommission(), selected));
        }

        repository.save(batch);

        List<SelectedPayoutDto> selectedPayouts = result.selectedPayouts().stream()
                .map(payout -> new SelectedPayoutDto(payout.requestReference(), payout.payoutAmount(),
                        payout.agentCommission()))
                .toList();

        return new OptimizeBatchResponse(batchId, selectedPayouts, result.totalFloatConsumed(),
                result.totalAgentCommission(), createdAt);
    }

    private OptimizeBatchResponse toResponse(PayoutBatch batch) {
        List<SelectedPayoutDto> selectedPayouts = batch.getPayoutRequests().stream()
                .filter(PayoutRequest::isSelected)
                .map(payout -> new SelectedPayoutDto(
                        payout.getRequestReference(),
                        payout.getPayoutAmount(),
                        payout.getAgentCommission()))
                .toList();

        return new OptimizeBatchResponse(
                batch.getId(),
                selectedPayouts,
                batch.getTotalFloatConsumed(),
                batch.getTotalAgentCommission(),
                batch.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public OptimizeBatchResponse getById(UUID batchId) {
        PayoutBatch batch = repository.findById(batchId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payout batch not found with id: " + batchId));

        return toResponse(batch);
    }

    @Transactional(readOnly = true)
    public Page<OptimizeBatchResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(entity -> this.toResponse(entity));
    }
}
