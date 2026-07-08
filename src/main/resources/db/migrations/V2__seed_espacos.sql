INSERT INTO secoes (
    id,
    data_criacao,
    data_atualizacao,
    nome,
    tipo,
    capacidade,
    taxa_por_m2,
    ativa,
    ordem_visual
)
VALUES
    (
        '00000000-0000-0000-0000-000000000001',
        NOW(),
        NOW(),
        'Pedra',
        'PEDRA',
        40,
        15.00,
        TRUE,
        1
    ),
    (
        '00000000-0000-0000-0000-000000000002',
        NOW(),
        NOW(),
        'Box',
        'BOX',
        40,
        NULL,
        TRUE,
        2
    ),
    (
        '00000000-0000-0000-0000-000000000003',
        NOW(),
        NOW(),
        'Volante',
        'VOLANTE',
        166,
        NULL,
        TRUE,
        3
    )
ON CONFLICT (tipo) DO UPDATE SET
    nome = EXCLUDED.nome,
    capacidade = EXCLUDED.capacidade,
    taxa_por_m2 = EXCLUDED.taxa_por_m2,
    ativa = EXCLUDED.ativa,
    ordem_visual = EXCLUDED.ordem_visual,
    data_atualizacao = NOW();


INSERT INTO espacos (
    id,
    data_criacao,
    data_atualizacao,
    numero,
    secao_id,
    status_ocupacao,
    area_m2,
    ativo,
    pavilhao,
    grupo_visual,
    linha,
    coluna,
    ordem_visual
)
SELECT
    LOWER(
        REGEXP_REPLACE(
            MD5('PEDRA-' || LPAD(n::TEXT, 2, '0')),
            '(.{8})(.{4})(.{4})(.{4})(.{12})',
            '\1-\2-\3-\4-\5'
        )
    ) AS id,
    NOW() AS data_criacao,
    NOW() AS data_atualizacao,
    LPAD(n::TEXT, 2, '0') AS numero,
    '00000000-0000-0000-0000-000000000001' AS secao_id,
    'LIVRE' AS status_ocupacao,
    1.00 AS area_m2,
    TRUE AS ativo,
    NULL AS pavilhao,
    'PEDRA' AS grupo_visual,
    CEIL(n / 10.0)::INTEGER AS linha,
    (((n - 1) % 10) + 1) AS coluna,
    n AS ordem_visual
FROM GENERATE_SERIES(1, 40) AS n
ON CONFLICT (secao_id, numero) DO UPDATE SET
    area_m2 = EXCLUDED.area_m2,
    ativo = TRUE,
    pavilhao = EXCLUDED.pavilhao,
    grupo_visual = EXCLUDED.grupo_visual,
    linha = EXCLUDED.linha,
    coluna = EXCLUDED.coluna,
    ordem_visual = EXCLUDED.ordem_visual,
    data_atualizacao = NOW();


INSERT INTO espacos (
    id,
    data_criacao,
    data_atualizacao,
    numero,
    secao_id,
    status_ocupacao,
    area_m2,
    ativo,
    pavilhao,
    grupo_visual,
    linha,
    coluna,
    ordem_visual
)
SELECT
    LOWER(
        REGEXP_REPLACE(
            MD5('BOX-' || LPAD(n::TEXT, 2, '0')),
            '(.{8})(.{4})(.{4})(.{4})(.{12})',
            '\1-\2-\3-\4-\5'
        )
    ) AS id,
    NOW() AS data_criacao,
    NOW() AS data_atualizacao,
    LPAD(n::TEXT, 2, '0') AS numero,
    '00000000-0000-0000-0000-000000000002' AS secao_id,
    'LIVRE' AS status_ocupacao,
    NULL AS area_m2,
    TRUE AS ativo,
    CASE
        WHEN n <= 20 THEN 'Pavilhão 01'
        ELSE 'Pavilhão 02'
    END AS pavilhao,
    CASE
        WHEN n <= 20 THEN 'PAVILHAO_01'
        ELSE 'PAVILHAO_02'
    END AS grupo_visual,
    CASE
        WHEN n <= 20 THEN CEIL(n / 10.0)::INTEGER
        ELSE CEIL((n - 20) / 10.0)::INTEGER
    END AS linha,
    CASE
        WHEN n <= 20 THEN (((n - 1) % 10) + 1)
        ELSE ((((n - 20) - 1) % 10) + 1)
    END AS coluna,
    n AS ordem_visual
FROM GENERATE_SERIES(1, 40) AS n
ON CONFLICT (secao_id, numero) DO UPDATE SET
    area_m2 = EXCLUDED.area_m2,
    ativo = TRUE,
    pavilhao = EXCLUDED.pavilhao,
    grupo_visual = EXCLUDED.grupo_visual,
    linha = EXCLUDED.linha,
    coluna = EXCLUDED.coluna,
    ordem_visual = EXCLUDED.ordem_visual,
    data_atualizacao = NOW();


INSERT INTO espacos (
    id,
    data_criacao,
    data_atualizacao,
    numero,
    secao_id,
    status_ocupacao,
    area_m2,
    ativo,
    pavilhao,
    grupo_visual,
    linha,
    coluna,
    ordem_visual
)
SELECT
    LOWER(
        REGEXP_REPLACE(
            MD5(
                'VOLANTE-' ||
                CASE
                    WHEN n < 100 THEN LPAD(n::TEXT, 2, '0')
                    ELSE n::TEXT
                END
            ),
            '(.{8})(.{4})(.{4})(.{4})(.{12})',
            '\1-\2-\3-\4-\5'
        )
    ) AS id,
    NOW() AS data_criacao,
    NOW() AS data_atualizacao,
    CASE
        WHEN n < 100 THEN LPAD(n::TEXT, 2, '0')
        ELSE n::TEXT
    END AS numero,
    '00000000-0000-0000-0000-000000000003' AS secao_id,
    'LIVRE' AS status_ocupacao,
    NULL AS area_m2,
    TRUE AS ativo,
    NULL AS pavilhao,
    'VOLANTE' AS grupo_visual,
    CEIL(n / 24.0)::INTEGER AS linha,
    (((n - 1) % 24) + 1) AS coluna,
    n AS ordem_visual
FROM GENERATE_SERIES(1, 166) AS n
ON CONFLICT (secao_id, numero) DO UPDATE SET
    area_m2 = EXCLUDED.area_m2,
    ativo = TRUE,
    pavilhao = EXCLUDED.pavilhao,
    grupo_visual = EXCLUDED.grupo_visual,
    linha = EXCLUDED.linha,
    coluna = EXCLUDED.coluna,
    ordem_visual = EXCLUDED.ordem_visual,
    data_atualizacao = NOW();


UPDATE espacos
SET
    ativo = FALSE,
    status_ocupacao = 'INDISPONIVEL',
    data_atualizacao = NOW()
WHERE secao_id = '00000000-0000-0000-0000-000000000001'
  AND numero NOT IN (
      SELECT LPAD(n::TEXT, 2, '0')
      FROM GENERATE_SERIES(1, 40) AS n
  );


UPDATE espacos
SET
    ativo = FALSE,
    status_ocupacao = 'INDISPONIVEL',
    data_atualizacao = NOW()
WHERE secao_id = '00000000-0000-0000-0000-000000000002'
  AND numero NOT IN (
      SELECT LPAD(n::TEXT, 2, '0')
      FROM GENERATE_SERIES(1, 40) AS n
  );


UPDATE espacos
SET
    ativo = FALSE,
    status_ocupacao = 'INDISPONIVEL',
    data_atualizacao = NOW()
WHERE secao_id = '00000000-0000-0000-0000-000000000003'
  AND numero NOT IN (
      SELECT
          CASE
              WHEN n < 100 THEN LPAD(n::TEXT, 2, '0')
              ELSE n::TEXT
          END
      FROM GENERATE_SERIES(1, 166) AS n
  );