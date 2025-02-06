package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.OverallWalletSummary;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.Wallet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WalletOverallSummaryCommandTest {

    private static SelectionKey selectionKey;
    private static AssetsCatalog assetsCatalog;
    private static User user;
    private static Wallet wallet;
    private static WalletOverallSummaryCommand walletOverallSummaryCommand;
    private static OverallWalletSummary summary;

    @BeforeAll
    static void setUp() {
        selectionKey = mock();
        assetsCatalog = mock();
        user = mock();
        wallet = mock();
        summary = mock();

        when(selectionKey.attachment()).thenReturn(user);
        when(user.getWallet()).thenReturn(wallet);
    }

    @Test
    void testSuccessfulSummary() {
        when(summary.toString()).thenReturn("summary");
        when(wallet.getOverallSummary(assetsCatalog)).thenReturn(summary);

        String[] arguments = {};
        walletOverallSummaryCommand = new WalletOverallSummaryCommand(arguments, selectionKey, assetsCatalog);

        String result = walletOverallSummaryCommand.execute();

        assertEquals("summary", result,
                "Should return the overall summary of the wallet.");
    }

    @Test
    void testNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);

        String[] arguments = {};
        walletOverallSummaryCommand = new WalletOverallSummaryCommand(arguments, selectionKey, assetsCatalog);

        String result = walletOverallSummaryCommand.execute();

        assertEquals("You are not logged in. Please register or login first.", result,
                "Should return a message indicating the user is not logged in.");
    }

    @Test
    void testInvalidArgumentCount() {
        String[] arguments = {"extraArg"};

        assertThrows(CommandArgumentCountException.class,
                () -> new WalletOverallSummaryCommand(arguments, selectionKey, assetsCatalog),
                "Should throw an exception for too many arguments.");
    }
}
