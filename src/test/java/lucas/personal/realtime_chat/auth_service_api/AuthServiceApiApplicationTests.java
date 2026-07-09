package lucas.personal.realtime_chat.auth_service_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceApiApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_Method_Coverage() {
        String[] args = new String[]{
                "--spring.profiles.active=test",
                "--server.port=0"
        };
        assertDoesNotThrow(() -> AuthServiceApiApplication.main(args));
    }

}
