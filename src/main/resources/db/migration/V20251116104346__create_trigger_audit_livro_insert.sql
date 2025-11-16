-- Função para auditar inserções na tabela livro
CREATE OR REPLACE FUNCTION fn_audit_livro_insert()
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
            'INSERT',
            CURRENT_USER,
            CURRENT_TIMESTAMP,
            NULL,
            jsonb_build_object(
                    'codl', NEW.codl,
                    'titulo', NEW.titulo,
                    'editora', NEW.editora,
                    'edicao', NEW.edicao,
                    'anopublicacao', NEW.anopublicacao,
                    'valoremcentavos', NEW.valoremcentavos
            )
        );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Criação do trigger
CREATE TRIGGER trg_audit_livro_insert
    AFTER INSERT
    ON livro
    FOR EACH ROW
EXECUTE FUNCTION fn_audit_livro_insert();

-- Comentários
COMMENT ON FUNCTION fn_audit_livro_insert() IS 'Função para auditar inserções na tabela livro';
COMMENT ON TRIGGER trg_audit_livro_insert ON livro IS 'Trigger que registra inserções de livros na auditoria';
