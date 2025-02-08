package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnavailableAssetException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BuyCommandTest {

    private static AssetsCatalog assetsCatalog;
    private static User user;
    private static SelectionKey selectionKey;
    private static Wallet wallet;

    private BuyCommand buyCommand;

    @BeforeEach
    void setUp() {
        assetsCatalog = mock();
        user = mock();
        selectionKey = mock();
        wallet = mock();
        when(selectionKey.attachment()).thenReturn(user);
        when(user.getWallet()).thenReturn(wallet);
    }

    @Test
    void testSuccessfulPurchase() throws InsufficientBalanceException, UnavailableAssetException {
        String[] arguments = {"assetId", "100"};
        buyCommand = new BuyCommand(arguments, selectionKey, assetsCatalog);

        String result = buyCommand.execute();

        assertTrue(result.contains("Purchase successful."),
                "The purchase should be successful.");
        verify(wallet, times(1)).buy("assetId", 100, assetsCatalog);
    }

    @Test
    void testInsufficientArguments() {
        String[] arguments = {"assetId"};
        assertThrows(CommandArgumentCountException.class, () -> new BuyCommand(arguments, selectionKey, assetsCatalog),
                "Should throw for insufficient arguments.");
    }

    @Test
    void testInvalidDollarAmount() {
        String[] arguments = {"assetId", "invalid"};
        assertThrows(InvalidCommandArgumentException.class, () -> new BuyCommand(arguments, selectionKey, assetsCatalog),
                "Should throw for invalid dollar amount.");
    }

    @Test
    void testMoreArguments() {
        String[] arguments = {"assetId", "argument2", "argument3"};
        assertThrows(CommandArgumentCountException.class, () -> new BuyCommand(arguments, selectionKey, assetsCatalog),
                "Should throw for more arguments.");
    }

    @Test
    void testUserNotLoggedIn() {
        when(selectionKey.attachment()).thenReturn(null);

        String[] arguments = {"assetId", "100"};
        buyCommand = new BuyCommand(arguments, selectionKey, assetsCatalog);

        String result = buyCommand.execute();

        assertEquals(Command.NOT_LOGGED_IN_MESSAGE, result,
                "Should return 'Not logged in' message when user is not logged.");
    }

    @Test
    void testInsufficientBalance() throws InsufficientBalanceException, UnavailableAssetException {
        doThrow(new InsufficientBalanceException("Insufficient balance")).when(wallet)
                .buy("assetId", 100, assetsCatalog);

        String[] arguments = {"assetId", "100"};
        buyCommand = new BuyCommand(arguments, selectionKey, assetsCatalog);

        String result = buyCommand.execute();

        assertTrue(result.contains("Insufficient balance to complete the purchase."),
                "Should return an error message for insufficient balance.");
    }

    @Test
    void testUnavailableAsset() throws InsufficientBalanceException, UnavailableAssetException {
        doThrow(new UnavailableAssetException("Asset unavailable")).when(wallet)
                .buy("assetId", 100, assetsCatalog);

        String[] arguments = {"assetId", "100"};
        buyCommand = new BuyCommand(arguments, selectionKey, assetsCatalog);

        String result = buyCommand.execute();

        assertTrue(result.contains("The asset with the given ID is unavailable."),
                "Should return an error message for unavailable asset.");
    }
}