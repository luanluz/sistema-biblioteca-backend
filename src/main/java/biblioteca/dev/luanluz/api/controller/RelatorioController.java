package biblioteca.dev.luanluz.api.controller;

import biblioteca.dev.luanluz.api.exception.error.ErrorDetail;
import biblioteca.dev.luanluz.api.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/relatorio")
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios do sistema")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @Operation(
            summary = "Gerar relatório agrupado por autores em PDF",
            description = "Gera e retorna o relatório de todos os autores em formato PDF. " +
                    "O relatório inclui informações de cada autor, seus livros, editoras, " +
                    "assuntos relacionados, valores e período de publicação. "
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Relatório PDF gerado com sucesso",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PDF_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor ao gerar o relatório PDF",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            )
    })
    @GetMapping(value = "/autor/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> gerarRelatorioAutoresPDF() throws Exception {
        byte[] relatorio = relatorioService.gerarRelatorioAutoresPDF();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "relatorio_autores.pdf");

        return new ResponseEntity<>(relatorio, headers, HttpStatus.OK);
    }

    @Operation(
            summary = "Gerar relatório agrupado por autores em Excel",
            description = "Gera e retorna o relatório de todos os autores em formato Excel (XLSX). " +
                    "O relatório inclui informações de cada autor em formato de planilha. "
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Relatório Excel gerado com sucesso",
                    content = @Content(
                            mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor ao gerar o relatório Excel",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            )
    })
    @GetMapping(value = "/autor/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> gerarRelatorioAutoresExcel() throws Exception {
        byte[] relatorio = relatorioService.gerarRelatorioAutoresExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        headers.setContentDispositionFormData("attachment", "relatorio_autores.xlsx");

        return new ResponseEntity<>(relatorio, headers, HttpStatus.OK);
    }
}
