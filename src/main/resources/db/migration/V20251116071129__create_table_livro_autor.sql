-- Tabela de relacionamento entre livro e autor
CREATE TABLE livro_autor
(
    livro_codl  INTEGER NOT NULL,
    autor_codau INTEGER NOT NULL,
    PRIMARY KEY (livro_codl, autor_codau),
    CONSTRAINT fk_livro_autor_livro FOREIGN KEY (livro_codl)
        REFERENCES livro (codl) ON DELETE CASCADE,
    CONSTRAINT fk_livro_autor_autor FOREIGN KEY (autor_codau)
        REFERENCES autor (codau) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_livro_autor_livro ON livro_autor (livro_codl);
CREATE INDEX idx_livro_autor_autor ON livro_autor (autor_codau);

-- Comentários
COMMENT ON TABLE livro_autor IS 'Tabela de relacionamento N:N entre Livro e Autor';
COMMENT ON COLUMN livro_autor.livro_codl IS 'Código do livro (FK)';
COMMENT ON COLUMN livro_autor.autor_codau IS 'Código do autor (FK)';