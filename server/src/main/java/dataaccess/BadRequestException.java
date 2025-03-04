package dataaccess;

public class BadRequestException extends RuntimeException {
    // Error thrown when a request is lacking or wrong
    public BadRequestException(String message) {
        super(message);
    }
}
