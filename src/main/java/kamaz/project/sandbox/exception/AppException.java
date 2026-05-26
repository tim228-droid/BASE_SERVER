package kamaz.project.sandbox.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException{
    private final HttpStatus status;
    private final String message;

    public AppException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
