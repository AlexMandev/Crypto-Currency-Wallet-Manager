package bg.sofia.uni.fmi.mjt.crypto.wallet.command;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.UserRepository;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterCommandTest {

    private static SelectionKey selectionKey;
    private static UserRepository userRepository;
    private RegisterCommand registerCommand;

    @BeforeEach
    void setUp() {
        selectionKey = mock();
        userRepository = mock();
    }

    @Test
    void testSuccessfulRegistration() throws UserAlreadyExistsException {
        String[] arguments = {"validUser", "ValidPass123"};
        when(selectionKey.attachment()).thenReturn(null);

        registerCommand = new RegisterCommand(arguments, selectionKey, userRepository);
        String result = registerCommand.execute();

        assertEquals("You have registered successfully.", result,
                "Successful registration message should be returned.");
        verify(userRepository, times(1)).addUser(any(User.class));
    }

    @Test
    void testAlreadyLoggedIn() throws UserAlreadyExistsException {
        when(selectionKey.attachment()).thenReturn(mock(User.class));

        String[] arguments = {"validUser", "ValidPass123"};
        registerCommand = new RegisterCommand(arguments, selectionKey, userRepository);
        String result = registerCommand.execute();

        assertEquals("You are already logged in.", result,
                "Should return an already logged in message.");
        verify(userRepository, never()).addUser(any(User.class));
    }

    @Test
    void testUsernameTooShort() {
        String[] arguments = {"ab", "ValidPass123"};
        assertThrows(InvalidCommandArgumentException.class, () -> new RegisterCommand(arguments, selectionKey, userRepository),
                "Should throw for too short username.");
    }

    @Test
    void testUsernameTooLong() {
        String[] arguments = {"a".repeat(21), "ValidPass123"};
        assertThrows(InvalidCommandArgumentException.class, () -> new RegisterCommand(arguments, selectionKey, userRepository),
                "Should throw for too long username.");
    }

    @Test
    void testInvalidUsernameFormat() {
        String[] arguments = {"_ invalidUser", "ValidPass123"};
        assertThrows(InvalidCommandArgumentException.class, () -> new RegisterCommand(arguments, selectionKey, userRepository),
                "Should throw for invalid username format.");
    }

    @Test
    void testPasswordTooShort() {
        String[] arguments = {"validUser", "short"};
        assertThrows(InvalidCommandArgumentException.class, () -> new RegisterCommand(arguments, selectionKey, userRepository),
                "Should throw for too short password.");
    }

    @Test
    void testPasswordTooLong() {
        String[] arguments = {"validUser", "a".repeat(51)};
        assertThrows(InvalidCommandArgumentException.class, () -> new RegisterCommand(arguments, selectionKey, userRepository),
                "Should throw for too long password.");
    }

    @Test
    void testUserAlreadyExists() throws UserAlreadyExistsException {
        String[] arguments = {"validUser", "ValidPass123"};
        when(selectionKey.attachment()).thenReturn(null);
        doThrow(new UserAlreadyExistsException("User already exists.")).when(userRepository).addUser(any());
        registerCommand = new RegisterCommand(arguments, selectionKey, userRepository);
        String result = registerCommand.execute();

        assertEquals("User already exists.", result,
                "Should return user already exists message.");
    }

    @Test
    void testNoArguments() {
        String[] arguments = {};
        assertThrows(CommandArgumentCountException.class, () -> new RegisterCommand(arguments, selectionKey, userRepository),
                "Should throw for missing arguments.");
    }

    @Test
    void testOnlyUsername() {
        String[] arguments = {"validUser"};
        assertThrows(CommandArgumentCountException.class, () -> new RegisterCommand(arguments, selectionKey, userRepository),
                "Should throw for missing password.");
    }

    @Test
    void testMoreArguments() {
        String[] arguments = {"validUser", "ValidPass123", "extraArg"};
        assertThrows(CommandArgumentCountException.class, () -> new RegisterCommand(arguments, selectionKey, userRepository),
                "Should throw for too many arguments.");
    }
}
