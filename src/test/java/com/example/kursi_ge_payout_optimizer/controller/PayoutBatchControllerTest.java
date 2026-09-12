package com.example.kursi_ge_payout_optimizer.controller;

import com.example.kursi_ge_payout_optimizer.repository.PayoutBatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PayoutBatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PayoutBatchRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void optimizeReturnsCreatedBatch() throws Exception {
        String requestBody = """
                {
                  "availablePayoutFloat": 12000,
                  "payoutRequests": [
                    {
                      "requestReference": "PO-3001",
                      "payoutAmount": 4000,
                      "agentCommission": 90
                    },
                    {
                      "requestReference": "PO-3002",
                      "payoutAmount": 6000,
                      "agentCommission": 150
                    },
                    {
                      "requestReference": "PO-3003",
                      "payoutAmount": 2500,
                      "agentCommission": 55
                    },
                    {
                      "requestReference": "PO-3004",
                      "payoutAmount": 5000,
                      "agentCommission": 115
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/payout-batches/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.batchId").exists())
                .andExpect(jsonPath("$.totalFloatConsumed").value(11000))
                .andExpect(jsonPath("$.totalAgentCommission").value(265))
                .andExpect(jsonPath("$.selectedPayouts.length()").value(2));

        assertEquals(1, repository.count());
    }

    @Test
    void invalidRequestReturnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "availablePayoutFloat": -100,
                  "payoutRequests": []
                }
                """;

        mockMvc.perform(post("/api/v1/payout-batches/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void unknownBatchReturnsNotFound() throws Exception {
        mockMvc.perform(get(
                "/api/v1/payout-batches/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllReturnsPaginatedResults() throws Exception {
        String requestBody = """
                {
                  "availablePayoutFloat": 12000,
                  "payoutRequests": [
                    {
                      "requestReference": "PO-1",
                      "payoutAmount": 4000,
                      "agentCommission": 90
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/payout-batches/optimize")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/payout-batches")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}