package biblioteca.dev.luanluz.api.exception.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.List;

public class ErrorDetailFactory {
    public static ResponseEntity<Object> create(
            String title,
            String detail,
            HttpStatus status,
            WebRequest request,
            List<InvalidField> invalidFields
    ) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

        String baseUri = String.format("%s://%s:%d",
                servletRequest.getScheme(),
                servletRequest.getServerName(),
                servletRequest.getServerPort()
        );

        String errorPath = resolveErrorPath(status);
        URI type = URI.create(baseUri + errorPath);
        String instance = request
                .getDescription(false)
                .replace("uri=", "");

        ErrorDetail errorDetail = ValidationErrorDetail.validationErrorDetail()
                .type(type.toString())
                .title(title)
                .status(status.value())
                .detail(detail)
                .instance(instance)
                .invalidFields(invalidFields)
                .build();

        return ResponseEntity
                .status(status)
                .body(errorDetail);
    }

    private static String resolveErrorPath(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> "/not-found";
            case BAD_REQUEST -> "/bad-request";
            case CONFLICT -> "/conflict";
            case INTERNAL_SERVER_ERROR -> "/internal-error";
            default -> "/error";
        };
    }
}
