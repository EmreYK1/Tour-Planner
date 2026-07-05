-- backend/src/main/resources/init.sql
-- Legt alle Tabellen idempotent an (vollständiges Schema inkl. aller Spalten).

-- 1. users zuerst, da tours darauf referenziert
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 2. tours mit allen Spalten
CREATE TABLE IF NOT EXISTS tours (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    from_place VARCHAR(500) NOT NULL,
    to_place VARCHAR(500) NOT NULL,
    transport_type VARCHAR(50) NOT NULL,
    distance DOUBLE PRECISION NOT NULL,
    estimated_time_seconds BIGINT NOT NULL,
    image VARCHAR(2000),
    route_geometry TEXT,
    from_lon DOUBLE PRECISION,
    from_lat DOUBLE PRECISION,
    to_lon DOUBLE PRECISION,
    to_lat DOUBLE PRECISION,
    owner_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 3. tour_logs mit allen Spalten
CREATE TABLE IF NOT EXISTS tour_logs (
    id BIGSERIAL PRIMARY KEY,
    tour_id BIGINT NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
    date_time TIMESTAMP NOT NULL,
    comment TEXT,
    difficulty INT NOT NULL,
    total_distance DOUBLE PRECISION NOT NULL,
    total_time BIGINT NOT NULL,
    rating INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Sicherheitsnetz: Spalten nachträglich hinzufügen falls alte DB existiert
ALTER TABLE tours ADD COLUMN IF NOT EXISTS route_geometry TEXT;
ALTER TABLE tours ADD COLUMN IF NOT EXISTS owner_id BIGINT REFERENCES users(id);
ALTER TABLE tours ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE tours ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE tours ADD COLUMN IF NOT EXISTS from_lon DOUBLE PRECISION;
ALTER TABLE tours ADD COLUMN IF NOT EXISTS from_lat DOUBLE PRECISION;
ALTER TABLE tours ADD COLUMN IF NOT EXISTS to_lon DOUBLE PRECISION;
ALTER TABLE tours ADD COLUMN IF NOT EXISTS to_lat DOUBLE PRECISION;
ALTER TABLE tour_logs ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE tour_logs ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();
