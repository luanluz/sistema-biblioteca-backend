package biblioteca.dev.luanluz.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivroResponseDTO {

    private Integer codigo;
    private String titulo;
    private String editora;
    private Integer edicao;
    private String anoPublicacao;
    private Integer valorEmCentavos;
    private Set<AutorResponseDTO> autores;
    private Set<AssuntoResponseDTO> assuntos;
}
