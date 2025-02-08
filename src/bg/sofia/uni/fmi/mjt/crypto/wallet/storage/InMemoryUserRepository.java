package bg.sofia.uni.fmi.mjt.crypto.wallet.storage;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {

    public static final String FILE_PATH = "data" + File.separator + "users.dat";
    private Map<String, User> users;

    public InMemoryUserRepository() {
        this.users = new HashMap<>();
    }

    InMemoryUserRepository(Map<String, User> users) {
        this.users = users;
    }

    @Override
    public void addUser(User user) throws UserAlreadyExistsException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (users.containsKey(user.getUsername())) {
            throw new UserAlreadyExistsException("User with username " + user.getUsername() + " already exists.");
        }
        users.put(user.getUsername(), user);
    }

    @Override
    public void load() {
        try (ObjectInputStream usersStream = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            users = (Map<String, User>) usersStream.readObject();
        } catch (IOException e) {
            throw new UncheckedIOException("Error occurred when loading users from file.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error occurred when loading users from file.", e);
        }
    }

    @Override
    public void save() {
        try (ObjectOutputStream usersStream = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            usersStream.writeObject(users);
        } catch (IOException e) {
            throw new UncheckedIOException("Error occurred when saving users to file.", e);
        }
    }

    @Override
    public User getUser(String username) {
        return users.getOrDefault(username, null);
    }

    @Override
    public Map<String, User> getUsers() {
        return Collections.unmodifiableMap(users);
    }

    @Override
    public void close() {
        save();
    }
}
