package bg.sofia.uni.fmi.mjt.crypto.wallet.exception;

public class UnknownCommandException extends Exception {
    public UnknownCommandException(String message) {
        super(message);
    }

    public UnknownCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
