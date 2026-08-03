package lucas.personal.realtime_chat.auth_service_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LocalizableBusinessException extends RuntimeException {

    private final HttpStatus status;

    public LocalizableBusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
