-- Agregar campos de aceptación de términos y condiciones a users
-- Por temas legales y auditoría

ALTER TABLE users
ADD COLUMN terms_version VARCHAR(10) NOT NULL DEFAULT '1',
ADD COLUMN terms_accepted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_users_terms_version ON users(terms_version);