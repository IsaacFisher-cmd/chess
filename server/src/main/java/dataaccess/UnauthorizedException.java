package dataaccess;

public class UnauthorizedException extends RuntimeException {
    // Error thrown when there is not the correct Auth token
    public UnauthorizedException() {}
}
