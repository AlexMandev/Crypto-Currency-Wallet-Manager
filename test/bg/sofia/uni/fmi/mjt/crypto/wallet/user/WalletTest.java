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

        assertEquals(initialBalance + depositAmount, wallet.getBalance());
    }

    @Test
    void testDepositNegative() {
        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(-1000.0));
    }

    @Test
    void testWithdraw() throws InsufficientBalanceException {
        wallet.deposit(1000.0);
        double withdrawAmount = 500.0;

        wallet.withdraw(withdrawAmount);

        assertEquals(500.0, wallet.getBalance());
    }

    @Test
    void testWithdrawNegative() {
        assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(-500.0));
    }

    @Test
    void testWithdrawInsufficient() {
        assertThrows(InsufficientBalanceException.class, () -> wallet.withdraw(1000.0));
    }

    @Test
    void testBuy() throws InsufficientBalanceException, UnavailableAssetException {
        wallet.deposit(2000.0);

        wallet.buy("BTC", 1000.0, catalogMock);

        assertEquals(1000.0, wallet.getBalance());
    }

    @Test
    void testBuyAddsAsset() throws InsufficientBalanceException, UnavailableAssetException {
        wallet.deposit(2000.0);

        wallet.buy("BTC", 1000.0, catalogMock);

        assertTrue(wallet.getSummary().ownedAssets().containsKey(asset));
    }

    @Test
    void testBuyNegativeAmount() {
        wallet.deposit(1000.0);

        assertThrows(IllegalArgumentException.class, () -> wallet.buy("BTC", -500.0, catalogMock));
    }

    @Test
    void testBuyInsufficientFunds() {
        assertThrows(InsufficientBalanceException.class, () -> wallet.buy("BTC", 1000.0, catalogMock));
    }

    @Test
    void testBuyUnavailableAsset() {
        wallet.deposit(1000.0);
        when(catalogMock.findById("BTC")).thenReturn(null);

        assertThrows(UnavailableAssetException.class, () -> wallet.buy("BTC", 500.0, catalogMock));
    }

    @Test
    void testBuyNullAssetId() {
        assertThrows(IllegalArgumentException.class, () -> wallet.buy(null, 100.0, catalogMock));
    }

    @Test
    void testBuyNullCatalog() {
        assertThrows(IllegalArgumentException.class, () -> wallet.buy("BTC", 100.0, null));
    }

    @Test
    void testSell() throws InsufficientBalanceException, UnavailableAssetException, AssetNotOwnedException {
        wallet.deposit(1000.0);
        wallet.buy("BTC", 500.0, catalogMock);
        double initialBalance = wallet.getBalance();

        wallet.sell("BTC", catalogMock);

        assertTrue(wallet.getBalance() > initialBalance);
    }

    @Test
    void testSellRemovesAsset() throws InsufficientBalanceException, UnavailableAssetException, AssetNotOwnedException {
        wallet.deposit(1000.0);
        wallet.buy("BTC", 500.0, catalogMock);

        wallet.sell("BTC", catalogMock);

        assertTrue(wallet.getSummary().ownedAssets().isEmpty());
    }

    @Test
    void testSellNonexistentAsset() {
        assertThrows(AssetNotOwnedException.class, () -> wallet.sell("BTC", catalogMock));
    }

    @Test
    void testSellNullAssetId() {
        assertThrows(IllegalArgumentException.class, () -> wallet.sell(null, catalogMock));
    }

    @Test
    void testSellNullCatalog() {
        assertThrows(IllegalArgumentException.class, () -> wallet.sell("BTC", null));
    }

    @Test
    void testSellCatalogReturnsNull() throws InsufficientBalanceException, UnavailableAssetException {
        wallet.deposit(1000.0);
        wallet.buy("BTC", 500.0, catalogMock);
        when(catalogMock.findById("BTC")).thenReturn(null);

        assertThrows(UnavailableAssetException.class, () -> wallet.sell("BTC", catalogMock));
    }

    @Test
    void testOverallWalletSummary() throws InsufficientBalanceException, UnavailableAssetException {
        wallet.deposit(1000.0);
        wallet.buy("BTC", 500.0, catalogMock);
        when(catalogMock.findById("BTC")).thenReturn(new Asset("BTC", "Bitcoin", 60000.0, 1));

        OverallWalletSummary summary = wallet.getOverallSummary(catalogMock);

        assertEquals(1, summary.assetProfits().size());
        assertTrue(summary.assetProfits().containsKey(asset));
    }
}
