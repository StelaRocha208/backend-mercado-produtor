CREATE TABLE IF NOT EXISTS reservas (
    id VARCHAR(255) PRIMARY KEY,
    data_criacao TIMESTAMP(6) NOT NULL,
    data_atualizacao TIMESTAMP(6),

    produtor_id VARCHAR(255) NOT NULL,
    espaco_id VARCHAR(255) NOT NULL,

    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,

    status_reserva VARCHAR(40) NOT NULL,
    observacao VARCHAR(500),

    CONSTRAINT fk_reservas_produtor
        FOREIGN KEY (produtor_id)
        REFERENCES produtores (id),

    CONSTRAINT fk_reservas_espaco
        FOREIGN KEY (espaco_id)
        REFERENCES espacos (id),

    CONSTRAINT ck_reservas_periodo
        CHECK (data_fim >= data_inicio)
);

CREATE INDEX IF NOT EXISTS idx_reservas_produtor_id
    ON reservas (produtor_id);

CREATE INDEX IF NOT EXISTS idx_reservas_espaco_id
    ON reservas (espaco_id);

CREATE INDEX IF NOT EXISTS idx_reservas_periodo
    ON reservas (data_inicio, data_fim);

CREATE INDEX IF NOT EXISTS idx_reservas_status
    ON reservas (status_reserva);