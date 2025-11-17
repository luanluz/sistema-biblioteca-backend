package biblioteca.dev.luanluz.api.exception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {
    private final String type;
    private final String title;
    private final int status;
    private final String detail;
    private final String instance;
}
