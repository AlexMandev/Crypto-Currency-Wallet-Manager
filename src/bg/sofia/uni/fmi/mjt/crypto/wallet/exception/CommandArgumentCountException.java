package bg.sofia.uni.fmi.mjt.crypto.wallet.exception;

public class CommandArgumentCountException extends RuntimeException {
    public CommandArgumentCountException(String message) {
        super(message);
    }

    public CommandArgumentCountException(String message, Throwable cause) {
        super(message, cause);
    }
}
