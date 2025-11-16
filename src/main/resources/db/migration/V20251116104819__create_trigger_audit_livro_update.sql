-- Função para auditar alterações na tabela livro, registrando dados antes e depois
CREATE OR REPLACE FUNCTION fn_audit_livro_update()
    RETURNS TRIGGER AS
$$
BEGIN
    IF (ROW (NEW.*) IS DISTINCT FROM ROW (OLD.*)) THEN
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
             'UPDATE',
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
                 jsonb_build_object(
                         'codl', NEW.codl,
                         'titulo', NEW.titulo,
                         'editora', NEW.editora,
                         'edicao', NEW.edicao,
                         'anopublicacao', NEW.anopublicacao,
                         'valoremcentavos', NEW.valoremcentavos
                 )
            );
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Criação do trigger
CREATE TRIGGER trg_audit_livro_update
    AFTER UPDATE
    ON livro
    FOR EACH ROW
EXECUTE FUNCTION fn_audit_livro_update();

-- Comentários
COMMENT ON FUNCTION fn_audit_livro_update() IS 'Função para auditar alterações na tabela livro, registrando dados antes e depois';
COMMENT ON TRIGGER trg_audit_livro_update ON livro IS 'Trigger que registra alterações de livros na auditoria';
