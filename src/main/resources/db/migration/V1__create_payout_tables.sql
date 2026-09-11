CREATE TABLE payout_batches (
    id UUID PRIMARY KEY,
    available_payout_float NUMERIC(19, 2) NOT NULL,
    total_agent_commision NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE payout_requests (
    id BIGSERIAL PRIMARY KEY,
    batch_id UUID NOT NULL REFERENCES payout_batches(id) ON DELETE CASCADE,
    request_reference VARCHAR(255) NOT NULL,
    payout_amount NUMERIC(19, 2) NOT NULL,
    agent_commision NUMERIC(19, 2) NOT NULL,
    selected BOOLEAN NOT NULL
);
CREATE INDEX idx_payout_batches_created_at ON payout_batches(created_at);
CREATE INDEX idx_payout_requests_batch_id ON payout_requests(batch_id);