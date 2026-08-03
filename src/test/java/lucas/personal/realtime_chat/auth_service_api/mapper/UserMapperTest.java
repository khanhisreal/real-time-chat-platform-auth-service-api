package lucas.personal.realtime_chat.auth_service_api.mapper;

import lucas.personal.realtime_chat.auth_service_api.dto.UserResponseDTO;
import lucas.personal.realtime_chat.auth_service_api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    @DisplayName("mapToResponse() - Should correctly map all User entity fields to UserResponseDTO")
    void mapToResponse_ShouldMapAllFieldsCorrectly() {
        Instant now = Instant.now();
        User user = User.builder()
                .userId(100L)
                .username("lucas_dev")
                .email("lucas@gcash.com")
                .password("encoded_password")
                .build();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        UserResponseDTO response = userMapper.mapToResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(100L);
        assertThat(response.getUsername()).isEqualTo("lucas_dev");
        assertThat(response.getEmail()).isEqualTo("lucas@gcash.com");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("mapToResponse() - Should handle mapping when timestamp fields are null")
    void mapToResponse_ShouldHandleNullTimestamps() {
        User user = User.builder()
                .userId(1L)
                .username("test_user")
                .email("test@gmail.com")
                .password("hash")
                .build();

        UserResponseDTO response = userMapper.mapToResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("test_user");
        assertThat(response.getEmail()).isEqualTo("test@gmail.com");
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
    }
}