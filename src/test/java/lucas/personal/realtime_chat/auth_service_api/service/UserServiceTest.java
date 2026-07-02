package lucas.personal.realtime_chat.auth_service_api.service;

import lucas.personal.realtime_chat.auth_service_api.dto.UserCreateRequestDTO;
import lucas.personal.realtime_chat.auth_service_api.dto.UserResponseDTO;
import lucas.personal.realtime_chat.auth_service_api.entity.User;
import lucas.personal.realtime_chat.auth_service_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserCreateRequestDTO requestDTO;
    private User mockUser;

    private static final String ENCRYPTED_PASSWORD = "$2a$12$5BgSMZJZosmcuGKuhPbCLuu24JJKvNs.s.nQpXYqJFScaFJWXKMlC";

    @BeforeEach
    void setUp() {
        requestDTO = new UserCreateRequestDTO();
        requestDTO.setUsername("test_user");
        requestDTO.setPassword("rawPassword123");
        requestDTO.setEmail("test@gmail.com");

        mockUser = User.builder()
                .userId(1L)
                .username("test_user")
                .email("test@gmail.com")
                .password(ENCRYPTED_PASSWORD)
                .build();
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByUsername(requestDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn(ENCRYPTED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponseDTO response = userService.createUser(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("test_user", response.getUsername());
        assertEquals("test@gmail.com", response.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ThrowsException_WhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername(requestDTO.getUsername())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(requestDTO);
        });

        assertEquals("Username already taken!", exception.getMessage());

        verify(userRepository, times(1)).existsByUsername(requestDTO.getUsername());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ThrowsException_WhenEmailAlreadyExists() {
        when(userRepository.existsByUsername(requestDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(requestDTO);
        });

        assertEquals("Email already registered!", exception.getMessage());

        verify(userRepository, times(1)).existsByUsername(requestDTO.getUsername());
        verify(userRepository, times(1)).existsByEmail(requestDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));

        List<UserResponseDTO> responses = userService.getAllUsers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("test_user", responses.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(mockUser));

        UserResponseDTO response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("test_user", response.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ThrowsException_WhenUserIsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(99L);
        });

        assertEquals("User not found with id: 99", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void updateUser_WithNewPassword_Success() {
        UserCreateRequestDTO updateRequest = new UserCreateRequestDTO();
        updateRequest.setUsername("updated_lucas");
        updateRequest.setEmail("updated@gmail.com");
        updateRequest.setPassword("newRawPassword456");

        User updatedMockUser = User.builder()
                .userId(1L)
                .username("updated_lucas")
                .email("updated@gmail.com")
                .password("$2a$12$NEW_HASHED_PASSWORD_STRING")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedMockUser);

        UserResponseDTO response = userService.updateUser(1L, updateRequest);

        assertNotNull(response);
        assertEquals("updated_lucas", response.getUsername());
        assertEquals("updated@gmail.com", response.getEmail());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void updateUser_WithoutPassword_Success() {
        UserCreateRequestDTO updateRequest = new UserCreateRequestDTO();
        updateRequest.setUsername("updated_lucas_no_pwd");
        updateRequest.setEmail("updated_email@gmail.com");
        updateRequest.setPassword("");

        User updatedMockUser = User.builder()
                .userId(1L)
                .username("updated_lucas_no_pwd")
                .email("updated_email@gmail.com")
                .password(ENCRYPTED_PASSWORD)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedMockUser);

        UserResponseDTO response = userService.updateUser(1L, updateRequest);

        assertNotNull(response);
        assertEquals("updated_lucas_no_pwd", response.getUsername());
        assertEquals("updated_email@gmail.com", response.getEmail());

        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void updateUser_WithNullPassword_Success() {
        UserCreateRequestDTO updateRequest = new UserCreateRequestDTO();
        updateRequest.setUsername("updated_lucas_null_pwd");
        updateRequest.setEmail("null_email@gmail.com");
        updateRequest.setPassword(null);

        User updatedMockUser = User.builder()
                .userId(1L)
                .username("updated_lucas_null_pwd")
                .email("null_email@gmail.com")
                .password(ENCRYPTED_PASSWORD)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedMockUser);

        UserResponseDTO response = userService.updateUser(1L, updateRequest);

        assertNotNull(response);
        assertEquals("updated_lucas_null_pwd", response.getUsername());
        assertEquals("null_email@gmail.com", response.getEmail());

        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void updateUser_ThrowsException_WhenUserDoesNotExist() {
        UserCreateRequestDTO updateRequest = new UserCreateRequestDTO();
        updateRequest.setUsername("updated_lucas");
        updateRequest.setEmail("updated@gmail.com");
        updateRequest.setPassword("newRawPassword456");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(99L, updateRequest);
        });

        assertEquals("User not found with id: 99", exception.getMessage());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_ThrowsException_WhenUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(99L);
        });

        assertEquals("User not found with id: 99", exception.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }
}