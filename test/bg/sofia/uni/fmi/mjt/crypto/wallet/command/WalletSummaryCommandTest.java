package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.Wallet;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.WalletSummary;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletSummaryCommandTest {

    private static SelectionKey selectionKey;
    private static User user;
    private static Wallet wallet;
    private static WalletSummaryCommand walletSummaryCommand;
    private static WalletSummary walletSummary;

    @BeforeAll
    static void setUp() {
        selectionKey = mock();
        user = mock();
        wallet = mock();
        walletSummary = mock();

        when(selectionKey.attachment()).thenReturn(user);
        when(user.getWallet()).thenReturn(wallet);
    }

    @Test
    void testSuccessfulSummary() {
        when(wallet.getSummary()).thenReturn(walletSummary);
        when(walletSummary.toString()).thenReturn("summary");

        String[] arguments = {};
        walletSummaryCommand = new WalletSummaryCommand(arguments, selectionKey);

        String result = walletSummaryCommand.execute();

        assertEquals("summary", result,
                "Should return the wallet summary.");
    }

    @Test
    void testNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);

        String[] arguments = {};
        walletSummaryCommand = new WalletSummaryCommand(arguments, selectionKey);

        String result = walletSummaryCommand.execute();

        assertEquals("You are not logged in. Please register or login first.", result,
                "Should return a message indicating the user is not logged in.");
    }

    @Test
    void testInvalidArgumentCount() {
        String[] arguments = {"extraArg"};

        assertThrows(CommandArgumentCountException.class,
                () -> new WalletSummaryCommand(arguments, selectionKey),
                "Should throw an exception for too many arguments.");
    }
}
