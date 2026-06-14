-- ----------------------------------------------------------
-- SISTEMA DOSECERTA - SCRIPT DE CRIAÇÃO DO BANCO DE DADOS
-- Banco de Dados: MySQL 8+
-------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS dosecerta_db;
USE dosecerta_db;

-- --------------------------------------------------------
-- TABELA DE USUÁRIOS (Para o Login / Spring Security)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Senha criptografada (BCrypt)
    role VARCHAR(20) DEFAULT 'ROLE_USER',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------
-- TABELA DE MEDICAMENTOS (Gestão de Estoque)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS medicamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    dosagem VARCHAR(50) NOT NULL,
    quantidade INT NOT NULL CHECK (quantidade >= 0),
    data_validade DATE NOT NULL,
    lote VARCHAR(50),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT,

    -- Chave estrangeira: Vincula quem cadastrou o remédio
    CONSTRAINT fk_medicamento_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE SET NULL
);

-- --------------------------------------------------------
-- Senha padrão gerada via BCrypt (ex: 'admin123')
INSERT INTO usuarios (nome, username, password, role)
VALUES ('Administrador', 'admin', '$2a$10$wK/p8fX.uB0GvC./7.2r0.7v9.gR0/p8fX.uB0GvC./7.2r0', 'ROLE_ADMIN');

INSERT INTO medicamentos (nome, dosagem, quantidade, data_validade, lote, usuario_id)
VALUES
('Dipirona Sódica', '500mg', 45, '2025-12-01', 'L-10293', 1),
('Amoxicilina', '875mg', 8, '2023-10-15', 'L-99821', 1),
('Losartana Potássica', '50mg', 120, '2026-05-20', 'L-44322', 1);