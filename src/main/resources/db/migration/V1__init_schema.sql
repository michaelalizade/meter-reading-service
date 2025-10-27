CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    keycloak_sub VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255),
    full_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE meters (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    meter_type VARCHAR(50) NOT NULL,
    meter_number VARCHAR(100) NOT NULL,
    installation_date DATE,
    location VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_meter_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_meter_number UNIQUE(user_id, meter_number)
);

CREATE TABLE readings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    meter_id UUID NOT NULL,
    reading_value DECIMAL(12,3) NOT NULL,
    reading_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reading_meter FOREIGN KEY (meter_id) REFERENCES meters(id) ON DELETE CASCADE,
    CONSTRAINT uk_meter_reading_date UNIQUE(meter_id, reading_date),
    CONSTRAINT chk_reading_value_positive CHECK (reading_value >= 0)
);

CREATE INDEX idx_meters_user_id ON meters(user_id);
CREATE INDEX idx_meters_type ON meters(meter_type);
CREATE INDEX idx_readings_meter_id ON readings(meter_id);
CREATE INDEX idx_readings_date ON readings(reading_date);
CREATE INDEX idx_users_keycloak_sub ON users(keycloak_sub);

--CREATE OR REPLACE FUNCTION update_updated_at_column()
--RETURNS TRIGGER AS $$
--BEGIN
--    NEW.updated_at = CURRENT_TIMESTAMP;
--    RETURN NEW;
--END;
--$$ language 'plpgsql';
--
--CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
--    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
--
--CREATE TRIGGER update_meters_updated_at BEFORE UPDATE ON meters
--    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
--
--CREATE TRIGGER update_readings_updated_at BEFORE UPDATE ON readings
--    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();