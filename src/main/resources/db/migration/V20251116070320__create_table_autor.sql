-- Tabela de autores
CREATE TABLE autor
(
    codau SERIAL PRIMARY KEY NOT NULL UNIQUE,
    nome  VARCHAR(40) NOT NULL
);

-- Comentários nas colunas
COMMENT ON TABLE autor IS 'Tabela de autores';
COMMENT ON COLUMN autor.codau IS 'Código do autor (PK)';
COMMENT ON COLUMN autor.nome IS 'Nome do autor';
