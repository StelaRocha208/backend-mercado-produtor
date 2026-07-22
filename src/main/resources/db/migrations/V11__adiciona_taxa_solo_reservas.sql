ALTER TABLE reservas
    ADD COLUMN area_m2_cobrada NUMERIC(10, 2),
    ADD COLUMN tarifa_por_m2_aplicada NUMERIC(10, 2),
    ADD COLUMN dias_uso INTEGER,
    ADD COLUMN valor_taxa_solo NUMERIC(14, 2);
