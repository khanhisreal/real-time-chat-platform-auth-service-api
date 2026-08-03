package lucas.personal.realtime_chat.auth_service_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
    private int status;
    private String error;
    private String message;
    private Instant timestamp;
    private Map<String, String> validationErrors;
}
