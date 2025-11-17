package biblioteca.dev.luanluz.api.exception.error;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class ValidationErrorDetail extends ErrorDetail {

    @Getter
    private final List<InvalidField> invalidFields;

    @Builder(builderMethodName = "validationErrorDetail")
    public ValidationErrorDetail(
            String type,
            String title,
            int status,
            String detail,
            String instance,
            List<InvalidField> invalidFields
    ) {
        super(type, title, status, detail, instance);
        this.invalidFields = invalidFields;
    }
}
