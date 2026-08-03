package lucas.personal.realtime_chat.auth_service_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lucas.personal.realtime_chat.auth_service_api.dto.LoginRequestDTO;
import lucas.personal.realtime_chat.auth_service_api.dto.RegisterRequestDTO;
import lucas.personal.realtime_chat.auth_service_api.dto.UserResponseDTO;
import lucas.personal.realtime_chat.auth_service_api.exception.InvalidCredentialException;
import lucas.personal.realtime_chat.auth_service_api.exception.UserAlreadyExistsException;
import lucas.personal.realtime_chat.auth_service_api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {AuthController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private UserResponseDTO userResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@gmail.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequestDTO();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        userResponse = UserResponseDTO.builder()
                .userId(1L)
                .username("testuser")
                .email("test@gmail.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should return 201 Created on valid request")
    void register_Success_Returns201() throws Exception {
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should return 409 Conflict when user exists")
    void register_UserExists_Returns409() throws Exception {
        when(authService.register(any(RegisterRequestDTO.class)))
                .thenThrow(new UserAlreadyExistsException("Username is already taken!"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Username is already taken!"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should return 400 Bad Request on validation failure")
    void register_InvalidDTO_Returns400() throws Exception {
        registerRequest.setEmail("not-an-email");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Should return 200 OK on successful login")
    void login_Success_Returns200() throws Exception {
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Should return 401 Unauthorized on invalid credentials")
    void login_InvalidCredentials_Returns401() throws Exception {
        when(authService.login(any(LoginRequestDTO.class))).thenThrow(new InvalidCredentialException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid username/email or password!"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Should return 500 Internal Server Error on unexpected exception")
    void login_UnexpectedException_Returns500() throws Exception {
        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new RuntimeException("Database connection timed out!"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred."));
    }
}