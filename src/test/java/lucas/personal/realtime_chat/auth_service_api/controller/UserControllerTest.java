package lucas.personal.realtime_chat.auth_service_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lucas.personal.realtime_chat.auth_service_api.config.SecurityConfigTest;
import lucas.personal.realtime_chat.auth_service_api.dto.UserCreateRequestDTO;
import lucas.personal.realtime_chat.auth_service_api.dto.UserResponseDTO;
import lucas.personal.realtime_chat.auth_service_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfigTest.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserCreateRequestDTO requestDTO;
    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new UserCreateRequestDTO();
        requestDTO.setUsername("lucas_dev");
        requestDTO.setEmail("lucas@gmail.com");
        requestDTO.setPassword("password123");

        responseDTO = UserResponseDTO.builder()
                .userId(1L)
                .username("lucas_dev")
                .email("lucas@gmail.com")
                .build();
    }

    @Test
    void createUser_ShouldReturn201AndUser() throws Exception {
        when(userService.createUser(any(UserCreateRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("lucas_dev"));
    }

    @Test
    void getAllUsers_ShouldReturn200AndAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("lucas_dev"));
    }

    @Test
    void getUserById_ShouldReturn200AndUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void updateUser_ShouldReturn200AndUser() throws Exception {
        when(userService.updateUser(eq(1L), any(UserCreateRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("lucas_dev"))
                .andExpect(jsonPath("$.email").value("lucas@gmail.com"));
    }

    @Test
    void deleteUser_ShouldReturn204AndVoid() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }
}