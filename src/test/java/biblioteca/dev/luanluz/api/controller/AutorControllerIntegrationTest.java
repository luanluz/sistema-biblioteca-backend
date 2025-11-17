package biblioteca.dev.luanluz.api.controller;

import biblioteca.dev.luanluz.api.dto.request.AutorRequestDTO;
import biblioteca.dev.luanluz.api.model.Autor;
import biblioteca.dev.luanluz.api.repository.AutorRepository;
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
class AutorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AutorRepository autorRepository;

    private Autor autor1;

    @BeforeEach
    void setUp() {
        autorRepository.deleteAll();

        autor1 = new Autor();
        autor1.setNome("Machado de Assis");
        autor1 = autorRepository.save(autor1);
    }

    @Test
    @Order(1)
    void deveListarTodosAutoresComPaginacao() throws Exception {
        mockMvc.perform(get("/autor")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", is("Machado de Assis")))
                .andExpect(jsonPath("$.page").exists());
    }

    @Test
    @Order(2)
    void deveListarAutoresComOrdenacao() throws Exception {
        Autor autor2 = new Autor();
        autor2.setNome("Clarice Lispector");
        autorRepository.save(autor2);

        mockMvc.perform(get("/autor")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "nome,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nome", is("Machado de Assis")))
                .andExpect(jsonPath("$.content[1].nome", is("Clarice Lispector")));
    }

    @Test
    @Order(3)
    void deveBuscarAutorPorId() throws Exception {
        mockMvc.perform(get("/autor/{codigo}", autor1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is(autor1.getCodigo())))
                .andExpect(jsonPath("$.nome", is("Machado de Assis")));
    }

    @Test
    @Order(4)
    void deveRetornar404QuandoAutorNaoEncontrado() throws Exception {
        mockMvc.perform(get("/autor/{codigo}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail", containsString("Autor")))
                .andExpect(jsonPath("$.instance", notNullValue()));
    }

    @Test
    @Order(5)
    void deveCriarNovoAutor() throws Exception {
        AutorRequestDTO requestDTO = new AutorRequestDTO();
        requestDTO.setNome("Jorge Amado");

        mockMvc.perform(post("/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.codigo", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Jorge Amado")));
    }

    @Test
    @Order(6)
    void deveRetornar400QuandoNomeVazio() throws Exception {
        AutorRequestDTO requestDTO = new AutorRequestDTO();
        requestDTO.setNome("");

        mockMvc.perform(post("/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.invalidFields", notNullValue()));
    }

    @Test
    @Order(7)
    void deveRetornar400QuandoNomeNulo() throws Exception {
        AutorRequestDTO requestDTO = new AutorRequestDTO();
        requestDTO.setNome(null);

        mockMvc.perform(post("/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(8)
    void deveRetornar409QuandoNomeDuplicado() throws Exception {
        AutorRequestDTO requestDTO = new AutorRequestDTO();
        requestDTO.setNome("Machado de Assis");

        mockMvc.perform(post("/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.detail", containsString("nome")));
    }

    @Test
    @Order(9)
    void deveRetornar409QuandoNomeDuplicadoIgnorandoCase() throws Exception {
        AutorRequestDTO requestDTO = new AutorRequestDTO();
        requestDTO.setNome("MACHADO DE ASSIS");

        mockMvc.perform(post("/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    @Order(10)
    void deveAtualizarAutorExistente() throws Exception {
        AutorRequestDTO requestDTO = new AutorRequestDTO();
        requestDTO.setNome("Joaquim Maria Machado de Assis");

        mockMvc.perform(put("/autor/{codigo}", autor1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is(autor1.getCodigo())))
                .andExpect(jsonPath("$.nome", is("Joaquim Maria Machado de Assis")));
    }

    @Test
    @Order(11)
    void deveRetornar404AoAtualizarAutorInexistente() throws Exception {
        AutorRequestDTO requestDTO = new AutorRequestDTO();
        requestDTO.setNome("Novo Nome");

        mockMvc.perform(put("/autor/{codigo}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(12)
    void deveRetornar409AoAtualizarComNomeDuplicado() throws Exception {
        Autor autor2 = new Autor();
        autor2.setNome("Jorge Amado");
        autorRepository.save(autor2);

        AutorRequestDTO requestDTO = new AutorRequestDTO();
        requestDTO.setNome("Jorge Amado");

        mockMvc.perform(put("/autor/{codigo}", autor1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    @Order(13)
    void devePermitirAtualizarComMesmoNome() throws Exception {
        AutorRequestDTO requestDTO = new AutorRequestDTO();
        requestDTO.setNome("Machado de Assis");

        mockMvc.perform(put("/autor/{codigo}", autor1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Machado de Assis")));
    }

    @Test
    @Order(14)
    void deveDeletarAutor() throws Exception {
        mockMvc.perform(delete("/autor/{codigo}", autor1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/autor/{codigo}", autor1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(15)
    void deveRetornar404AoDeletarAutorInexistente() throws Exception {
        mockMvc.perform(delete("/autor/{codigo}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(16)
    void deveRetornar400QuandoNomeExcedeTamanhoMaximo() throws Exception {
        AutorRequestDTO requestDTO = new AutorRequestDTO();
        requestDTO.setNome("A".repeat(41));

        mockMvc.perform(post("/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(17)
    void deveRetornar400QuandoJsonInvalido() throws Exception {
        String jsonInvalido = "{ nome: 'sem aspas' }";

        mockMvc.perform(post("/autor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(18)
    void deveVerificarEstruturaDaPaginacao() throws Exception {
        mockMvc.perform(get("/autor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").exists());
    }

    @Test
    @Order(19)
    void deveVerificarEstruturaDoAutor() throws Exception {
        mockMvc.perform(get("/autor/{codigo}", autor1.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").exists())
                .andExpect(jsonPath("$.nome").exists())
                .andExpect(jsonPath("$.codigo").isNumber())
                .andExpect(jsonPath("$.nome").isString());
    }
}
