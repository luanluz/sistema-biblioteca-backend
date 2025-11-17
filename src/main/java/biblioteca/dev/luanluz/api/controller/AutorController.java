package biblioteca.dev.luanluz.api.controller;

import biblioteca.dev.luanluz.api.dto.request.AutorRequestDTO;
import biblioteca.dev.luanluz.api.dto.response.AutorPaginacaoDTO;
import biblioteca.dev.luanluz.api.dto.response.AutorResponseDTO;
import biblioteca.dev.luanluz.api.exception.error.ErrorDetail;
import biblioteca.dev.luanluz.api.exception.error.ValidationErrorDetail;
import biblioteca.dev.luanluz.api.service.AutorService;
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
@RequestMapping("/autor")
@Tag(name = "Autores", description = "Endpoints para gerenciamento de autores")
public class AutorController {

    private final AutorService autorService;

    @Operation(
            summary = "Listar todos os autores",
            description = "Retorna uma lista paginada de todos os autores cadastrados. " +
                    "Suporta paginação e ordenação através dos parâmetros page, size e sort."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de autores retornada com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AutorPaginacaoDTO.class)
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
    public ResponseEntity<Page<AutorResponseDTO>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(
                    description = "Parâmetros de ordenação no formato: propriedade,direção",
                    example = "nome,asc",
                    schema = @Schema(type = "string")
            )
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "nome", direction = Sort.Direction.ASC)
            }) Sort sort
    ) {
        var pageRequest = getPageRequest(page, size, sort);
        var autores = autorService.findAll(pageRequest);
        return ResponseEntity.ok(autores);
    }

    @Operation(
            summary = "Buscar autor por código",
            description = "Retorna os dados completos de um autor específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autor encontrado com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AutorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Autor não encontrado",
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
    public ResponseEntity<AutorResponseDTO> findById(
            @Parameter(description = "Código do autor", required = true)
            @PathVariable Integer codigo
    ) {
        var autor = autorService.findById(codigo);
        return ResponseEntity.ok(autor);
    }

    @Operation(
            summary = "Criar novo autor",
            description = "Cadastra um novo autor no sistema. " +
                    "O nome deve ser único no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Autor criado com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AutorResponseDTO.class)
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
                    description = "Autor com nome já existente",
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
    public ResponseEntity<AutorResponseDTO> create(
            @Parameter(description = "Dados do autor a ser criado", required = true)
            @Valid @RequestBody AutorRequestDTO autorRequestDTO
    ) {
        var autorCriado = autorService.create(autorRequestDTO);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(autorCriado.getCodigo())
                .toUri();

        return ResponseEntity.created(location).body(autorCriado);
    }

    @Operation(
            summary = "Atualizar autor existente",
            description = "Atualiza os dados de um autor existente. " +
                    "O nome deve permanecer único no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autor atualizado com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AutorResponseDTO.class)
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
                    description = "Autor não encontrado",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Nome já utilizado por outro autor",
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
    public ResponseEntity<AutorResponseDTO> update(
            @Parameter(description = "Código do autor a ser atualizado", required = true)
            @PathVariable Integer codigo,
            @Parameter(description = "Novos dados do autor", required = true)
            @Valid @RequestBody AutorRequestDTO autorRequestDTO
    ) {
        var autorAtualizado = autorService.update(codigo, autorRequestDTO);
        return ResponseEntity.ok(autorAtualizado);
    }

    @Operation(
            summary = "Deletar autor",
            description = "Remove um autor do sistema. " +
                    "A operação não pode ser desfeita. " +
                    "Não é possível deletar autores que possuem livros associados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Autor deletado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Autor possui livros associados",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationErrorDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Autor não encontrado",
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
            @Parameter(description = "Código do autor a ser deletado", required = true)
            @PathVariable Integer codigo
    ) {
        autorService.delete(codigo);
        return ResponseEntity.noContent().build();
    }
}
