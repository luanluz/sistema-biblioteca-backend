-- Tabela de assuntos
CREATE TABLE assunto
(
    codas     SERIAL PRIMARY KEY NOT NULL UNIQUE,
    descricao VARCHAR(20) NOT NULL
);

-- Comentários nas colunas
COMMENT ON TABLE assunto IS 'Tabela de assuntos';
COMMENT ON COLUMN assunto.codas IS 'Código do assunto (PK)';
COMMENT ON COLUMN assunto.descricao IS 'Descrição do assunto';
