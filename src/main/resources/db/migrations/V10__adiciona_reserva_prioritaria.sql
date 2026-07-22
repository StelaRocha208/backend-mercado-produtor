ALTER TABLE reservas
    DROP CONSTRAINT IF EXISTS ck_reservas_tipo,
    DROP CONSTRAINT IF EXISTS ck_reservas_origem_provisoria;

ALTER TABLE reservas
    ADD CONSTRAINT ck_reservas_tipo
        CHECK (
            tipo_reserva IN (
                'DIARIA',
                'PRIORITARIA',
                'TITULAR',
                'PROVISORIA'
            )
        ),
    ADD CONSTRAINT ck_reservas_origem_provisoria
        CHECK (
            (
                tipo_reserva IN ('DIARIA', 'PRIORITARIA', 'TITULAR')
                AND reserva_origem_id IS NULL
            )
            OR (tipo_reserva = 'PROVISORIA' AND reserva_origem_id IS NOT NULL)
        );
