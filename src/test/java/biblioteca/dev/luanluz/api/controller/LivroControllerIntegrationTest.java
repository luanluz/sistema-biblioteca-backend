package biblioteca.dev.luanluz.api.controller;

import biblioteca.dev.luanluz.api.dto.request.LivroRequestDTO;
import biblioteca.dev.luanluz.api.model.Assunto;
import biblioteca.dev.luanluz.api.model.Autor;
import biblioteca.dev.luanluz.api.model.Livro;
import biblioteca.dev.luanluz.api.repository.AssuntoRepository;
import biblioteca.dev.luanluz.api.repository.AutorRepository;
import biblioteca.dev.luanluz.api.repository.LivroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LivroControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private AssuntoRepository assuntoRepository;

    private Livro livro1;
    private Autor autor1;
    private Autor autor2;
    private Assunto assunto1;
    private Assunto assunto2;

    @BeforeEach
    void setUp() {
        livroRepository.deleteAll();
        autorRepository.deleteAll();
        assuntoRepository.deleteAll();

        autor1 = new Autor();
        autor1.setNome("Machado de Assis");
        autor1 = autorRepository.save(autor1);

        autor2 = new Autor();
        autor2.setNome("Jorge Amado");
        autor2 = autorRepository.save(autor2);

        assunto1 = new Assunto();
        assunto1.setDescricao("Ficção");
        assunto1 = assuntoRepository.save(assunto1);

        assunto2 = new Assunto();
        assunto2.setDescricao("Romance");
        assunto2 = assuntoRepository.save(assunto2);

        livro1 = new Livro();
        livro1.setTitulo("Dom Casmurro");
        livro1.setEditora("Editora Nacional");
        livro1.setEdicao(1);
        livro1.setAnoPublicacao("1899");
        livro1.setValorEmCentavos(5000);
        livro1.setAutores(Set.of(autor1));
        livro1.setAssuntos(Set.of(assunto1));
        livro1 = livroRepository.save(livro1);
    }

    @Test
    @Order(1)
    void deveListarTodosLivrosComPaginacao() throws Exception {
        mockMvc.perform(get("/livro")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].titulo", is("Dom Casmurro")))
                .andExpect(jsonPath("$.page").exists());
    }

    @Test
    @Order(2)
    void deveListarLivrosComOrdenacao() throws Exception {
        Livro livro2 = new Livro();
        livro2.setTitulo("Capitães da Areia");
        livro2.setEditora("Companhia das Letras");
        livro2.setEdicao(1);
        livro2.setAnoPublicacao("1937");
        livro2.setValorEmCentavos(4500);
        livro2.setAutores(Set.of(autor2));
        livro2.setAssuntos(Set.of(assunto2));
        livroRepository.save(livro2);

        mockMvc.perform(get("/livro")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "titulo,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].titulo", is("Dom Casmurro")))
                .andExpect(jsonPath("$.content[1].titulo", is("Capitães da Areia")));
    }

    @Test
    @Order(3)
    void deveBuscarLivroPorId() throws Exception {
        mockMvc.perform(get("/livro/{codigo}", livro1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is(livro1.getCodigo())))
                .andExpect(jsonPath("$.titulo", is("Dom Casmurro")))
                .andExpect(jsonPath("$.editora", is("Editora Nacional")))
                .andExpect(jsonPath("$.edicao", is(1)))
                .andExpect(jsonPath("$.anoPublicacao", is("1899")))
                .andExpect(jsonPath("$.valorEmCentavos", is(5000)));
    }

    @Test
    @Order(4)
    void deveRetornar404QuandoLivroNaoEncontrado() throws Exception {
        mockMvc.perform(get("/livro/{codigo}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail", containsString("Livro")))
                .andExpect(jsonPath("$.instance", notNullValue()));
    }

    @Test
    @Order(5)
    void deveCriarNovoLivro() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Memórias Póstumas de Brás Cubas")
                .editora("Editora Nacional")
                .edicao(1)
                .anoPublicacao("1881")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.codigo", notNullValue()))
                .andExpect(jsonPath("$.titulo", is("Memórias Póstumas de Brás Cubas")));
    }

    @Test
    @Order(6)
    void deveRetornar400QuandoTituloVazio() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("")
                .editora("Editora Nacional")
                .edicao(1)
                .anoPublicacao("1881")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.invalidFields", notNullValue()));
    }

    @Test
    @Order(7)
    void deveRetornar400QuandoTituloNulo() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo(null)
                .editora("Editora Nacional")
                .edicao(1)
                .anoPublicacao("1881")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(8)
    void deveRetornar400QuandoEditoraVazia() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Novo Livro")
                .editora("")
                .edicao(1)
                .anoPublicacao("1881")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(9)
    void deveRetornar400QuandoEdicaoNula() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Novo Livro")
                .editora("Editora Nacional")
                .edicao(null)
                .anoPublicacao("1881")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(10)
    void deveRetornar400QuandoEdicaoNegativa() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Novo Livro")
                .editora("Editora Nacional")
                .edicao(-1)
                .anoPublicacao("1881")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(11)
    void deveRetornar400QuandoAnoPublicacaoInvalido() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Novo Livro")
                .editora("Editora Nacional")
                .edicao(1)
                .anoPublicacao("20244")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(12)
    void deveRetornar400QuandoValorNegativo() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Novo Livro")
                .editora("Editora Nacional")
                .edicao(1)
                .anoPublicacao("2024")
                .valorEmCentavos(-100)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(13)
    void deveRetornar400QuandoAutoresVazio() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Novo Livro")
                .editora("Editora Nacional")
                .edicao(1)
                .anoPublicacao("2024")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of())
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(14)
    void deveRetornar400QuandoAssuntosVazio() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Novo Livro")
                .editora("Editora Nacional")
                .edicao(1)
                .anoPublicacao("2024")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of())
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(15)
    void deveRetornar409QuandoTituloDuplicado() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Dom Casmurro")
                .editora("Editora Nacional")
                .edicao(2)
                .anoPublicacao("1900")
                .valorEmCentavos(5500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.detail", containsString("título")));
    }

    @Test
    @Order(16)
    void deveRetornar409QuandoTituloDuplicadoIgnorandoCase() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("DOM CASMURRO")
                .editora("Editora Nacional")
                .edicao(2)
                .anoPublicacao("1900")
                .valorEmCentavos(5500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    @Order(17)
    void deveAtualizarLivroExistente() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Dom Casmurro - Edição Especial")
                .editora("Editora Nova")
                .edicao(2)
                .anoPublicacao("1900")
                .valorEmCentavos(6000)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(put("/livro/{codigo}", livro1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is(livro1.getCodigo())))
                .andExpect(jsonPath("$.titulo", is("Dom Casmurro - Edição Especial")))
                .andExpect(jsonPath("$.editora", is("Editora Nova")));
    }

    @Test
    @Order(18)
    void deveRetornar404AoAtualizarLivroInexistente() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Livro Inexistente")
                .editora("Editora Nacional")
                .edicao(1)
                .anoPublicacao("2024")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(put("/livro/{codigo}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(19)
    void deveRetornar409AoAtualizarComTituloDuplicado() throws Exception {
        Livro livro2 = new Livro();
        livro2.setTitulo("Quincas Borba");
        livro2.setEditora("Editora Nacional");
        livro2.setEdicao(1);
        livro2.setAnoPublicacao("1891");
        livro2.setValorEmCentavos(4800);
        livro2.setAutores(Set.of(autor1));
        livro2.setAssuntos(Set.of(assunto1));
        livroRepository.save(livro2);

        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Quincas Borba")
                .editora("Editora Nacional")
                .edicao(1)
                .anoPublicacao("1900")
                .valorEmCentavos(5000)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(put("/livro/{codigo}", livro1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    @Order(20)
    void devePermitirAtualizarComMesmoTitulo() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Dom Casmurro")
                .editora("Editora Atualizada")
                .edicao(2)
                .anoPublicacao("1900")
                .valorEmCentavos(5500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(put("/livro/{codigo}", livro1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Dom Casmurro")))
                .andExpect(jsonPath("$.editora", is("Editora Atualizada")));
    }

    @Test
    @Order(21)
    void deveDeletarLivro() throws Exception {
        mockMvc.perform(delete("/livro/{codigo}", livro1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/livro/{codigo}", livro1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(22)
    void deveRetornar404AoDeletarLivroInexistente() throws Exception {
        mockMvc.perform(delete("/livro/{codigo}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(23)
    void deveRetornar400QuandoTituloExcedeTamanhoMaximo() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("A".repeat(41))
                .editora("Editora Nacional")
                .edicao(1)
                .anoPublicacao("2024")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(24)
    void deveRetornar400QuandoEditoraExcedeTamanhoMaximo() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Novo Livro")
                .editora("A".repeat(41))
                .edicao(1)
                .anoPublicacao("2024")
                .valorEmCentavos(4500)
                .autoresCodigos(Set.of(autor1.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(25)
    void deveRetornar400QuandoJsonInvalido() throws Exception {
        String jsonInvalido = "{ titulo: 'sem aspas' }";

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(26)
    void deveVerificarEstruturaDaPaginacao() throws Exception {
        mockMvc.perform(get("/livro")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").exists());
    }

    @Test
    @Order(27)
    void deveVerificarEstruturaDoLivro() throws Exception {
        mockMvc.perform(get("/livro/{codigo}", livro1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").exists())
                .andExpect(jsonPath("$.titulo").exists())
                .andExpect(jsonPath("$.editora").exists())
                .andExpect(jsonPath("$.edicao").exists())
                .andExpect(jsonPath("$.anoPublicacao").exists())
                .andExpect(jsonPath("$.valorEmCentavos").exists())
                .andExpect(jsonPath("$.codigo").isNumber())
                .andExpect(jsonPath("$.titulo").isString());
    }

    @Test
    @Order(28)
    void deveCriarLivroComMultiplosAutoresEAssuntos() throws Exception {
        LivroRequestDTO requestDTO = LivroRequestDTO.builder()
                .titulo("Livro Colaborativo")
                .editora("Editora Colaborativa")
                .edicao(1)
                .anoPublicacao("2024")
                .valorEmCentavos(7000)
                .autoresCodigos(Set.of(autor1.getCodigo(), autor2.getCodigo()))
                .assuntosCodigos(Set.of(assunto1.getCodigo(), assunto2.getCodigo()))
                .build();

        mockMvc.perform(post("/livro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo", is("Livro Colaborativo")));
    }
}
