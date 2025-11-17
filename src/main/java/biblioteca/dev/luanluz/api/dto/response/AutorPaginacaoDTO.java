package biblioteca.dev.luanluz.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta paginada de autores")
public class AutorPaginacaoDTO extends PaginacaoDTO<AutorResponseDTO> {
}
