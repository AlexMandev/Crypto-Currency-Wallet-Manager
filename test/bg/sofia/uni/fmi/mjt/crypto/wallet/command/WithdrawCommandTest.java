package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WithdrawCommandTest {

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
    void testSuccessfulWithdraw() throws InsufficientBalanceException {
        String[] arguments = {"500"};
        Command withdrawCommand = new WithdrawCommand(arguments, selectionKey);

        String result = withdrawCommand.execute();

        assertTrue(result.contains("Withdrawal successful."), "The withdrawal should be successful.");
        verify(wallet, times(1)).withdraw(500);
    }

    @Test
    void testInsufficientArguments() {
        String[] arguments = {};
        assertThrows(CommandArgumentCountException.class, () -> new WithdrawCommand(arguments, selectionKey),
                "Should throw for insufficient arguments.");
    }

    @Test
    void testInvalidAmount() {
        String[] arguments = {"invalid"};
        assertThrows(InvalidCommandArgumentException.class, () -> new WithdrawCommand(arguments, selectionKey),
                "Should throw for invalid withdrawal amount.");
    }

    @Test
    void testNegativeAmount() {
        String[] arguments = {"-100"};
        assertThrows(InvalidCommandArgumentException.class, () -> new WithdrawCommand(arguments, selectionKey),
                "Should throw for negative withdrawal amount.");
    }

    @Test
    void testMoreArguments() {
        String[] arguments = {"500", "extraArg"};
        assertThrows(CommandArgumentCountException.class, () -> new WithdrawCommand(arguments, selectionKey),
                "Should throw for more arguments.");
    }

    @Test
    void testInsufficientBalance() throws InsufficientBalanceException {
        String[] arguments = {"2000"};
        doThrow(new InsufficientBalanceException("Insufficient funds"))
                .when(wallet).withdraw(2000);

        Command withdrawCommand = new WithdrawCommand(arguments, selectionKey);
        String result = withdrawCommand.execute();

        assertTrue(result.contains("Insufficient funds"), "Should return insufficient balance message.");
    }

    @Test
    void testNullArguments() {
        assertThrows(IllegalArgumentException.class,
                () ->new WithdrawCommand(null, selectionKey),
                "Constructor should throw for null arguments array.");
    }

    @Test
    void testNullSelectionKey() {
        assertThrows(IllegalArgumentException.class,
                () ->new WithdrawCommand(new String[]{}, null),
                "Constructor should throw for null selection key.");
    }
}