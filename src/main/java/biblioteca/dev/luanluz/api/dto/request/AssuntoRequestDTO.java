package biblioteca.dev.luanluz.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssuntoRequestDTO {

    @NotBlank(message = "{assunto.descricao.notblank}")
    @Size(max = 20, message = "{assunto.descricao.size}")
    private String descricao;
}
