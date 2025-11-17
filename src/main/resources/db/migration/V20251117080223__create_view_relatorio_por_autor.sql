-- View de relatório agrupado por autor
CREATE OR REPLACE VIEW vw_relatorio_por_autor AS
SELECT a.codau,
       a.nome                                                          AS nome_autor,
       COUNT(DISTINCT l.codl)                                          AS quantidade_livros,
       STRING_AGG(DISTINCT l.titulo, '; ' ORDER BY l.titulo)           AS titulos,
       STRING_AGG(DISTINCT l.editora, '; ' ORDER BY l.editora)         AS editoras,
       STRING_AGG(DISTINCT ass.descricao, '; ' ORDER BY ass.descricao) AS assuntos,
       ROUND(SUM(l.valoremcentavos) / 100.0, 2)                        AS valor_total_reais,
       MIN(l.anopublicacao)                                            AS ano_publicacao_mais_antigo,
       MAX(l.anopublicacao)                                            AS ano_publicacao_mais_recente
FROM autor a
         LEFT JOIN livro_autor la ON a.codau = la.autor_codau
         LEFT JOIN livro l ON la.livro_codl = l.codl
         LEFT JOIN livro_assunto las ON l.codl = las.livro_codl
         LEFT JOIN assunto ass ON las.assunto_codas = ass.codas
GROUP BY a.codau, a.nome
ORDER BY a.nome;

COMMENT ON VIEW vw_relatorio_por_autor IS 'Relatório de livros agrupados por autor';
