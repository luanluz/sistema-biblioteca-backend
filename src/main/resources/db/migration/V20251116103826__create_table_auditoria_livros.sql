-- Tabela de auditoria de livros
CREATE TABLE auditoria_livros
(
    id               SERIAL PRIMARY KEY NOT NULL,
    operacao         VARCHAR(10)  NOT NULL CHECK (operacao IN ('INSERT', 'UPDATE', 'DELETE')),
    usuario          VARCHAR(100) NOT NULL,
    data_operacao    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dados_anteriores JSONB,
    dados_novos      JSONB
);

-- Índices
CREATE INDEX idx_auditoria_livros_operacao ON auditoria_livros (operacao);
CREATE INDEX idx_auditoria_livros_data ON auditoria_livros (data_operacao);
CREATE INDEX idx_auditoria_livros_usuario ON auditoria_livros (usuario);

-- Comentários
COMMENT ON TABLE auditoria_livros IS 'Tabela de auditoria para registrar todas as operações na tabela livro';
COMMENT ON COLUMN auditoria_livros.id IS 'Identificador único do registro de auditoria';
COMMENT ON COLUMN auditoria_livros.operacao IS 'Tipo de operação: INSERT, UPDATE ou DELETE';
COMMENT ON COLUMN auditoria_livros.usuario IS 'Usuário que realizou a operação';
COMMENT ON COLUMN auditoria_livros.data_operacao IS 'Data e hora da operação';
COMMENT ON COLUMN auditoria_livros.dados_anteriores IS 'Dados do registro antes da operação (JSON)';
COMMENT ON COLUMN auditoria_livros.dados_novos IS 'Dados do registro após a operação (JSON)';
