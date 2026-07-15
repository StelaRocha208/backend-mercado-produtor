ALTER TABLE produtores
ADD COLUMN inadimplente BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE produtores
ADD COLUMN justificativa_inadimplencia VARCHAR(500);