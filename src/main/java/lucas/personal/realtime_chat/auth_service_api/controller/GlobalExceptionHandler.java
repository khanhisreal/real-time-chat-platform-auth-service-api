package lucas.personal.realtime_chat.auth_service_api.controller;

import lucas.personal.realtime_chat.auth_service_api.dto.ErrorResponseDTO;
import lucas.personal.realtime_chat.auth_service_api.exception.LocalizableBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocalizableBusinessException.class)
    public ResponseEntity<ErrorResponseDTO>
    handleLocalizableBusinessException(LocalizableBusinessException ex) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO
                .builder()
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(ex.getStatus()).body(errorResponseDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO>
    handleValidationsException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO
                .builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .validationErrors(errors)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred.")
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDTO);
    }
}
