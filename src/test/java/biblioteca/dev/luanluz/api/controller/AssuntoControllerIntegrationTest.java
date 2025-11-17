package biblioteca.dev.luanluz.api.controller;

import biblioteca.dev.luanluz.api.dto.request.AssuntoRequestDTO;
import biblioteca.dev.luanluz.api.model.Assunto;
import biblioteca.dev.luanluz.api.repository.AssuntoRepository;
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
class AssuntoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AssuntoRepository assuntoRepository;

    private Assunto assunto1;

    @BeforeEach
    void setUp() {
        assuntoRepository.deleteAll();

        assunto1 = new Assunto();
        assunto1.setDescricao("Ficção Científica");
        assunto1 = assuntoRepository.save(assunto1);
    }

    @Test
    @Order(1)
    void deveListarTodosAssuntosComPaginacao() throws Exception {
        mockMvc.perform(get("/assunto")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].descricao", is("Ficção Científica")))
                .andExpect(jsonPath("$.page").exists());
    }

    @Test
    @Order(2)
    void deveListarAssuntosComOrdenacao() throws Exception {
        Assunto assunto2 = new Assunto();
        assunto2.setDescricao("Romance");
        assuntoRepository.save(assunto2);

        mockMvc.perform(get("/assunto")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "descricao,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].descricao", is("Romance")))
                .andExpect(jsonPath("$.content[1].descricao", is("Ficção Científica")));
    }

    @Test
    @Order(3)
    void deveBuscarAssuntoPorId() throws Exception {
        mockMvc.perform(get("/assunto/{codigo}", assunto1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is(assunto1.getCodigo())))
                .andExpect(jsonPath("$.descricao", is("Ficção Científica")));
    }

    @Test
    @Order(4)
    void deveRetornar404QuandoAssuntoNaoEncontrado() throws Exception {
        mockMvc.perform(get("/assunto/{codigo}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail", containsString("Assunto")))
                .andExpect(jsonPath("$.instance", notNullValue()));
    }

    @Test
    @Order(5)
    void deveCriarNovoAssunto() throws Exception {
        AssuntoRequestDTO requestDTO = new AssuntoRequestDTO();
        requestDTO.setDescricao("Terror");

        mockMvc.perform(post("/assunto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.codigo", notNullValue()))
                .andExpect(jsonPath("$.descricao", is("Terror")));
    }

    @Test
    @Order(6)
    void deveRetornar400QuandoDescricaoVazia() throws Exception {
        AssuntoRequestDTO requestDTO = new AssuntoRequestDTO();
        requestDTO.setDescricao("");

        mockMvc.perform(post("/assunto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.invalidFields", notNullValue()));
    }

    @Test
    @Order(7)
    void deveRetornar400QuandoDescricaoNula() throws Exception {
        AssuntoRequestDTO requestDTO = new AssuntoRequestDTO();
        requestDTO.setDescricao(null);

        mockMvc.perform(post("/assunto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(8)
    void deveRetornar409QuandoDescricaoDuplicada() throws Exception {
        AssuntoRequestDTO requestDTO = new AssuntoRequestDTO();
        requestDTO.setDescricao("Ficção Científica");

        mockMvc.perform(post("/assunto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.detail", containsString("descrição")));
    }

    @Test
    @Order(9)
    void deveRetornar409QuandoDescricaoDuplicadaIgnorandoCase() throws Exception {
        AssuntoRequestDTO requestDTO = new AssuntoRequestDTO();
        requestDTO.setDescricao("FICÇÃO CIENTÍFICA");

        mockMvc.perform(post("/assunto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    @Order(10)
    void deveAtualizarAssuntoExistente() throws Exception {
        AssuntoRequestDTO requestDTO = new AssuntoRequestDTO();
        requestDTO.setDescricao("Ficção");

        mockMvc.perform(put("/assunto/{codigo}", assunto1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is(assunto1.getCodigo())))
                .andExpect(jsonPath("$.descricao", is("Ficção")));
    }

    @Test
    @Order(11)
    void deveRetornar404AoAtualizarAssuntoInexistente() throws Exception {
        AssuntoRequestDTO requestDTO = new AssuntoRequestDTO();
        requestDTO.setDescricao("Nova Descrição");

        mockMvc.perform(put("/assunto/{codigo}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(12)
    void deveRetornar409AoAtualizarComDescricaoDuplicada() throws Exception {
        Assunto assunto2 = new Assunto();
        assunto2.setDescricao("Romance");
        assuntoRepository.save(assunto2);

        AssuntoRequestDTO requestDTO = new AssuntoRequestDTO();
        requestDTO.setDescricao("Romance");

        mockMvc.perform(put("/assunto/{codigo}", assunto1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    @Order(13)
    void devePermitirAtualizarComMesmaDescricao() throws Exception {
        AssuntoRequestDTO requestDTO = new AssuntoRequestDTO();
        requestDTO.setDescricao("Ficção Científica");

        mockMvc.perform(put("/assunto/{codigo}", assunto1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao", is("Ficção Científica")));
    }

    @Test
    @Order(14)
    void deveDeletarAssunto() throws Exception {
        mockMvc.perform(delete("/assunto/{codigo}", assunto1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/assunto/{codigo}", assunto1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(15)
    void deveRetornar404AoDeletarAssuntoInexistente() throws Exception {
        mockMvc.perform(delete("/assunto/{codigo}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(16)
    void deveRetornar400QuandoDescricaoExcedeTamanhoMaximo() throws Exception {
        AssuntoRequestDTO requestDTO = new AssuntoRequestDTO();
        requestDTO.setDescricao("A".repeat(21));

        mockMvc.perform(post("/assunto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(17)
    void deveRetornar400QuandoJsonInvalido() throws Exception {
        String jsonInvalido = "{ descricao: 'sem aspas' }";

        mockMvc.perform(post("/assunto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(18)
    void deveVerificarEstruturaDaPaginacao() throws Exception {
        mockMvc.perform(get("/assunto")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").exists());
    }

    @Test
    @Order(19)
    void deveVerificarEstruturaDoAssunto() throws Exception {
        mockMvc.perform(get("/assunto/{codigo}", assunto1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").exists())
                .andExpect(jsonPath("$.descricao").exists())
                .andExpect(jsonPath("$.codigo").isNumber())
                .andExpect(jsonPath("$.descricao").isString());
    }
}
