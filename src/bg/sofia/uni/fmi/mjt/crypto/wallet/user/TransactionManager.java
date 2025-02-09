package bg.sofia.uni.fmi.mjt.crypto.wallet.user;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.AssetNotOwnedException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TransactionManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 4405006780330102317L;

    private final Map<Asset, Double> ownedAssets;
    private final Map<Asset, Double> buyHistory;
    private static final double INITIAL_AMOUNT = 0.0;

    public TransactionManager(Map<Asset, Double> ownedAssets, Map<Asset, Double> buyHistory) {
        this.ownedAssets = ownedAssets;
        this.buyHistory = buyHistory;
    }

    public double buy(Asset asset, double dollarAmount, double currentPrice) {
        double amountBought = dollarAmount / currentPrice;
        ownedAssets.put(asset, ownedAssets.getOrDefault(asset, INITIAL_AMOUNT) + amountBought);
        buyHistory.put(asset, buyHistory.getOrDefault(asset, INITIAL_AMOUNT) + dollarAmount);
        return amountBought;
    }

    public double sell(Asset asset, double currentPrice) throws AssetNotOwnedException {
        if (!ownedAssets.containsKey(asset) || ownedAssets.get(asset) == 0.0) {
            throw new AssetNotOwnedException("Wallet doesn't contain this asset.");
        }

        double soldAmount = ownedAssets.get(asset);
        double income = soldAmount * currentPrice;

        ownedAssets.remove(asset);
        buyHistory.remove(asset);

        return income;
    }

    public Map<Asset, Double> calculateProfits(AssetsCatalog catalog) {
        Map<Asset, Double> assetProfits = new HashMap<>();

        for (var entry : ownedAssets.entrySet()) {
            Asset asset = catalog.findById(entry.getKey().getAssetId());
            if (asset == null) {
                continue;
            }

            double currentPrice = asset.getPrice();
            double amountOwned = entry.getValue();
            double totalSpent = buyHistory.get(asset);

            double currentValue = amountOwned * currentPrice;
            double profitOrLoss = currentValue - totalSpent;

            if (Math.abs(profitOrLoss) < Wallet.DELTA) {
                profitOrLoss = 0.0;
            }

            assetProfits.put(asset, profitOrLoss);
        }

        return assetProfits;
    }
}
