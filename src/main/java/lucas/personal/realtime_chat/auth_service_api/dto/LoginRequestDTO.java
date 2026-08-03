package lucas.personal.realtime_chat.auth_service_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "Username or Email is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;

}
