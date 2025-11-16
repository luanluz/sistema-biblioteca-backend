-- View com informações estatísticas de livros agrupados por autor
CREATE OR REPLACE VIEW vw_relatorio_livros_por_autor AS
SELECT a.codau,
       a.nome                                                            AS autor,
       COUNT(DISTINCT la.livro_codl)                                     AS quantidade_livros,
       CONCAT(TO_CHAR(AVG(l.valoremcentavos) / 100.0, 'FM999999990.00')) AS preco_medio,
       CONCAT(TO_CHAR(MIN(l.valoremcentavos) / 100.0, 'FM999999990.00')) AS preco_minimo,
       CONCAT(TO_CHAR(MAX(l.valoremcentavos) / 100.0, 'FM999999990.00')) AS preco_maximo,
       CONCAT(TO_CHAR(SUM(l.valoremcentavos) / 100.0, 'FM999999990.00')) AS valor_total
FROM autor a
         INNER JOIN livro_autor la ON a.codau = la.autor_codau
         INNER JOIN livro l ON la.livro_codl = l.codl
GROUP BY a.codau, a.nome
ORDER BY quantidade_livros DESC, a.nome;

COMMENT ON VIEW vw_relatorio_livros_por_autor IS 'View com informações estatísticas de livros agrupados por autor';
