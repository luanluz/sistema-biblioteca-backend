package biblioteca.dev.luanluz.api.exception;

import biblioteca.dev.luanluz.api.exception.error.ErrorDetail;
import biblioteca.dev.luanluz.api.exception.error.ErrorDetailFactory;
import biblioteca.dev.luanluz.api.exception.error.InvalidField;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Object> handleMissingRequestHeader(
            MissingRequestHeaderException ex,
            WebRequest request
    ) {
        log.error("Cabeçalho obrigatório ausente: {}", ex.getHeaderName());
        return ErrorDetailFactory.create(
                "Cabeçalho obrigatório ausente",
                "Falha. O cabecom çalho " + ex.getHeaderName() + " obrigatório não foi informado.",
                HttpStatus.BAD_REQUEST,
                request,
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        log.error("Falha na validação: {}", ex.getMessage());
        var invalidFields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new InvalidField(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ErrorDetailFactory.create(
                "Requisição mal sucedida",
                "Um ou mais campos são inválidos",
                HttpStatus.BAD_REQUEST,
                request,
                invalidFields
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request
    ) {
        log.error("Requisição mal formada: {}", ex.getMessage());

        if (ex.getCause() instanceof InvalidFormatException invalidFormatEx) {
            if (invalidFormatEx.getTargetType() != null &&
                    invalidFormatEx.getTargetType().isEnum()) {

                String fieldName = getFieldNameFromPath(invalidFormatEx);
                String enumValues = getEnumValues(invalidFormatEx.getTargetType());
                String errorMessage = "O campo " + fieldName + " está inválido. Os valores aceitos são: "
                        + enumValues.toUpperCase() + ".";

                List<InvalidField> invalidFields = List.of(
                        new InvalidField(fieldName, errorMessage)
                );

                return ErrorDetailFactory.create(
                        "Requisição mal sucedida",
                        "Um ou mais campos são inválidos",
                        HttpStatus.BAD_REQUEST,
                        request,
                        invalidFields
                );
            }
        }

        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

        String baseUri = String.format("%s://%s:%d",
                servletRequest.getScheme(),
                servletRequest.getServerName(),
                servletRequest.getServerPort()
        );
        URI type = URI.create(baseUri + "/bad-request");

        ErrorDetail problemDetail = ErrorDetail.builder()
                .type(type.toString())
                .title("Requisição mal formada")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("O corpo da requisição está mal formado ou contém dados inválidos.")
                .instance(request.getDescription(false))
                .build();

        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDetail> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            WebRequest request
    ) {
        log.error("Método HTTP não suportado para {}: {}", request.getDescription(false), ex.getMessage());

        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

        String baseUri = String.format("%s://%s:%d",
                servletRequest.getScheme(),
                servletRequest.getServerName(),
                servletRequest.getServerPort()
        );
        URI type = URI.create(baseUri + "/method-not-allowed");

        String supportedMethods = ex.getSupportedMethods() != null
                ? String.join(", ", ex.getSupportedMethods())
                : "Nenhum método suportado";
        var message = String.format(
                "O método '%s' não é suportado para este recurso. Métodos suportados: %s",
                ex.getMethod(),
                supportedMethods
        );

        ErrorDetail problemDetail = ErrorDetail.builder()
                .type(type.toString())
                .title("Método HTTP não suportado")
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .detail(message)
                .instance(request.getDescription(false))
                .build();

        return new ResponseEntity<>(problemDetail, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorDetail> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            WebRequest request
    ) {
        log.error("Tipo de mídia não suportado: {}", ex.getMessage());

        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

        String baseUri = String.format("%s://%s:%d",
                servletRequest.getScheme(),
                servletRequest.getServerName(),
                servletRequest.getServerPort()
        );
        URI type = URI.create(baseUri + "/unsupported-media-type");

        String supportedMediaTypes = !ex.getSupportedMediaTypes().isEmpty()
                ? ex.getSupportedMediaTypes().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "))
                : "Nenhum tipo de mídia suportado";

        var message = String.format(
                "O tipo de mídia '%s' não é suportado para este recurso. Tipos de mídia suportados: %s",
                ex.getContentType(),
                supportedMediaTypes
        );

        ErrorDetail problemDetail = ErrorDetail.builder()
                .type(type.toString())
                .title("Tipo de mídia não suportado")
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .detail(message)
                .instance(request.getDescription(false))
                .build();

        return new ResponseEntity<>(problemDetail, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request
    ) {
        log.error("Violação de constraints de validação: {}", ex.getMessage());

        List<InvalidField> invalidFields = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.contains(".")
                    ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
                    : propertyPath;

            invalidFields.add(new InvalidField(fieldName, violation.getMessage()));
        }

        return ErrorDetailFactory.create(
                "Requisição mal sucedida",
                "Um ou mais parâmetros são inválidos",
                HttpStatus.BAD_REQUEST,
                request,
                invalidFields
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleRegistroNaoEncontrado(
            ResourceNotFoundException ex,
            WebRequest request
    ) {
        log.error("Registro não encontrado: {}", ex.getMessage());
        return ErrorDetailFactory.create(
                "Recurso não encontrado",
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request,
                null
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFound(NoHandlerFoundException ex, WebRequest request) {
        String message = ex.getMessage() == null ? "Recurso não encontrado: " + ex.getRequestURL() : ex.getMessage();

        log.error("Recurso não encontrado: {}", message);
        return ErrorDetailFactory.create(
                "Recurso não encontrado",
                message,
                HttpStatus.NOT_FOUND,
                request,
                null
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicateResourceException(
            DuplicateResourceException ex,
            WebRequest request
    ) {
        log.warn("Conflito: {}", ex.getMessage());
        return ErrorDetailFactory.create(
                "Conflito",
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request,
                null
        );
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Object> handleDomainException(
            DomainException ex,
            WebRequest request
    ) {
        log.warn("Erro de domínio: {}", ex.getMessage());
        return ErrorDetailFactory.create(
                "Erro de domínio",
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request,
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(
            Exception ex,
            WebRequest request
    ) {
        log.error("Ocorreu um erro inesperado: {}", ex.getMessage(), ex);
        return ErrorDetailFactory.create(
                "Erro interno do servidor",
                "Ocorreu um erro inesperado no servidor.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request,
                null
        );
    }

    private String getFieldNameFromPath(InvalidFormatException ex) {
        if (ex.getPath() != null && !ex.getPath().isEmpty()) {
            String fieldName = ex.getPath().get(ex.getPath().size() - 1).getFieldName();
            return fieldName != null ? fieldName : "campo";
        }

        return "campo";
    }

    private String getEnumValues(Class<?> enumClass) {
        try {
            Object[] enumConstants = enumClass.getEnumConstants();
            if (enumConstants != null && enumConstants.length > 0) {
                List<String> values = new ArrayList<>();
                for (Object enumConstant : enumConstants) {
                    values.add(enumConstant.toString().toLowerCase());
                }
                return String.join(", ", values);
            }
        } catch (Exception e) {
            log.warn("Erro ao obter valores do enum {}: {}", enumClass.getSimpleName(), e.getMessage());
        }

        return "valores válidos";
    }
}
