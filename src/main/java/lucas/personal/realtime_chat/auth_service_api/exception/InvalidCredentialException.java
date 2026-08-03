package lucas.personal.realtime_chat.auth_service_api.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialException extends LocalizableBusinessException {

    private static final String MESSAGE = "Invalid username/email or password!";

    public InvalidCredentialException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }

}
