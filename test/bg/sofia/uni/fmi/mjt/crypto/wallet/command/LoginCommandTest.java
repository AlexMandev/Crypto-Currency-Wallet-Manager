package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.UserRepository;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class LoginCommandTest {

    private static SelectionKey selectionKey;
    private static UserRepository userRepository;
    private static User user;
    private LoginCommand loginCommand;

    @BeforeEach
    void setUp() {
        selectionKey = mock();
        userRepository = mock();
        user = mock();
    }

    @Test
    void testSuccessfulLogin() {
        String[] arguments = {"validUser", "correctPassword"};
        when(userRepository.getUser("validUser")).thenReturn(user);
        when(user.matchesPassword("correctPassword")).thenReturn(true);

        loginCommand = new LoginCommand(arguments, selectionKey, userRepository);
        String result = loginCommand.execute();

        assertEquals("You have logged in successfully.", result, "Should return success message.");
        verify(selectionKey, times(1)).attach(user);
    }

    @Test
    void testAlreadyLoggedIn() {
        when(selectionKey.attachment()).thenReturn(user);

        loginCommand = new LoginCommand(new String[]{"validUser", "correctPassword"}, selectionKey, userRepository);
        String result = loginCommand.execute();

        assertEquals("You are already logged in.", result, "Should return already logged in message.");
    }

    @Test
    void testUsernameNotFound() {
        when(userRepository.getUser("unknownUser")).thenReturn(null);

        loginCommand = new LoginCommand(new String[]{"unknownUser", "somePassword"}, selectionKey, userRepository);
        String result = loginCommand.execute();

        assertEquals("Username not found. Please try again.", result,
                "Should return username not found message.");
    }

    @Test
    void testIncorrectPassword() {
        when(userRepository.getUser("validUser")).thenReturn(user);
        when(user.matchesPassword("wrongPassword")).thenReturn(false);

        loginCommand = new LoginCommand(new String[]{"validUser", "wrongPassword"}, selectionKey, userRepository);
        String result = loginCommand.execute();

        assertEquals("Incorrect password.", result, "Should return incorrect password message.");
    }

    @Test
    void testNoArguments() {
        assertThrows(CommandArgumentCountException.class,
                () -> new LoginCommand(new String[]{}, selectionKey, userRepository),
                "Should throw for missing arguments.");
    }

    @Test
    void testOnlyUsernameProvided() {
        assertThrows(CommandArgumentCountException.class,
                () -> new LoginCommand(new String[]{"validUser"}, selectionKey, userRepository),
                "Should throw for missing password.");
    }

    @Test
    void testTooManyArguments() {
        assertThrows(CommandArgumentCountException.class,
                () -> new LoginCommand(new String[]{"user", "pass", "extraArg"}, selectionKey, userRepository),
                "Should throw for too many arguments.");
    }

    @Test
    void testNullUserRepository() {
        assertThrows(IllegalArgumentException.class,
                () -> new LoginCommand(new String[]{"user", "pass"}, selectionKey, null),
                "Should throw if UserRepository is null.");
    }
}
