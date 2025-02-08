package bg.sofia.uni.fmi.mjt.crypto.wallet.user;

import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WalletSummaryTest {
    @Test
    void testToString() {
        Map<Asset, Double> assetProfits = new HashMap<>();
        Asset asset = new Asset("BTC", "Bitcoin", 10.00, 1);
        assetProfits.put(asset, 1.0);
        WalletSummary ws = new WalletSummary(10.0, assetProfits);
        String print = ws.toString();

        assertTrue(print.contains("Bitcoin"), "Should mention owned assets");
        assertTrue(print.contains("10.0"), "Should mention current balance.");
    }
}
