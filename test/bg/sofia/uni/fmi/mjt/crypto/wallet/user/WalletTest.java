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

    private static final String BTC_ID = "BTC";
    private static final double BTC_PRICE = 10;
    private static final Asset BTC_ASSET = new Asset(BTC_ID, "Bitcoin", BTC_PRICE, 1);

    private Wallet wallet;
    private static AssetsCatalog assetsCatalog;

    @BeforeAll
    static void setupMocks() {
        assetsCatalog = mock();
        when(assetsCatalog.findById(BTC_ID)).thenReturn(BTC_ASSET);
    }

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
    }

    @Test
    void testInitialBalanceIsZero() {
        assertEquals(0.0, wallet.getBalance(), "Initial wallet balance should be zero.");
    }

    @Test
    void testDepositIncreasesBalance() {
        wallet.deposit(1000.0);
        assertEquals(1000.0, wallet.getBalance(),
                "Balance should be updated after deposit.");
    }

    @Test
    void testDepositNegativeAmountThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(-100.0),
                "Deposit should throw for invalid dollar amount.");
    }

    @Test
    void testSellUnavailableAsset() {
        when(assetsCatalog.findById("AssetId")).thenReturn(null);
        assertThrows(UnavailableAssetException.class,
                () -> wallet.sell("AssetId", assetsCatalog),
                "Should throw if asset is unavailable.");
    }

    @Test
    void testBuyNullCatalog() {
        assertThrows(IllegalArgumentException.class,
                () -> wallet.buy("id", 10.0, null),
                "Should throw if assetsCatalog is null.");
    }

    @Test
    void testBuyNullAssetId() {
        assertThrows(IllegalArgumentException.class,
                () -> wallet.buy(null, 10.0, assetsCatalog),
                "Should throw if assetId is null.");
    }

    @Test
    void testBuyAssetSuccessfully() throws InsufficientBalanceException, UnavailableAssetException {
        wallet.deposit(1000.00);
        wallet.buy(BTC_ID, 500.00, assetsCatalog);

        WalletSummary walletSummary = wallet.getSummary();

        assertEquals(500, wallet.getBalance(), "Balance should be reduced after buying.");
        assertTrue(walletSummary.ownedAssets().containsKey(BTC_ASSET),
                "Asset should be owned after buying.");
        assertEquals(500 / assetsCatalog.findById(BTC_ID).getPrice(),
                walletSummary.ownedAssets().get(BTC_ASSET),
                "Asset should be owned for exact amount.");
    }

    @Test
    void testBuyInsufficientBalanceThrowsException() {
        assertThrows(InsufficientBalanceException.class,
                () -> wallet.buy(BTC_ID, 1000.0, assetsCatalog),
                "Buy should throw if balance is insufficient.");
    }

    @Test
    void testBuyUnavailableAssetThrowsException() {
        wallet.deposit(1000.0);
        when(assetsCatalog.findById("ETH")).thenReturn(null);
        assertThrows(UnavailableAssetException.class,
                () -> wallet.buy("ETH", 1000.0, assetsCatalog),
                "Buy should throw if asset is not available.");
    }

    @Test
    void testSellAssetSuccessfully() throws InsufficientBalanceException, UnavailableAssetException,
            AssetNotOwnedException {
        wallet.deposit(100.00);
        wallet.buy(BTC_ID, 50.00, assetsCatalog);

        double balanceBeforeSell = wallet.getBalance();
        wallet.sell(BTC_ID, assetsCatalog);
        assertTrue(wallet.getSummary().ownedAssets().isEmpty(),
                "Wallet should not contain sold asset.");
        assertTrue(wallet.getBalance() > balanceBeforeSell,
                "Balance should increase after selling.");
    }

    @Test
    void testSellNonOwnedAssetThrowsException() {
        assertThrows(AssetNotOwnedException.class, () -> wallet.sell(BTC_ID, assetsCatalog),
                "Sell should throw if asset is not owned.");
    }

    @Test
    void testGetOverallSummary() throws InsufficientBalanceException, UnavailableAssetException {
        wallet.deposit(100.00);
        wallet.buy(BTC_ID, 50.00, assetsCatalog);

        when(assetsCatalog.findById(BTC_ID))
                .thenReturn(new Asset(BTC_ID, "Bitcoin", 20.00, 1));

        OverallWalletSummary summary = wallet.getOverallSummary(assetsCatalog);
        assertTrue(summary.assetProfits().get(BTC_ASSET) > 0,
                "Profit should be positive if asset price increased.");
    }

    @Test
    void testSellRemovesBuyHistory() throws InsufficientBalanceException, UnavailableAssetException, AssetNotOwnedException {
        wallet.deposit(100.00);
        wallet.buy(BTC_ID, 50.00, assetsCatalog);

        wallet.sell(BTC_ID, assetsCatalog);
        OverallWalletSummary summary = wallet.getOverallSummary(assetsCatalog);
        assertTrue(summary.assetProfits().isEmpty(),
                "Buy history should be removed after selling.");
    }

    @Test
    void testWithdrawInsufficientBalance() {
        assertThrows(InsufficientBalanceException.class,
                () -> wallet.withdraw(100.00),
                "Should throw if balance is insufficient.");
    }

    @Test
    void testWithdrawInvalidAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> wallet.withdraw(-100.00),
                "Should throw if dollar amount is invalid.");
    }

    @Test
    void testWithdraw() throws InsufficientBalanceException {
        wallet.deposit(100.00);
        wallet.withdraw(50.00);
        assertEquals(50.00, wallet.getBalance(),
                "Balance should be updated after withdrawing.");
    }
}