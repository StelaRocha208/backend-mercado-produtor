ALTER TABLE registros
ADD COLUMN reserva_id VARCHAR(36);

ALTER TABLE registros
ADD CONSTRAINT fk_registro_reserva
FOREIGN KEY (reserva_id)
REFERENCES reservas(id);