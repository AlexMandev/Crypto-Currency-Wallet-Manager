package bg.sofia.uni.fmi.mjt.crypto.wallet.exception;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String message, Exception e) {
        super(message, e);
    }
}
