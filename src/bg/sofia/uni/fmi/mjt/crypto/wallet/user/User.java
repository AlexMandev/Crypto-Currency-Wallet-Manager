package bg.sofia.uni.fmi.mjt.crypto.wallet.user;

import bg.sofia.uni.fmi.mjt.crypto.wallet.algorithm.HashAlgorithm;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 9191793071732236523L;

    private final String username;
    private final String hashedPassword;
    private final Wallet wallet;

    public User(String username, String password) {
        this.username = username;
        this.hashedPassword = HashAlgorithm.hashPassword(password);
        this.wallet = new Wallet();
    }

    public String getUsername() {
        return username;
    }

    public boolean matchesPassword(String password) {
        return HashAlgorithm.hashPassword(password).equals(hashedPassword);
    }

    public Wallet getWallet() {
        return wallet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
