package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class DepositCommandTest {

    private static User user;
    private static SelectionKey selectionKey;
    private static Wallet wallet;

    @BeforeEach
    void setUp() {
        user = mock();
        selectionKey = mock();
        wallet = mock();
        when(selectionKey.attachment()).thenReturn(user);
        when(user.getWallet()).thenReturn(wallet);
        when(wallet.getBalance()).thenReturn(1000.0);
    }

    @Test
    void testSuccessfulDeposit() {
        String[] arguments = {"500"};
        Command depositCommand = new DepositCommand(arguments, selectionKey);

        String result = depositCommand.execute();

        assertTrue(result.contains("Deposit successful."), "The deposit should be successful.");
        verify(wallet, times(1)).deposit(500);
    }

    @Test
    void testInsufficientArguments() {
        String[] arguments = {};
        assertThrows(CommandArgumentCountException.class, () -> new DepositCommand(arguments, selectionKey),
                "Should throw for insufficient arguments.");
    }

    @Test
    void testInvalidAmount() {
        String[] arguments = {"invalid"};
        assertThrows(InvalidCommandArgumentException.class, () -> new DepositCommand(arguments, selectionKey),
                "Should throw for invalid deposit amount.");
    }

    @Test
    void testNegativeAmount() {
        String[] arguments = {"-100"};
        assertThrows(InvalidCommandArgumentException.class, () -> new DepositCommand(arguments, selectionKey),
                "Should throw for negative deposit amount.");
    }

    @Test
    void testMoreArguments() {
        String[] arguments = {"500", "extraArg"};
        assertThrows(CommandArgumentCountException.class, () -> new DepositCommand(arguments, selectionKey),
                "Should throw for more arguments.");
    }
}
