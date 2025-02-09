package bg.sofia.uni.fmi.mjt.crypto.wallet.user;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.AssetNotOwnedException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionManagerTest {
    private static AssetsCatalog catalogMock;
    private static Asset asset;

    private Map<Asset, Double> ownedAssets;
    private Map<Asset, Double> buyHistory;
    private TransactionManager manager;

    @BeforeAll
    static void setupMocks() {
        catalogMock = mock();
        asset = new Asset("BTC", "Bitcoin", 50000.00, 1);
    }

    @BeforeEach
    void setUp() {
        ownedAssets = new HashMap<>();
        buyHistory = new HashMap<>();
        manager = new TransactionManager(ownedAssets, buyHistory);
    }

    @Test
    void testBuyFirstTime() {
        double dollarAmount = 1000.0;
        double currentPrice = 50000.0;
        double expectedAmount = dollarAmount / currentPrice;

        double result = manager.buy(asset, dollarAmount, currentPrice);

        assertEquals(expectedAmount, result);
    }

    @Test
    void testBuyAddsOwnedAsset() {
        double dollarAmount = 1000.0;
        double currentPrice = 50000.0;
        double expectedAmount = dollarAmount / currentPrice;

        manager.buy(asset, dollarAmount, currentPrice);

        assertEquals(expectedAmount, ownedAssets.get(asset));
    }

    @Test
    void testBuyAddsToHistory() {
        double dollarAmount = 1000.0;
        double currentPrice = 50000.0;

        manager.buy(asset, dollarAmount, currentPrice);

        assertEquals(dollarAmount, buyHistory.get(asset));
    }

    @Test
    void testBuyUpdatesOwnedAmount() {
        ownedAssets.put(asset, 0.1);
        buyHistory.put(asset, 5000.0);
        double dollarAmount = 1000.0;
        double currentPrice = 50000.0;
        double expectedAmount = dollarAmount / currentPrice + 0.1;

        manager.buy(asset, dollarAmount, currentPrice);

        assertEquals(expectedAmount, ownedAssets.get(asset));
    }

    @Test
    void testBuyUpdatesHistory() {
        ownedAssets.put(asset, 0.1);
        buyHistory.put(asset, 5000.0);
        double dollarAmount = 1000.0;

        manager.buy(asset, dollarAmount, 50000.0);

        assertEquals(6000.0, buyHistory.get(asset));
    }

    @Test
    void testSellReturnsCorrectIncome() throws AssetNotOwnedException {
        double ownedAmount = 0.5;
        ownedAssets.put(asset, ownedAmount);
        buyHistory.put(asset, 25000.0);
        double currentPrice = 60000.0;
        double expectedIncome = ownedAmount * currentPrice;

        double result = manager.sell(asset, currentPrice);

        assertEquals(expectedIncome, result);
    }

    @Test
    void testSellRemovesOwnedAsset() throws AssetNotOwnedException {
        ownedAssets.put(asset, 0.5);
        buyHistory.put(asset, 25000.0);

        manager.sell(asset, 60000.0);

        assertFalse(ownedAssets.containsKey(asset));
    }

    @Test
    void testSellRemovesFromHistory() throws AssetNotOwnedException {
        ownedAssets.put(asset, 0.5);
        buyHistory.put(asset, 25000.0);

        manager.sell(asset, 60000.0);

        assertFalse(buyHistory.containsKey(asset));
    }

    @Test
    void testSellNonexistentAsset() {
        assertThrows(AssetNotOwnedException.class,
                () -> manager.sell(asset, 50000.0));
    }

    @Test
    void testCalculateProfits() {
        ownedAssets.put(asset, 0.5);
        buyHistory.put(asset, 20000.0);
        when(catalogMock.findById("BTC")).thenReturn(asset);
        double expectedProfit = (0.5 * 50000.0) - 20000.0;

        Map<Asset, Double> profits = manager.calculateProfits(catalogMock);

        assertEquals(expectedProfit, profits.get(asset));
    }

    @Test
    void testCalculateProfitsAssetNotFound() {
        ownedAssets.put(asset, 0.5);
        buyHistory.put(asset, 20000.0);
        when(catalogMock.findById("BTC")).thenReturn(null);

        Map<Asset, Double> profits = manager.calculateProfits(catalogMock);

        assertTrue(profits.isEmpty());
    }
}