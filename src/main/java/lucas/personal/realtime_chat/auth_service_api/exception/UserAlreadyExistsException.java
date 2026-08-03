package lucas.personal.realtime_chat.auth_service_api.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends LocalizableBusinessException {

    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

}
