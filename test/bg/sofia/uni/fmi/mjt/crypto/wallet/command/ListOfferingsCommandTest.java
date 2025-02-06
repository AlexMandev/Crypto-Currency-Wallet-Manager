package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListOfferingsCommandTest {

    private static SelectionKey selectionKey;
    private static AssetsCatalog assetsCatalog;
    private ListOfferingsCommand listOfferingsCommand;

    @BeforeEach
    void setUp() {
        selectionKey = mock();
        assetsCatalog = mock();
    }

    @Test
    void testSuccessfulExecution() {
        when(assetsCatalog.toString()).thenReturn("BTC: $40000, ETH: $2500");

        listOfferingsCommand = new ListOfferingsCommand(new String[]{}, selectionKey, assetsCatalog);
        String result = listOfferingsCommand.execute();

        assertEquals("BTC: $40000, ETH: $2500", result, "Should return the correct offerings list.");
    }

    @Test
    void testInvalidArguments() {
        String[] arguments = {"invalid"};
        assertThrows(CommandArgumentCountException.class,
                () -> new ListOfferingsCommand(arguments, selectionKey, assetsCatalog),
                "Should throw exception for unexpected arguments.");
    }

    @Test
    void testNullAssetsCatalog() {
        assertThrows(IllegalArgumentException.class,
                () -> new ListOfferingsCommand(new String[]{}, selectionKey, null),
                "Should throw exception if AssetsCatalog is null.");
    }
}
