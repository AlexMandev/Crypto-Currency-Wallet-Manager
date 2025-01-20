package bg.sofia.uni.fmi.mjt.crypto.wallet.storage;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {

    private Map<String, User> users;

    public InMemoryUserRepository() {
        this.users = new HashMap<>();
    }

    public InMemoryUserRepository(Path path) {
        load(path);
    }

    @Override
    public void addUser(User user) throws UserAlreadyExistsException {
        if (users.containsKey(user.getUsername())) {
            throw new UserAlreadyExistsException("User with username " + user.getUsername() + " already exists.");
        }
        users.put(user.getUsername(), user);
    }

    @Override
    public void load(Path path) {
        try (ObjectInputStream usersStream = new ObjectInputStream(Files.newInputStream(path))) {
            Map<String, User> loadedUsers = (HashMap<String, User>) usersStream.readObject();
            users.clear();
            users.putAll(loadedUsers);
        } catch (IOException e) {
            throw new UncheckedIOException("Error occurred when loading users from file.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error occurred when loading users from file.", e);
        }
    }

    @Override
    public void save(Path path) {
        try (ObjectOutputStream usersStream
                     = new ObjectOutputStream(Files.newOutputStream(path))) {
            usersStream.writeObject(users);
        } catch (IOException e) {
            throw new UncheckedIOException("Error occurred when saving users to file.", e);
        }
    }

    @Override
    public Map<String, User> getUsers() {
        return Collections.unmodifiableMap(users);
    }
}
