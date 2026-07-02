package lucas.personal.realtime_chat.auth_service_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class AuthServiceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApiApplication.class, args);
	}

}
