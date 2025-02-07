package bg.sofia.uni.fmi.mjt.crypto.wallet.storage;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;

import java.nio.file.Path;
import java.util.Map;

public interface UserRepository extends AutoCloseable {

    void addUser(User user) throws UserAlreadyExistsException;

    void load(Path path);

    void save(Path path);

    User getUser(String username);

    Map<String, User> getUsers();
}
