-- Add missing columns to employers table to match EmployerEntity mapping
ALTER TABLE employers ADD COLUMN logo_url VARCHAR(500);
ALTER TABLE employers ADD COLUMN description TEXT;

