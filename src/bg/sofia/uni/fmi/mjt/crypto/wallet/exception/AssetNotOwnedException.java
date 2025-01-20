package bg.sofia.uni.fmi.mjt.crypto.wallet.exception;

public class AssetNotOwnedException extends Exception {
    public AssetNotOwnedException(String message) {
        super(message);
    }

    public AssetNotOwnedException(String message, Exception e) {
        super(message, e);
    }
}
