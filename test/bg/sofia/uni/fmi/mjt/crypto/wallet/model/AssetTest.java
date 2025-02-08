package bg.sofia.uni.fmi.mjt.crypto.wallet.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssetTest {

    @Test
    void testNullAssetId() {
        assertThrows(IllegalArgumentException.class,
                () -> new Asset(null, "Bitcoin", 10.0, 1),
                "Constructor should throw for null asset id.");
    }

    @Test
    void testNullAssetName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Asset("id", null, 10.0, 1),
                "Constructor should throw for null asset name.");
    }

    @Test
    void testAssetNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new Asset("id", "name", -5.0, 1),
                "Constructor should throw for negative price.");
    }
}
