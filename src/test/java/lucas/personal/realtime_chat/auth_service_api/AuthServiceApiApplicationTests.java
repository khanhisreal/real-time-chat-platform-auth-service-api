package lucas.personal.realtime_chat.auth_service_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class AuthServiceApiApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_Method_Coverage() {
        System.setProperty("spring.flyway.enabled", "false");
        System.setProperty("spring.jpa.hibernate.ddl-auto", "none");
        System.setProperty("spring.datasource.url", "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
        System.setProperty("spring.datasource.username", "sa");
        System.setProperty("spring.datasource.password", "");
        System.setProperty("server.port", "0");

        try {
            assertDoesNotThrow(() -> AuthServiceApiApplication.main(new String[]{}));
        } finally {
            System.clearProperty("spring.flyway.enabled");
            System.clearProperty("spring.jpa.hibernate.ddl-auto");
            System.clearProperty("spring.datasource.url");
            System.clearProperty("spring.datasource.driver-class-name");
            System.clearProperty("spring.datasource.username");
            System.clearProperty("spring.datasource.password");
            System.clearProperty("server.port");
        }
    }

}
