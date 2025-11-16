INSERT INTO autor (nome)
VALUES ('Machado de Assis'),
       ('José Saramago'),
       ('Gabriel García Márquez'),
       ('George Orwell'),
       ('J.K. Rowling'),
       ('Stephen King'),
       ('Agatha Christie'),
       ('Isaac Asimov'),
       ('Arthur C. Clarke'),
       ('Philip K. Dick'),
       ('Robert C. Martin'),
       ('Martin Fowler'),
       ('Eric Evans'),
       ('Kent Beck'),
       ('Robert Louis Stevenson'),
       ('Jane Austen'),
       ('Charles Dickens'),
       ('Mark Twain'),
       ('Ernest Hemingway'),
       ('F. Scott Fitzgerald'),
       ('J.R.R. Tolkien'),
       ('C.S. Lewis'),
       ('Paulo Coelho'),
       ('Clarice Lispector'),
       ('Jorge Amado');

INSERT INTO assunto (descricao)
VALUES ('Ficção'),
       ('Romance'),
       ('Suspense'),
       ('Terror'),
       ('Fantasia'),
       ('Ficção Científica'),
       ('Tecnologia'),
       ('Programação'),
       ('Arquitetura Software'),
       ('Literatura Clássica'),
       ('Literatura Nacional'),
       ('Literatura Inglesa'),
       ('Policial'),
       ('Aventura'),
       ('Drama'),
       ('Autoajuda'),
       ('Filosofia'),
       ('História'),
       ('Biografia'),
       ('Poesia');

-- Literatura Nacional
INSERT INTO livro (titulo, editora, edicao, anopublicacao, valoremcentavos)
VALUES ('Dom Casmurro', 'Editora Globo', 3, '2008', 3500),
       ('Memórias Póstumas de Brás Cubas', 'Ática', 2, '2010', 3200),
       ('Quincas Borba', 'Nova Fronteira', 1, '2015', 4000),
       ('A Hora da Estrela', 'Rocco', 1, '1977', 2800),
       ('Capitães da Areia', 'Companhia das Letras', 5, '2008', 3800);

-- Literatura Estrangeira Clássica
INSERT INTO livro (titulo, editora, edicao, anopublicacao, valoremcentavos)
VALUES ('Cem Anos de Solidão', 'Record', 1, '1967', 5500),
       ('1984', 'Companhia das Letras', 1, '1949', 4200),
       ('A Revolução dos Bichos', 'Globo', 2, '2007', 3000),
       ('Orgulho e Preconceito', 'Penguin', 1, '1813', 3500),
       ('Grandes Esperanças', 'Martin Claret', 1, '1861', 4500);

-- Fantasia
INSERT INTO livro (titulo, editora, edicao, anopublicacao, valoremcentavos)
VALUES ('Harry Potter - Pedra Filosofal', 'Rocco', 1, '1997', 4500),
       ('Harry Potter - Câmara Secreta', 'Rocco', 1, '1998', 4500),
       ('O Senhor dos Anéis', 'Martins Fontes', 3, '1954', 8900),
       ('As Crônicas de Nárnia', 'Martins Fontes', 1, '1950', 6500),
       ('O Hobbit', 'Martins Fontes', 2, '1937', 5200);

-- Terror e Suspense
INSERT INTO livro (titulo, editora, edicao, anopublicacao, valoremcentavos)
VALUES ('O Iluminado', 'Suma', 1, '1977', 4800),
       ('It - A Coisa', 'Suma', 1, '1986', 6500),
       ('Carrie', 'Suma', 1, '1974', 3900),
       ('Assassinato no Expresso do Oriente', 'Harper Collins', 1, '1934', 3500),
       ('E Não Sobrou Nenhum', 'Globo', 2, '1939', 4200);

-- Ficção Científica
INSERT INTO livro (titulo, editora, edicao, anopublicacao, valoremcentavos)
VALUES ('Fundação', 'Aleph', 1, '1951', 4500),
       ('Eu, Robô', 'Aleph', 1, '1950', 4000),
       ('2001: Uma Odisseia no Espaço', 'Aleph', 1, '1968', 4200),
       ('O Fim da Eternidade', 'Aleph', 1, '1955', 3800),
       ('Androides Sonham com Ovelhas?', 'Aleph', 1, '1968', 4500);

-- Programação e Tecnologia
INSERT INTO livro (titulo, editora, edicao, anopublicacao, valoremcentavos)
VALUES ('Clean Code', 'Alta Books', 1, '2008', 8900),
       ('Refactoring', 'Novatec', 2, '2018', 7500),
       ('Domain-Driven Design', 'Alta Books', 1, '2003', 9500),
       ('Test Driven Development', 'Bookman', 1, '2002', 6800),
       ('Design Patterns', 'Bookman', 1, '1994', 8500);

-- Literatura Americana
INSERT INTO livro (titulo, editora, edicao, anopublicacao, valoremcentavos)
VALUES ('O Velho e o Mar', 'Bertrand Brasil', 1, '1952', 3200),
       ('O Grande Gatsby', 'Intrínseca', 2, '1925', 3800),
       ('As Aventuras de Tom Sawyer', 'Zahar', 1, '1876', 3000),
       ('A Ilha do Tesouro', 'Zahar', 1, '1883', 2800),
       ('O Médico e o Monstro', 'Zahar', 1, '1886', 2500);

-- Autoajuda e Filosofia
INSERT INTO livro (titulo, editora, edicao, anopublicacao, valoremcentavos)
VALUES ('O Alquimista', 'Planeta', 1, '1988', 2500),
       ('Brida', 'Planeta', 1, '1990', 2800),
       ('Veronika Decide Morrer', 'Planeta', 1, '1998', 3000),
       ('O Cortiço', 'Ática', 2, '1890', 2200),
       ('A Moreninha', 'Ática', 1, '1844', 1800);

-- Livros com preços variados para testar faixas
INSERT INTO livro (titulo, editora, edicao, anopublicacao, valoremcentavos)
VALUES ('Livro Gratuito Digital', 'Editora Online', 1, '2024', 0),
       ('Livro Econômico', 'Editora Popular', 1, '2023', 1500),
       ('Livro Médio', 'Editora Normal', 1, '2022', 3500),
       ('Livro Premium', 'Editora Luxo', 1, '2021', 12000),
       ('Livro Colecionador', 'Editora Rara', 1, '2020', 25000),
       ('Edição Especial Limitada', 'Editora Elite', 1, '2019', 35000);

-- Literatura Nacional
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES
-- Machado de Assis
(1, 1),
(2, 1),
(3, 1),
-- Clarice Lispector
(4, 24),
-- Jorge Amado
(5, 25);

-- Literatura Estrangeira
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES
-- Gabriel García Márquez
(6, 3),
-- George Orwell
(7, 4),
(8, 4),
-- Jane Austen
(9, 16),
-- Charles Dickens
(10, 17);

-- Fantasia
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES
-- J.K. Rowling
(11, 5),
(12, 5),
-- J.R.R. Tolkien
(13, 21),
(15, 21),
-- C.S. Lewis
(14, 22);

-- Terror e Suspense
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES
-- Stephen King
(16, 6),
(17, 6),
(18, 6),
-- Agatha Christie
(19, 7),
(20, 7);

-- Ficção Científica
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES
-- Isaac Asimov
(21, 8),
(22, 8),
(24, 8),
-- Arthur C. Clarke
(23, 9),
-- Philip K. Dick
(25, 10);

-- Programação
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES
-- Robert C. Martin
(26, 11),
-- Martin Fowler
(27, 12),
-- Eric Evans
(28, 13),
-- Kent Beck
(29, 14);

-- Design Patterns tem múltiplos autores
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES (30, 11),
       (30, 12);

-- Literatura Americana
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES
-- Ernest Hemingway
(31, 19),
-- F. Scott Fitzgerald
(32, 20),
-- Mark Twain
(33, 18),
-- Robert Louis Stevenson
(34, 15),
(35, 15);

-- Paulo Coelho
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES (36, 23),
       (37, 23),
       (38, 23);

-- Autores brasileiros clássicos
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES (39, 1),
       (40, 1);

-- Livros diversos (alguns sem autor definido ainda)
INSERT INTO livro_autor (livro_codl, autor_codau)
VALUES (41, 11),
       (42, 12),
       (43, 13),
       (44, 14),
       (45, 8),
       (46, 21);

-- Literatura Nacional
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES (1, 2),
       (1, 10),
       (1, 11), -- Dom Casmurro: Romance, Clássica, Brasileira
       (2, 1),
       (2, 10),
       (2, 11), -- Memórias Póstumas: Ficção, Clássica, Brasileira
       (3, 2),
       (3, 11), -- Quincas Borba: Romance, Brasileira
       (4, 1),
       (4, 15),
       (4, 11), -- A Hora da Estrela: Ficção, Drama, Brasileira
       (5, 14),
       (5, 15),
       (5, 11);
-- Capitães da Areia: Aventura, Drama, Brasileira

-- Literatura Estrangeira
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES (6, 1),
       (6, 2),
       (6, 10), -- Cem Anos: Ficção, Romance, Clássica
       (7, 1),
       (7, 6),
       (7, 15), -- 1984: Ficção, FC, Drama
       (8, 1),
       (8, 17), -- Revolução dos Bichos: Ficção, Filosofia
       (9, 2),
       (9, 10),
       (9, 12), -- Orgulho e Preconceito: Romance, Clássica, Inglesa
       (10, 2),
       (10, 10),
       (10, 12);
-- Grandes Esperanças: Romance, Clássica, Inglesa

-- Fantasia
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES (11, 5),
       (11, 14), -- HP1: Fantasia, Aventura
       (12, 5),
       (12, 14), -- HP2: Fantasia, Aventura
       (13, 5),
       (13, 14), -- LOTR: Fantasia, Aventura
       (14, 5),
       (14, 14), -- Nárnia: Fantasia, Aventura
       (15, 5),
       (15, 14);
-- Hobbit: Fantasia, Aventura

-- Terror e Suspense
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES (16, 4),
       (16, 3), -- Iluminado: Terror, Suspense
       (17, 4),
       (17, 3), -- It: Terror, Suspense
       (18, 4), -- Carrie: Terror
       (19, 13),
       (19, 3), -- Assassinato Expresso: Policial, Suspense
       (20, 13),
       (20, 3);
-- E Não Sobrou: Policial, Suspense

-- Ficção Científica
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES (21, 6),
       (21, 1),  -- Fundação: FC, Ficção
       (22, 6),
       (22, 1),  -- Eu Robô: FC, Ficção
       (23, 6),
       (23, 14), -- 2001: FC, Aventura
       (24, 6),
       (24, 1),  -- Fim da Eternidade: FC, Ficção
       (25, 6),
       (25, 1);
-- Androides: FC, Ficção

-- Programação
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES (26, 8),
       (26, 7), -- Clean Code: Programação, Tecnologia
       (27, 8),
       (27, 7), -- Refactoring: Programação, Tecnologia
       (28, 9),
       (28, 7), -- DDD: Arquitetura, Tecnologia
       (29, 8),
       (29, 7), -- TDD: Programação, Tecnologia
       (30, 8),
       (30, 9),
       (30, 7);
-- Design Patterns: Programação, Arquitetura, Tecnologia

-- Literatura Americana
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES (31, 1),
       (31, 15), -- Velho e Mar: Ficção, Drama
       (32, 2),
       (32, 10), -- Grande Gatsby: Romance, Clássica
       (33, 14),
       (33, 1),  -- Tom Sawyer: Aventura, Ficção
       (34, 14),
       (34, 1),  -- Ilha Tesouro: Aventura, Ficção
       (35, 4),
       (35, 6);
-- Médico Monstro: Terror, FC

-- Autoajuda e Filosofia
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES (36, 16),
       (36, 17), -- Alquimista: Autoajuda, Filosofia
       (37, 16),
       (37, 1),  -- Brida: Autoajuda, Ficção
       (38, 16),
       (38, 15), -- Veronika: Autoajuda, Drama
       (39, 15),
       (39, 11), -- Cortiço: Drama, Brasileira
       (40, 2),
       (40, 11);
-- Moreninha: Romance, Brasileira

-- Livros diversos
INSERT INTO livro_assunto (livro_codl, assunto_codas)
VALUES (41, 7),
       (41, 8), -- Gratuito: Tecnologia, Programação
       (42, 8), -- Econômico: Programação
       (43, 9), -- Médio: Arquitetura
       (44, 7), -- Premium: Tecnologia
       (45, 6), -- Colecionador: FC
       (46, 5),
       (46, 14); -- Especial: Fantasia, Aventura
