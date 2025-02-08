package bg.sofia.uni.fmi.mjt.crypto.wallet.storage;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryUserRepositoryTest {

    @Test
    void addUserNull() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        assertThrows(IllegalArgumentException.class,
                () -> repo.addUser(null),
                "addUser should throw for null user");
    }

    @Test
    void getUsersReturnsUnmodifiableCollection() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        assertThrows(UnsupportedOperationException.class,
                () -> repo.getUsers().put("username", new User("username", "password")),
                "addUser should throw for null user");
    }

    @Test
    void testGetUserReturnsNullForNotAdded() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        assertNull(repo.getUser("some-username"),
                "Should return null for non-existent user.");
    }

    @Test
    void addUser() throws UserAlreadyExistsException {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        User user = new User("username", "password");
        repo.addUser(user);

        assertEquals(user, repo.getUser("username"),
                "Should contain the newly added user.");
    }

    @Test
    void addUserAlreadyExistsThrows() {
        Map<String, User> map = new HashMap<>();
        User user = new User("username", "password");
        map.put("username", user);
        InMemoryUserRepository repo = new InMemoryUserRepository(map);

        assertThrows(UserAlreadyExistsException.class,
                () -> repo.addUser(user),
                "Should throw if user with the same username already exists.");
    }
}
