CREATE SCHEMA IF NOT EXISTS easy_wallet;
CREATE TABLE easy_wallet.wallet(
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR NOT NULL,
    balance bigint NOT NULL
);