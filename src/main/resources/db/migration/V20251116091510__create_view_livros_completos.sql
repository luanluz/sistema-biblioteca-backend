-- View com informações completas dos livros incluindo autores e assuntos concatenados
CREATE OR REPLACE VIEW vw_livros_completos AS
SELECT l.codl,
       l.titulo,
       l.editora,
       l.edicao,
       l.anopublicacao,
       l.valoremcentavos,
       CONCAT('R$ ', TO_CHAR(l.valoremcentavos / 100.0, 'FM999999990.00')) AS valor_formatado,
       STRING_AGG(DISTINCT a.nome, ', ' ORDER BY a.nome)                   AS autores,
       STRING_AGG(DISTINCT ass.descricao, ', ' ORDER BY ass.descricao)     AS assuntos,
       COUNT(DISTINCT la.autor_codau)                                      AS quantidade_autores,
       COUNT(DISTINCT lassunto.assunto_codas)                              AS quantidade_assuntos
FROM livro l
         LEFT JOIN livro_autor la ON l.codl = la.livro_codl
         LEFT JOIN autor a ON la.autor_codau = a.codau
         LEFT JOIN livro_assunto lassunto ON l.codl = lassunto.livro_codl
         LEFT JOIN assunto ass ON lassunto.assunto_codas = ass.codas
GROUP BY l.codl, l.titulo, l.editora, l.edicao, l.anopublicacao, l.valoremcentavos;

COMMENT ON VIEW vw_livros_completos IS 'View com informações completas dos livros incluindo autores e assuntos concatenados';
