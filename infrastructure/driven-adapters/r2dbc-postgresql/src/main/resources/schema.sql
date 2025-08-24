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
