package com.example.kursi_ge_payout_optimizer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kursi_ge_payout_optimizer.entity.PayoutBatch;

public interface PayoutBatchRepository extends JpaRepository<PayoutBatch, UUID> {

}