package com.example.kursi_ge_payout_optimizer.controller;

import com.example.kursi_ge_payout_optimizer.dto.OptimizeBatchRequest;
import com.example.kursi_ge_payout_optimizer.dto.OptimizeBatchResponse;
import com.example.kursi_ge_payout_optimizer.service.PayoutBatchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}