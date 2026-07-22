UPDATE secoes
SET
    taxa_por_m2 = CASE tipo
        WHEN 'BOX' THEN 2.50
        WHEN 'VOLATIL' THEN 1.20
        ELSE taxa_por_m2
    END,
    data_atualizacao = NOW()
WHERE taxa_por_m2 IS NULL
  AND tipo IN ('PEDRA', 'BOX', 'VOLATIL');

UPDATE espacos e
SET
    area_m2 = CASE s.tipo
        WHEN 'PEDRA' THEN 1.00
        WHEN 'BOX' THEN 12.00
        WHEN 'VOLATIL' THEN 40.00
    END,
    data_atualizacao = NOW()
FROM secoes s
WHERE e.secao_id = s.id
  AND e.area_m2 IS NULL
  AND s.tipo IN ('PEDRA', 'BOX', 'VOLATIL');
