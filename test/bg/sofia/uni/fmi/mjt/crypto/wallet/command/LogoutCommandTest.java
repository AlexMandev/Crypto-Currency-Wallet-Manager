package bg.sofia.uni.fmi.mjt.crypto.wallet.command;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class LogoutCommandTest {

    private static SelectionKey selectionKey;
    private LogoutCommand logoutCommand;

    @BeforeEach
    void setUp() {
        selectionKey = mock();
    }

    @Test
    void testSuccessfulLogout() {
        User user = mock();
        when(selectionKey.attachment()).thenReturn(user);

        logoutCommand = new LogoutCommand(new String[]{}, selectionKey);
        String result = logoutCommand.execute();

        assertEquals("You have logged out successfully from your account.", result,
                "Logout message should indicate successful logout.");
        verify(selectionKey).attach(null);
    }

    @Test
    void testLogoutWhenNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);

        logoutCommand = new LogoutCommand(new String[]{}, selectionKey);
        String result = logoutCommand.execute();

        assertEquals("You are not logged in. Please register or login first.", result,
                "Should return not logged in message when user is not logged in.");
    }

    @Test
    void testInvalidArguments() {
        String[] arguments = {"extraArg"};
        assertThrows(CommandArgumentCountException.class, () -> new LogoutCommand(arguments, selectionKey),
                "Should throw for invalid number of arguments.");
    }
}