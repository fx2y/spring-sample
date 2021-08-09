CREATE TABLE IF NOT EXISTS posts
(
    id         UUID        DEFAULT gen_random_uuid(),
    title      VARCHAR(255),
    content    VARCHAR(255),
    metadata   JSON        default '{}',
    -- Spring data r2dbc can convert Java Enum to pg VARCHAR, and reverse.
    status     post_status default 'DRAFT',
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version    INTEGER,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id         UUID DEFAULT gen_random_uuid(),
    content    VARCHAR(255),
    post_id    UUID REFERENCES posts ON DELETE CASCADE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version    INTEGER,
    PRIMARY KEY (id)
);