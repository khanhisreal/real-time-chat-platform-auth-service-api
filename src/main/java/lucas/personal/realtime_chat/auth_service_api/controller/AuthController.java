package lucas.personal.realtime_chat.auth_service_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lucas.personal.realtime_chat.auth_service_api.dto.LoginRequestDTO;
import lucas.personal.realtime_chat.auth_service_api.dto.RegisterRequestDTO;
import lucas.personal.realtime_chat.auth_service_api.dto.UserResponseDTO;
import lucas.personal.realtime_chat.auth_service_api.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid
                                                    @RequestBody RegisterRequestDTO request) {
        UserResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        UserResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
