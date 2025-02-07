package bg.sofia.uni.fmi.mjt.crypto.wallet.exception;

public class InvalidCommandArgumentException extends RuntimeException {
    public InvalidCommandArgumentException(String message) {
        super(message);
    }

    public InvalidCommandArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
