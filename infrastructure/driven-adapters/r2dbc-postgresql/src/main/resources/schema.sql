-- Script para crear la tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombres VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(15),
    correo_electronico VARCHAR(100) NOT NULL UNIQUE,
    salario_base DECIMAL(12,2) NOT NULL CHECK (salario_base >= 0 AND salario_base <= 15000000),
    fecha_registro DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Índice para mejorar el rendimiento de búsquedas por email
CREATE INDEX IF NOT EXISTS idx_usuarios_correo_electronico ON usuarios(correo_electronico);

-- Índice para mejorar el rendimiento de búsquedas por nombres y apellidos
CREATE INDEX IF NOT EXISTS idx_usuarios_nombres_apellidos ON usuarios(nombres, apellidos);

-- Datos iniciales para pruebas
INSERT INTO usuarios (nombres, apellidos, fecha_nacimiento, direccion, telefono, correo_electronico, salario_base, fecha_registro) VALUES
('Juan Carlos', 'Garcia Lopez', '1990-05-15', 'Calle 123 #45-67, Bogota', '3001234567', 'juan.garcia@email.com', 2500000, CURRENT_DATE),
('Maria Isabel', 'Rodriguez Silva', '1985-08-22', 'Carrera 78 #12-34, Medellin', '3109876543', 'maria.rodriguez@email.com', 3200000, CURRENT_DATE),
('Carlos Alberto', 'Martinez Torres', '1992-03-10', 'Avenida 5 #23-45, Cali', '3155551234', 'carlos.martinez@email.com', 2800000, CURRENT_DATE),
('Ana Sofia', 'Hernandez Diaz', '1988-11-30', 'Calle 90 #67-89, Barranquilla', '3204445678', 'ana.hernandez@email.com', 3000000, CURRENT_DATE),
('Luis Fernando', 'Gonzalez Ruiz', '1995-07-18', 'Carrera 15 #45-67, Bucaramanga', '3007778901', 'luis.gonzalez@email.com', 2200000, CURRENT_DATE)
ON CONFLICT (correo_electronico) DO NOTHING;
