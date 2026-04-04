-- backend/src/main/resources/init.sql
-- Legt die Tabelle tours an und fügt die Wienerwald-Demo-Tour idempotent ein.
CREATE TABLE IF NOT EXISTS tours (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    from_place VARCHAR(500) NOT NULL,
    to_place VARCHAR(500) NOT NULL,
    transport_type VARCHAR(50) NOT NULL,
    distance DOUBLE PRECISION NOT NULL,
    estimated_time_seconds BIGINT NOT NULL,
    image VARCHAR(2000)
);

-- Demo-Tour 
INSERT INTO tours (name, description, from_place, to_place, transport_type, distance, estimated_time_seconds, image)
SELECT 'Wienerwald Tour',
    'Rundtour durch den Wienerwald mit Aussichtspunkten und ruhigen Waldwegen.',
    'Wien Hietzing',
    'Kaltenleutgeben',
    'BICYCLE',
    35.2,
    14400,
    '/assets/tours/wienerwald.jpg'
WHERE NOT EXISTS (SELECT 1 FROM tours WHERE name = 'Wienerwald Tour');
