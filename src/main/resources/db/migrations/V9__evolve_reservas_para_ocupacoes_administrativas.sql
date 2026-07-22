UPDATE secoes
SET
    nome = 'Volátil',
    tipo = 'VOLATIL',
    data_atualizacao = NOW()
WHERE tipo = 'VOLANTE';

UPDATE espacos
SET
    grupo_visual = 'VOLATIL',
    data_atualizacao = NOW()
WHERE grupo_visual = 'VOLANTE';

ALTER TABLE reservas
    ADD COLUMN IF NOT EXISTS tipo_reserva VARCHAR(30) NOT NULL DEFAULT 'DIARIA',
    ADD COLUMN IF NOT EXISTS reserva_origem_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS data_encerramento DATE,
    ADD COLUMN IF NOT EXISTS motivo_encerramento VARCHAR(500);

ALTER TABLE reservas
    ADD CONSTRAINT fk_reservas_origem
        FOREIGN KEY (reserva_origem_id)
        REFERENCES reservas (id),
    ADD CONSTRAINT ck_reservas_tipo
        CHECK (tipo_reserva IN ('DIARIA', 'TITULAR', 'PROVISORIA')),
    ADD CONSTRAINT ck_reservas_encerramento
        CHECK (
            data_encerramento IS NULL
            OR data_encerramento BETWEEN data_inicio AND data_fim
        ),
    ADD CONSTRAINT ck_reservas_origem_provisoria
        CHECK (
            (tipo_reserva IN ('DIARIA', 'TITULAR') AND reserva_origem_id IS NULL)
            OR (tipo_reserva = 'PROVISORIA' AND reserva_origem_id IS NOT NULL)
        );

CREATE INDEX IF NOT EXISTS idx_reservas_tipo
    ON reservas (tipo_reserva);

CREATE INDEX IF NOT EXISTS idx_reservas_origem_id
    ON reservas (reserva_origem_id);
