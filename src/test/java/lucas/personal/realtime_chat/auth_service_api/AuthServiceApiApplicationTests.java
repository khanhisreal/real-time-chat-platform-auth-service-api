package lucas.personal.realtime_chat.auth_service_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class AuthServiceApiApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_Method_Coverage() {
        String[] args = new String[]{};
        assertDoesNotThrow(() -> AuthServiceApiApplication.main(args));
    }

}
