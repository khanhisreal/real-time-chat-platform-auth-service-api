package lucas.personal.realtime_chat.auth_service_api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    private JwtAuthenticationEntryPoint entryPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        entryPoint = new JwtAuthenticationEntryPoint(objectMapper);
    }

    @Test
    @DisplayName("commence() - Should write 401 JSON error response to output stream")
    void commence_ShouldSet401StatusAndReturnErrorJson() throws IOException, ServletException {
        AuthenticationException authException =
                new BadCredentialsException("Bad credentials");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);

        entryPoint.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jsonResponse = stringWriter.toString();
        assertThat(jsonResponse).contains("\"status\":401");
        assertThat(jsonResponse).contains("\"error\":\"Unauthorized\"");
        assertThat(jsonResponse)
                .contains("Full authentication is required to access this resource");
        assertThat(jsonResponse).contains("\"timestamp\"");
    }
}