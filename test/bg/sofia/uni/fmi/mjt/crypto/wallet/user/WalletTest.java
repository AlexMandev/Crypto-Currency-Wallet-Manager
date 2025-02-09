package bg.sofia.uni.fmi.mjt.crypto.wallet.user;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.AssetNotOwnedException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnavailableAssetException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WalletTest {
    private static AssetsCatalog catalogMock;
    private static Asset asset;

    private Wallet wallet;

    @BeforeAll
    static void setMocks() {
        asset = new Asset("BTC", "Bitcoin", 50000.00, 1);
        catalogMock = mock();
    }

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        when(catalogMock.findById("BTC")).thenReturn(asset);
    }

    @Test
    void testDeposit() {
        double initialBalance = wallet.getBalance();
        double depositAmount = 1000.0;

        wallet.deposit(depositAmount);

        assertEquals(initialBalance + depositAmount, wallet.getBalance(),
                "Deposit should add the dollar amount to the balance.");
    }

    @Test
    void testDepositNegative() {
        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(-1000.0),
                "Deposit should throw for invalid amount.");
    }

    @Test
    void testWithdraw() throws InsufficientBalanceException {
        wallet.deposit(1000.0);
        double withdrawAmount = 500.0;

        wallet.withdraw(withdrawAmount);

        assertEquals(500.0, wallet.getBalance(),
                "Withdraw should decrease the wallet's balance.");
    }

    @Test
    void testWithdrawNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> wallet.withdraw(-500.0),
                "Withdraw should throw for invalid amount.");
    }

    @Test
    void testWithdrawInsufficient() {
        assertThrows(InsufficientBalanceException.class,
                () -> wallet.withdraw(1000.0),
                "Withdraw should throw if the balance isn't enough.");
    }

    @Test
    void testBuy() throws InsufficientBalanceException, UnavailableAssetException {
        wallet.deposit(2000.0);

        wallet.buy("BTC", 1000.0, catalogMock);

        assertEquals(1000.0, wallet.getBalance(),
                "Buy should decrease the wallet's balance.");
    }

    @Test
    void testBuyAddsAsset() throws InsufficientBalanceException, UnavailableAssetException {
        wallet.deposit(2000.0);

        wallet.buy("BTC", 1000.0, catalogMock);

        assertTrue(wallet.getSummary().ownedAssets().containsKey(asset),
                "Buy should add the asset to the owned assets.");
    }

    @Test
    void testBuyNegativeAmount() {
        wallet.deposit(1000.0);

        assertThrows(IllegalArgumentException.class,
                () -> wallet.buy("BTC", -500.0, catalogMock),
                "Buy should throw for negative amount.");
    }

    @Test
    void testBuyInsufficientFunds() {
        assertThrows(InsufficientBalanceException.class,
                () -> wallet.buy("BTC", 1000.0, catalogMock),
                "Buy should throw for insufficient balance.");
    }

    @Test
    void testBuyUnavailableAsset() {
        wallet.deposit(1000.0);
        when(catalogMock.findById("BTC")).thenReturn(null);

        assertThrows(UnavailableAssetException.class,
                () -> wallet.buy("BTC", 500.0, catalogMock),
                "Buy should throw if asset is null.");
    }

    @Test
    void testBuyNullAssetId() {
        assertThrows(IllegalArgumentException.class,
                () -> wallet.buy(null, 100.0, catalogMock),
                "Buy should throw if asset id is null.");
    }

    @Test
    void testBuyNullCatalog() {
        assertThrows(IllegalArgumentException.class,
                () -> wallet.buy("BTC", 100.0, null),
                "Buy should throw if catalog is null.");
    }

    @Test
    void testSell() throws InsufficientBalanceException, UnavailableAssetException, AssetNotOwnedException {
        wallet.deposit(1000.0);
        wallet.buy("BTC", 500.0, catalogMock);
        double initialBalance = wallet.getBalance();

        wallet.sell("BTC", catalogMock);

        assertTrue(wallet.getBalance() > initialBalance,
                "Sell should add to the wallet's balance.");
    }

    @Test
    void testSellRemovesAsset() throws InsufficientBalanceException, UnavailableAssetException, AssetNotOwnedException {
        wallet.deposit(1000.0);
        wallet.buy("BTC", 500.0, catalogMock);

        wallet.sell("BTC", catalogMock);

        assertTrue(wallet.getSummary().ownedAssets().isEmpty(),
                "Sell should remove the owned asset.");
    }

    @Test
    void testSellNonexistentAsset() {
        assertThrows(AssetNotOwnedException.class,
                () -> wallet.sell("BTC", catalogMock),
                "Sell should throw if asset isn't owned.");
    }

    @Test
    void testSellNullAssetId() {
        assertThrows(IllegalArgumentException.class,
                () -> wallet.sell(null, catalogMock),
                "Sell should throw if asset id is null.");
    }

    @Test
    void testSellNullCatalog() {
        assertThrows(IllegalArgumentException.class,
                () -> wallet.sell("BTC", null),
                "Sell should throw if catalog is null.");
    }

    @Test
    void testSellCatalogReturnsNull() throws InsufficientBalanceException, UnavailableAssetException {
        wallet.deposit(1000.0);
        wallet.buy("BTC", 500.0, catalogMock);
        when(catalogMock.findById("BTC")).thenReturn(null);

        assertThrows(UnavailableAssetException.class,
                () -> wallet.sell("BTC", catalogMock),
                "Sell should throw if asset is unavailable.");
    }

    @Test
    void testOverallWalletSummary() throws InsufficientBalanceException, UnavailableAssetException {
        wallet.deposit(1000.0);
        wallet.buy("BTC", 500.0, catalogMock);
        when(catalogMock.findById("BTC")).thenReturn(new Asset("BTC", "Bitcoin", 60000.0, 1));

        OverallWalletSummary summary = wallet.getOverallSummary(catalogMock);

        assertEquals(1, summary.assetProfits().size(),
                "One asset should be owned.");
        assertTrue(summary.assetProfits().containsKey(asset),
                "The bought asset should be in the profits map.");
    }
}
