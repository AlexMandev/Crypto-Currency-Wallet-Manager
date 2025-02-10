package bg.sofia.uni.fmi.mjt.crypto.wallet.user;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "securePassword";
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(USERNAME, PASSWORD);
    }

    @Test
    void testPasswordHashing() {
        assertTrue(user.matchesPassword(PASSWORD),
                "Password should match the stored hash.");
        assertFalse(user.matchesPassword("wrongPassword"),
                "Incorrect password should not match.");
    }

    @Test
    void testEqualsSameUserName() {
        User sameUser = new User(USERNAME, "anotherPassword");
        assertEquals(user, sameUser,
                "Users with the same username should be equal.");
        assertEquals(user.hashCode(), sameUser.hashCode(),
                "Equal users should have the same hash code.");
    }

    @Test
    void testEqualityDifferentUsername() {
        User differentUser = new User("otherUser", PASSWORD);
        assertNotEquals(user, differentUser,
                "Users with different usernames should not be equal.");
    }

    @Test
    void testNullPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("username", null),
                "Constructor should throw for null password.");
    }

    @Test
    void testNullUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> new User(null, "password"),
                "Constructor should throw for null username");
    }

    @Test
    void testMatchesPasswordNullPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> user.matchesPassword(null),
                "Matches password should throw for null argument.");
    }
}