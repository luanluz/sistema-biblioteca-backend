-- Aplica desconto percentual em todos os livros de um autor
CREATE OR REPLACE PROCEDURE sp_aplicar_desconto_em_livros_por_autor(
    p_autor_id INTEGER,
    p_percentual_desconto NUMERIC(5, 2)
)
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE livro l
    SET valoremcentavos = ROUND(valoremcentavos * (1 - p_percentual_desconto / 100.0))
    WHERE codl IN (SELECT livro_codl FROM livro_autor WHERE autor_codau = p_autor_id);
END;
$$;

COMMENT ON PROCEDURE sp_aplicar_desconto_em_livros_por_autor IS 'Aplica desconto percentual em todos os livros de um autor';
