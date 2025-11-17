package biblioteca.dev.luanluz.api.controller;

import biblioteca.dev.luanluz.api.dto.request.AssuntoRequestDTO;
import biblioteca.dev.luanluz.api.dto.response.AssuntoPaginacaoDTO;
import biblioteca.dev.luanluz.api.dto.response.AssuntoResponseDTO;
import biblioteca.dev.luanluz.api.service.AssuntoService;
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
import org.springframework.http.HttpStatus;
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
@RequestMapping("/assunto")
@Tag(name = "Assuntos", description = "Endpoints para gerenciamento de assuntos")
public class AssuntoController {

    private final AssuntoService assuntoService;

    @Operation(
            summary = "Listar todos os assuntos",
            description = "Retorna uma lista paginada de todos os assuntos cadastrados. " +
                    "Suporta paginação e ordenação através dos parâmetros page, size e sort."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de assuntos retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AssuntoPaginacaoDTO.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<Page<AssuntoResponseDTO>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(
                    description = "Parâmetros de ordenação no formato: propriedade,direção",
                    example = "descricao,asc",
                    schema = @Schema(type = "string")
            )
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "descricao", direction = Sort.Direction.ASC)
            }) Sort sort
    ) {
        var pageRequest = getPageRequest(page, size, sort);
        var assuntos = assuntoService.findAll(pageRequest);
        return ResponseEntity.ok(assuntos);
    }

    @Operation(
            summary = "Buscar assunto por código",
            description = "Retorna os dados completos de um assunto específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Assunto encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AssuntoResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assunto não encontrado",
                    content = @Content
            )
    })
    @GetMapping("/{codigo}")
    public ResponseEntity<AssuntoResponseDTO> findById(
            @Parameter(description = "Código do assunto", required = true)
            @PathVariable Integer codigo
    ) {
        var assunto = assuntoService.findById(codigo);
        return ResponseEntity.ok(assunto);
    }

    @Operation(
            summary = "Criar novo assunto",
            description = "Cadastra um novo assunto no sistema. " +
                    "A descrição deve ser única no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Assunto criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AssuntoResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Assunto com descrição já existente",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<AssuntoResponseDTO> create(
            @Parameter(description = "Dados do assunto a ser criado", required = true)
            @Valid @RequestBody AssuntoRequestDTO assuntoRequestDTO
    ) {
        var assuntoCriado = assuntoService.create(assuntoRequestDTO);
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(assuntoCriado.getCodigo())
                .toUri();

        return ResponseEntity.created(location).body(assuntoCriado);
    }

    @Operation(
            summary = "Atualizar assunto existente",
            description = "Atualiza os dados de um assunto existente. " +
                    "A descrição deve permanecer única no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Assunto atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AssuntoResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assunto não encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Descrição já utilizada por outro assunto",
                    content = @Content
            )
    })
    @PutMapping("/{codigo}")
    public ResponseEntity<AssuntoResponseDTO> update(
            @Parameter(description = "Código do assunto a ser atualizado", required = true)
            @PathVariable Integer codigo,
            @Parameter(description = "Novos dados do assunto", required = true)
            @Valid @RequestBody AssuntoRequestDTO assuntoRequestDTO
    ) {
        var assuntoAtualizado = assuntoService.update(codigo, assuntoRequestDTO);
        return ResponseEntity.ok(assuntoAtualizado);
    }

    @Operation(
            summary = "Deletar assunto",
            description = "Remove um assunto do sistema. " +
                    "A operação não pode ser desfeita. " +
                    "Não é possível deletar assuntos que possuem livros associados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Assunto deletado com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Assunto possui livros associados",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Assunto não encontrado",
                    content = @Content
            )
    })
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Código do assunto a ser deletado", required = true)
            @PathVariable Integer codigo
    ) {
        assuntoService.delete(codigo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
