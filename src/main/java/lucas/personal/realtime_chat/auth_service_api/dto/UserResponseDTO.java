package lucas.personal.realtime_chat.auth_service_api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserResponseDTO {

    private Long userId;

    private String username;

    private String email;

    private Instant createdAt;

    private Instant updatedAt;

}
