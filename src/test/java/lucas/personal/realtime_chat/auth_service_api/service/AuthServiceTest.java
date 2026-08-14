package lucas.personal.realtime_chat.auth_service_api.service;

import lucas.personal.realtime_chat.auth_service_api.dto.AuthResponseDTO;
import lucas.personal.realtime_chat.auth_service_api.dto.LoginRequestDTO;
import lucas.personal.realtime_chat.auth_service_api.dto.RegisterRequestDTO;
import lucas.personal.realtime_chat.auth_service_api.dto.UserResponseDTO;
import lucas.personal.realtime_chat.auth_service_api.entity.User;
import lucas.personal.realtime_chat.auth_service_api.exception.InvalidCredentialException;
import lucas.personal.realtime_chat.auth_service_api.exception.UserAlreadyExistsException;
import lucas.personal.realtime_chat.auth_service_api.mapper.UserMapper;
import lucas.personal.realtime_chat.auth_service_api.repository.UserRepository;
import lucas.personal.realtime_chat.auth_service_api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private User user;
    private UserResponseDTO userResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@gmail.com");
        registerRequest.setPassword("password");

        loginRequest = new LoginRequestDTO();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        user = User.builder()
                .userId(1L)
                .username("testuser")
                .email("test@gmail.com")
                .password("encoded_password")
                .build();

        userResponse = UserResponseDTO.builder()
                .userId(1L)
                .username("testuser")
                .email("test@gmail.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("register() - Should successfully register a new user")
    void register_Success() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.mapToResponse(user)).thenReturn(userResponse);

        UserResponseDTO response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("register() - Should throw UserAlreadyExistsException when username exists")
    void register_DuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username is already taken!");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("register() - Should throw UserAlreadyExistsException when email exists")
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email is already registered!");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("login() - Should successfully log in with correct credentials")
    void login_Success() {
        String mockToken = "mocked.jwt.token";
        AuthResponseDTO authResponseDTO = AuthResponseDTO.builder()
                .token(mockToken)
                .tokenType("Bearer")
                .user(userResponse)
                .build();

        when(userRepository.findByUsername(loginRequest.getUsernameOrEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getUserId(), user.getUsername())).thenReturn(mockToken);
        when(userMapper.mapToAuthResponse(user, mockToken)).thenReturn(authResponseDTO);

        AuthResponseDTO response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(mockToken);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");

        verify(jwtService, times(1)).generateToken(user.getUserId(), user.getUsername());
    }

    @Test
    @DisplayName("login() - Should throw InvalidCredentialsException when user not found")
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername(loginRequest.getUsernameOrEmail())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(loginRequest.getUsernameOrEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialException.class)
                .hasMessage("Invalid username/email or password!");
    }

    @Test
    @DisplayName("login() - Should throw InvalidCredentialsException when password does not match")
    void login_WrongPassword_ThrowsException() {
        when(userRepository.findByUsername(loginRequest.getUsernameOrEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialException.class)
                .hasMessage("Invalid username/email or password!");
    }

}