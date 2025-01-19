package bg.sofia.uni.fmi.mjt.crypto.wallet.exception;

public class UnavailableAssetException extends Exception {
    public UnavailableAssetException(String message) {
        super(message);
    }

    public UnavailableAssetException(String message, Exception e) {
        super(message, e);
    }
}
