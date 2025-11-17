package biblioteca.dev.luanluz.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta paginada")
public class PaginacaoDTO<T> {

    @Schema(description = "Lista de elementos da página atual")
    private List<T> content;

    @Schema(description = "Informações sobre a paginação")
    private PageInfo page;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Metadados da paginação")
    public static class PageInfo {

        @Schema(description = "Tamanho da página", example = "20")
        private int size;

        @Schema(description = "Número da página atual (começa em 0)", example = "0")
        private int number;

        @Schema(description = "Total de elementos encontrados", example = "100")
        private long totalElements;

        @Schema(description = "Total de páginas disponíveis", example = "5")
        private int totalPages;
    }
}
