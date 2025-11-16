-- Função para auditar exclusões na tabela livro
CREATE OR REPLACE FUNCTION fn_audit_livro_delete()
    RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO auditoria_livros
        (
            operacao,
            usuario,
            data_operacao,
            dados_anteriores,
            dados_novos
        )
    VALUES
        (
            'DELETE',
            CURRENT_USER,
            CURRENT_TIMESTAMP,
            jsonb_build_object(
                    'codl', OLD.codl,
                    'titulo', OLD.titulo,
                    'editora', OLD.editora,
                    'edicao', OLD.edicao,
                    'anopublicacao', OLD.anopublicacao,
                    'valoremcentavos', OLD.valoremcentavos
                ),
            NULL
        );

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Criação do trigger
CREATE TRIGGER trg_audit_livro_delete
    AFTER DELETE
    ON livro
    FOR EACH ROW
EXECUTE FUNCTION fn_audit_livro_delete();

-- Comentários
COMMENT ON FUNCTION fn_audit_livro_delete() IS 'Função para auditar exclusões na tabela livro';
COMMENT ON TRIGGER trg_audit_livro_delete ON livro IS 'Trigger que registra exclusões de livros na auditoria';
