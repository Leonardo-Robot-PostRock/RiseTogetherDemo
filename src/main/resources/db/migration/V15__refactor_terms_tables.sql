-- Refactor terms acceptance into dedicated tables for full audit history.
-- Removes terms_version and terms_accepted_at columns from users (added in V14).

-- 1. Create terms_documents catalog (stores each published version + content)
CREATE TABLE terms_documents (
    id           CHAR(36)    NOT NULL PRIMARY KEY,
    terms_type   VARCHAR(30) NOT NULL,
    version      VARCHAR(10) NOT NULL,
    content      TEXT        NOT NULL,
    published_at TIMESTAMP   NOT NULL,
    UNIQUE KEY uk_terms_type_version (terms_type, version)
);

-- 2. Seed initial Terms of Service (version 1)
INSERT INTO terms_documents (id, terms_type, version, content, published_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'TERMS_OF_SERVICE',
    '1',
    'By creating an account and using ITJobs you agree to our Terms of Service. '
    'You must be at least 18 years old to use the platform. You are responsible '
    'for maintaining the confidentiality of your account credentials. ITJobs '
    'reserves the right to suspend accounts that violate these terms.',
    CURRENT_TIMESTAMP
);

-- 3. Create user_terms_acceptances audit table
CREATE TABLE user_terms_acceptances (
    id                CHAR(36)  NOT NULL PRIMARY KEY,
    user_id           CHAR(36)  NOT NULL,
    terms_document_id CHAR(36)  NOT NULL,
    accepted_at       TIMESTAMP NOT NULL,
    CONSTRAINT fk_uta_user FOREIGN KEY (user_id)           REFERENCES users(id),
    CONSTRAINT fk_uta_doc  FOREIGN KEY (terms_document_id) REFERENCES terms_documents(id)
);

CREATE INDEX idx_uta_user_id ON user_terms_acceptances(user_id);

-- 4. Migrate existing users: record their V14 acceptance against the initial ToS document
INSERT INTO user_terms_acceptances (id, user_id, terms_document_id, accepted_at)
SELECT UUID(), id, '00000000-0000-0000-0000-000000000001', terms_accepted_at
FROM users;

-- 5. Remove the V14 columns from users
ALTER TABLE users DROP INDEX idx_users_terms_version;
ALTER TABLE users
    DROP COLUMN terms_version,
    DROP COLUMN terms_accepted_at;

