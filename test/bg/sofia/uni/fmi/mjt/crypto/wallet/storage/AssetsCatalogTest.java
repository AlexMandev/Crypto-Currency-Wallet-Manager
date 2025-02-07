package bg.sofia.uni.fmi.mjt.crypto.wallet.storage;


import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssetsCatalogTest {

    private static final String BTC_ID = "BTC";
    private static final String ETH_ID = "ETH";
    private static final String NON_EXISTENT_ID = "DOGE";

    private AssetsCatalog assetsCatalog;
    private final Asset btcAsset = new Asset(BTC_ID, "Bitcoin", 50000.0, 1);
    private final Asset ethAsset = new Asset(ETH_ID, "Ethereum", 3500.0, 1);

    @BeforeEach
    void setUp() {
        assetsCatalog = new AssetsCatalog();
    }

    @Test
    void testUpdateCatalogAddsCryptoAssets() {
        assetsCatalog.updateCatalog(List.of(btcAsset, ethAsset));

        assertEquals(btcAsset, assetsCatalog.findById(BTC_ID), "BTC should be in the catalog.");
        assertEquals(ethAsset, assetsCatalog.findById(ETH_ID), "ETH should be in the catalog.");
    }

    @Test
    void testUpdateCatalogIgnoresNonCryptoAssets() {
        Asset stock = new Asset("MSFT", "Microsoft", 150.0, 0);

        assetsCatalog.updateCatalog(List.of(btcAsset, stock));

        assertEquals(btcAsset, assetsCatalog.findById(BTC_ID), "BTC should be in the catalog.");
        assertNull(assetsCatalog.findById("MSFT"), "Non-crypto assets should not be added.");
    }

    @Test
    void testFindByIdReturnsNullForNonExistentAsset() {
        assetsCatalog.updateCatalog(List.of(btcAsset, ethAsset));

        assertNull(assetsCatalog.findById(NON_EXISTENT_ID), "Should return null for non-existent asset.");
    }

    @Test
    void testUpdateCatalogThrowsForNull() {
        assertThrows(IllegalArgumentException.class, () -> assetsCatalog.updateCatalog(null),
                "UpdateCatalog should throw for null assets list.");
    }
}
