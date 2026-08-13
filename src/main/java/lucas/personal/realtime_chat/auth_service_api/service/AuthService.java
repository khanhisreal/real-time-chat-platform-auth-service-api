package lucas.personal.realtime_chat.auth_service_api.service;

import lombok.RequiredArgsConstructor;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final JwtService jwtService;

    public UserResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.mapToResponse(savedUser);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(InvalidCredentialException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialException();
        }

        String token = jwtService.generateToken(user.getUserId(), user.getUsername());

        return userMapper.mapToAuthResponse(user, token);
    }
}
