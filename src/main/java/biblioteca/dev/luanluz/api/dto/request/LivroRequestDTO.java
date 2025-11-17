package biblioteca.dev.luanluz.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivroRequestDTO {

    @NotBlank(message = "{livro.titulo.notblank}")
    @Size(max = 40, message = "{livro.titulo.size}")
    private String titulo;

    @NotBlank(message = "{livro.editora.notblank}")
    @Size(max = 40, message = "{livro.editora.size}")
    private String editora;

    @NotNull(message = "{livro.edicao.notnull}")
    @Positive(message = "{livro.edicao.positive}")
    private Integer edicao;

    @NotBlank(message = "{livro.anopublicacao.notblank}")
    @Pattern(regexp = "^\\d{4}$", message = "{livro.anopublicacao.pattern}")
    private String anoPublicacao;

    @NotNull(message = "{livro.valoremcentavos.notnull}")
    @PositiveOrZero(message = "{livro.valoremcentavos.positiveorzero}")
    private Integer valorEmCentavos;

    @NotEmpty(message = "{livro.autorescodigos.notempty}")
    private Set<Integer> autoresCodigos;

    @NotEmpty(message = "{livro.assuntoscodigos.notempty}")
    private Set<Integer> assuntosCodigos;
}
