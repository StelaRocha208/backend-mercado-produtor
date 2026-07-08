CREATE TABLE IF NOT EXISTS secoes (
    id VARCHAR(255) PRIMARY KEY,
    data_criacao TIMESTAMP(6) NOT NULL,
    data_atualizacao TIMESTAMP(6),

    nome VARCHAR(80) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    capacidade INTEGER NOT NULL,
    taxa_por_m2 NUMERIC(10, 2),
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    ordem_visual INTEGER,

    CONSTRAINT uk_secoes_tipo UNIQUE (tipo)
);

CREATE TABLE IF NOT EXISTS espacos (
    id VARCHAR(255) PRIMARY KEY,
    data_criacao TIMESTAMP(6) NOT NULL,
    data_atualizacao TIMESTAMP(6),

    numero VARCHAR(10) NOT NULL,
    secao_id VARCHAR(255) NOT NULL,
    status_ocupacao VARCHAR(30) NOT NULL DEFAULT 'LIVRE',
    area_m2 NUMERIC(10, 2),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    pavilhao VARCHAR(80),
    grupo_visual VARCHAR(80),
    linha INTEGER,
    coluna INTEGER,
    ordem_visual INTEGER,

    CONSTRAINT fk_espacos_secao
        FOREIGN KEY (secao_id)
        REFERENCES secoes (id),

    CONSTRAINT uk_espaco_secao_numero
        UNIQUE (secao_id, numero)
);

CREATE INDEX IF NOT EXISTS idx_espacos_secao_id
    ON espacos (secao_id);

CREATE INDEX IF NOT EXISTS idx_espacos_ativo
    ON espacos (ativo);

CREATE INDEX IF NOT EXISTS idx_espacos_status_ocupacao
    ON espacos (status_ocupacao);

CREATE INDEX IF NOT EXISTS idx_secoes_tipo
    ON secoes (tipo);