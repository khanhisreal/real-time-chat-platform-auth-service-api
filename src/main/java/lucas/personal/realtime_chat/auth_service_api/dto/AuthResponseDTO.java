package lucas.personal.realtime_chat.auth_service_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private UserResponseDTO user;

}
