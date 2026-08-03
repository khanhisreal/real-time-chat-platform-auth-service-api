package lucas.personal.realtime_chat.auth_service_api.mapper;

import lucas.personal.realtime_chat.auth_service_api.dto.UserResponseDTO;
import lucas.personal.realtime_chat.auth_service_api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO mapToResponse(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
