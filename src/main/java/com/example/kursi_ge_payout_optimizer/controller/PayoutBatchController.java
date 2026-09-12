package com.example.kursi_ge_payout_optimizer.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.kursi_ge_payout_optimizer.dto.OptimizeBatchRequest;
import com.example.kursi_ge_payout_optimizer.dto.OptimizeBatchResponse;
import com.example.kursi_ge_payout_optimizer.service.PayoutBatchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payout-batches")
public class PayoutBatchController {

    private final PayoutBatchService service;

    public PayoutBatchController(PayoutBatchService service) {
        this.service = service;
    }

    @PostMapping("/optimize")
    @ResponseStatus(HttpStatus.CREATED)
    public OptimizeBatchResponse optimize(
            @Valid @RequestBody OptimizeBatchRequest request) {
        return service.optimize(request);
    }

    @GetMapping("/{batchId}")
    public OptimizeBatchResponse getById(@PathVariable UUID batchId) {
        return service.getById(batchId);
    }

    @GetMapping
    public Page<OptimizeBatchResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return service.getAll(pageable);
    }
}