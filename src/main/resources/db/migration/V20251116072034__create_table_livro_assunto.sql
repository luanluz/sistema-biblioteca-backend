-- Tabela de relacionamento entre livro e assunto
CREATE TABLE livro_assunto
(
    livro_codl    INTEGER NOT NULL,
    assunto_codas INTEGER NOT NULL,
    PRIMARY KEY (livro_codl, assunto_codas),
    CONSTRAINT fk_livro_assunto_livro FOREIGN KEY (livro_codl)
        REFERENCES livro (codl) ON DELETE CASCADE,
    CONSTRAINT fk_livro_assunto_assunto FOREIGN KEY (assunto_codas)
        REFERENCES assunto (codas) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_livro_assunto_livro ON livro_assunto (livro_codl);
CREATE INDEX idx_livro_assunto_assunto ON livro_assunto (assunto_codas);

-- Comentários
COMMENT ON TABLE livro_assunto IS 'Tabela de relacionamento N:N entre Livro e Assunto';
COMMENT ON COLUMN livro_assunto.livro_codl IS 'Código do livro (FK)';
COMMENT ON COLUMN livro_assunto.assunto_codas IS 'Código do assunto (FK)';