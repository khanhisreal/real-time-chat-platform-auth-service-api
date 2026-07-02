package lucas.personal.realtime_chat.auth_service_api.dto;

import lombok.Data;

@Data
public class UserCreateRequestDTO {

    private String username;

    private String password;

    private String email;

}
