CREATE TABLE processed_webhook_events
(
    event_id     VARCHAR(255) PRIMARY KEY,
    event_type   VARCHAR(255)             NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL
);