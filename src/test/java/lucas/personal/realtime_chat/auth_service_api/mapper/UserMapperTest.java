package lucas.personal.realtime_chat.auth_service_api.mapper;

import lucas.personal.realtime_chat.auth_service_api.dto.AuthResponseDTO;
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

    @Test
    @DisplayName("mapToAuthResponse() - Should correctly map User and JWT token to AuthResponseDTO")
    void mapToAuthResponse_ShouldMapUserAndTokenCorrectly() {
        Instant now = Instant.now();
        User user = User.builder()
                .userId(42L)
                .username("khanh_user")
                .email("khanh@gmail.com")
                .password("hashed_pass")
                .build();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        String mockToken = "eyJhbGciOiJIUzI1NiJ9.mocked.jwt.token";

        AuthResponseDTO authResponse = userMapper.mapToAuthResponse(user, mockToken);

        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getToken()).isEqualTo(mockToken);
        assertThat(authResponse.getTokenType()).isEqualTo("Bearer");
        assertThat(authResponse.getUser()).isNotNull();
        assertThat(authResponse.getUser().getUserId()).isEqualTo(42L);
        assertThat(authResponse.getUser().getUsername()).isEqualTo("khanh_user");
        assertThat(authResponse.getUser().getEmail()).isEqualTo("khanh@gmail.com");
        assertThat(authResponse.getUser().getCreatedAt()).isEqualTo(now);
        assertThat(authResponse.getUser().getUpdatedAt()).isEqualTo(now);
    }
}