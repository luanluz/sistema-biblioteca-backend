package biblioteca.dev.luanluz.api.controller;

import biblioteca.dev.luanluz.api.dto.request.LivroRequestDTO;
import biblioteca.dev.luanluz.api.dto.response.LivroPaginacaoDTO;
import biblioteca.dev.luanluz.api.dto.response.LivroResponseDTO;
import biblioteca.dev.luanluz.api.exception.error.ErrorDetail;
import biblioteca.dev.luanluz.api.exception.error.ValidationErrorDetail;
import biblioteca.dev.luanluz.api.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static biblioteca.dev.luanluz.api.util.PageRequestHelper.getPageRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/livro")
@Tag(name = "Livros", description = "Endpoints para gerenciamento de livros")
public class LivroController {

    private final LivroService livroService;

    @Operation(
            summary = "Listar todos os livros",
            description = "Retorna uma lista paginada de todos os livros cadastrados. " +
                    "Suporta paginação e ordenação através dos parâmetros page, size e sort. " +
                    "Os livros são retornados com seus autores e assuntos associados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de livros retornada com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LivroPaginacaoDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro Interno do Servidor",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<Page<LivroResponseDTO>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(
                    description = "Parâmetros de ordenação no formato: propriedade,direção",
                    example = "titulo,asc",
                    schema = @Schema(type = "string")
            )
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "titulo", direction = Sort.Direction.ASC)
            }) Sort sort
    ) {
        var pageRequest = getPageRequest(page, size, sort);
        var livros = livroService.findAll(pageRequest);
        return ResponseEntity.ok(livros);
    }

    @Operation(
            summary = "Buscar livro por código",
            description = "Retorna os dados completos de um livro específico, " +
                    "incluindo seus autores e assuntos associados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Livro encontrado com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LivroResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Livro não encontrado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro Interno do Servidor",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            )
    })
    @GetMapping("/{codigo}")
    public ResponseEntity<LivroResponseDTO> findById(
            @Parameter(description = "Código do livro", required = true)
            @PathVariable Integer codigo
    ) {
        var livro = livroService.findById(codigo);

        return ResponseEntity.ok(livro);
    }

    @Operation(
            summary = "Criar novo livro",
            description = "Cadastra um novo livro no sistema. " +
                    "É necessário informar pelo menos um autor e um assunto. " +
                    "O título deve ser único no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Livro criado com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LivroResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationErrorDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Livro com título já existente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro Interno do Servidor",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<LivroResponseDTO> create(
            @Parameter(description = "Dados do livro a ser criado", required = true)
            @Valid @RequestBody LivroRequestDTO livroRequestDTO
    ) {
        var livroCriado = livroService.create(livroRequestDTO);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(livroCriado.getCodigo())
                .toUri();

        return ResponseEntity.created(location).body(livroCriado);
    }

    @Operation(
            summary = "Atualizar livro existente",
            description = "Atualiza os dados de um livro existente. " +
                    "Todos os campos podem ser atualizados, mantendo as mesmas regras de validação da criação."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Livro atualizado com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LivroResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationErrorDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Livro não encontrado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Título já utilizado por outro livro",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro Interno do Servidor",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            )
    })
    @PutMapping("/{codigo}")
    public ResponseEntity<LivroResponseDTO> update(
            @Parameter(description = "Código do livro a ser atualizado", required = true)
            @PathVariable Integer codigo,
            @Parameter(description = "Novos dados do livro", required = true)
            @Valid @RequestBody LivroRequestDTO livroRequestDTO
    ) {
        var livroAtualizado = livroService.update(codigo, livroRequestDTO);
        return ResponseEntity.ok(livroAtualizado);
    }

    @Operation(
            summary = "Deletar livro",
            description = "Remove um livro do sistema. " +
                    "A operação não pode ser desfeita."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Livro deletado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Livro não encontrado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro Interno do Servidor",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            )
    })
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Código do livro a ser deletado", required = true)
            @PathVariable Integer codigo
    ) {
        livroService.delete(codigo);
        return ResponseEntity.noContent().build();
    }
}
