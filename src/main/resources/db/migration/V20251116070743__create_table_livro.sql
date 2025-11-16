-- Tabela de livros
CREATE TABLE livro
(
    codl            SERIAL PRIMARY KEY NOT NULL UNIQUE,
    titulo          VARCHAR(40) NOT NULL,
    editora         VARCHAR(40),
    edicao          INTEGER,
    anopublicacao   VARCHAR(4),
    valoremcentavos INTEGER DEFAULT 0
);

-- Comentários nas colunas
COMMENT ON TABLE livro IS 'Tabela de livros';
COMMENT ON COLUMN livro.codl IS 'Código do livro (PK)';
COMMENT ON COLUMN livro.titulo IS 'Título do livro';
COMMENT ON COLUMN livro.editora IS 'Editora do livro';
COMMENT ON COLUMN livro.edicao IS 'Edição do livro';
COMMENT ON COLUMN livro.anopublicacao IS 'Ano de publicação do livro';
COMMENT ON COLUMN livro.valoremcentavos IS 'Valor do livro em centavos';
