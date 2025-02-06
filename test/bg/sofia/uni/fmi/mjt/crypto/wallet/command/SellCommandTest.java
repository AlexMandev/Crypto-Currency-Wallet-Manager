package bg.sofia.uni.fmi.mjt.crypto.wallet.command;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.AssetNotOwnedException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnavailableAssetException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.Wallet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SellCommandTest {

    private static SelectionKey selectionKey;
    private static AssetsCatalog assetsCatalog;
    private static User user;
    private static SellCommand sellCommand;
    private static Wallet wallet;

    @BeforeAll
    static void setUp() {
        selectionKey = mock();
        assetsCatalog = mock();
        user = mock();
        wallet = mock();

        when(selectionKey.attachment()).thenReturn(user);
        when(user.getWallet()).thenReturn(wallet);
    }

    @Test
    void testSuccessfulSell() throws AssetNotOwnedException, UnavailableAssetException {
        String[] arguments = {"validAssetId"};
        sellCommand = new SellCommand(arguments, selectionKey, assetsCatalog);

        doNothing().when(wallet).sell(any(), any());

        String result = sellCommand.execute();

        assertTrue(result.contains("Sell successful."),
                "Should return a success message.");
    }

    @Test
    void testAssetNotOwned() throws AssetNotOwnedException, UnavailableAssetException {
        String[] arguments = {"invalidAssetId"};
        sellCommand = new SellCommand(arguments, selectionKey, assetsCatalog);

        doThrow(new AssetNotOwnedException("You do not own this asset.")).when(wallet).sell(any(), any());

        String result = sellCommand.execute();

        assertEquals("You do not own this asset.", result,
                "Should return a message indicating that the asset is not owned.");
    }

    @Test
    void testAssetUnavailable() throws AssetNotOwnedException, UnavailableAssetException {
        String[] arguments = {"validAssetId"};
        sellCommand = new SellCommand(arguments, selectionKey, assetsCatalog);

        doThrow(new UnavailableAssetException("The asset with the given ID is unavailable."))
                .when(wallet).sell(any(), any());

        String result = sellCommand.execute();

        assertEquals("The asset with the given ID is unavailable.", result,
                "Should return a message indicating that the asset is unavailable.");
    }

    @Test
    void testNotLoggedIn() {
        String[] arguments = {"validAssetId"};
        when(selectionKey.attachment()).thenReturn(null);

        sellCommand = new SellCommand(arguments, selectionKey, assetsCatalog);
        String result = sellCommand.execute();

        assertEquals("You are not logged in. Please register or login first.", result,
                "Should return a message indicating the user is not logged in.");
    }

    @Test
    void testNoArguments() {
        String[] arguments = {};

        assertThrows(CommandArgumentCountException.class, () -> new SellCommand(arguments, selectionKey, assetsCatalog),
                "Should throw an exception for missing arguments.");
    }

    @Test
    void testInvalidArgumentCount() {
        String[] arguments = {"validAssetId", "extraArg"};
        assertThrows(CommandArgumentCountException.class, () -> new SellCommand(arguments, selectionKey, assetsCatalog),
                "Should throw an exception for too many arguments.");
    }
}