package bg.sofia.uni.fmi.mjt.crypto.wallet.user;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.AssetNotOwnedException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnavailableAssetException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Wallet implements Serializable {
    @Serial
    private static final long serialVersionUID = -7459989033155017236L;
    private static final double INITIAL_BALANCE = 0.0;
    private static final double DELTA = 0.01;

    private double balance;
    private final Map<Asset, Double> ownedAssets;
    private final Map<Asset, Double> buyHistory;

    public Wallet() {
        this.balance = INITIAL_BALANCE;
        this.ownedAssets = new HashMap<>();
        this.buyHistory = new HashMap<>();
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= INITIAL_BALANCE) {
            throw new IllegalArgumentException("Deposit amount should be positive");
        }

        balance += amount;
    }

    public void buy(String assetId, double dollarAmount, AssetsCatalog currentAssetsCatalog)
        throws InsufficientBalanceException, UnavailableAssetException {
        validateAssetId(assetId);
        validateAssetsCatalog(currentAssetsCatalog);
        if (dollarAmount <= INITIAL_BALANCE) {
            throw new IllegalArgumentException("DollarAmount should be positive.");
        }
        if (dollarAmount > balance) {
            throw new InsufficientBalanceException("Not enough funds to buy. Please check the wallet's balance.");
        }

        Asset asset = currentAssetsCatalog.findById(assetId);
        if (asset == null) {
            throw new UnavailableAssetException("The asset with the provided ID is unavailable.");
        }

        double amountBought = dollarAmount / asset.getPrice();
        balance -= dollarAmount;
        ownedAssets.put(asset, ownedAssets.getOrDefault(asset, 0.0) + amountBought);
        buyHistory.put(asset, buyHistory.getOrDefault(asset, 0.0) + dollarAmount);
    }

    public void sell(String assetId, AssetsCatalog currentAssetsCatalog)
            throws AssetNotOwnedException, UnavailableAssetException {
        validateAssetId(assetId);
        validateAssetsCatalog(currentAssetsCatalog);

        Asset asset = currentAssetsCatalog.findById(assetId);
        if (asset == null) {
            throw new UnavailableAssetException("The asset with the provided ID is unavailable.");
        }
        if (!ownedAssets.containsKey(asset) || ownedAssets.get(asset) == 0.0) {
            throw new AssetNotOwnedException("Wallet doesn't contain this asset.");
        }

        double soldAmount = ownedAssets.get(asset);
        double income = soldAmount * asset.getPrice();

        ownedAssets.remove(asset);
        balance += income;

        buyHistory.remove(asset);
    }

    public WalletSummary getSummary() {
        return new WalletSummary(balance, Collections.unmodifiableMap(ownedAssets));
    }

    public OverallWalletSummary getOverallSummary(AssetsCatalog assetsCatalog) {
        validateAssetsCatalog(assetsCatalog);

        Map<Asset, Double> assetProfits = new HashMap<>();

        for (var entry : ownedAssets.entrySet()) {
            Asset asset = assetsCatalog.findById(entry.getKey().getAssetId());
            if (asset == null) {
                continue;
            }

            double currentPrice = asset.getPrice();
            double amountOwned = entry.getValue();
            double totalSpent = buyHistory.get(asset);

            double currentValue = amountOwned * currentPrice;
            double profitOrLoss = currentValue - totalSpent;

            if (Math.abs(profitOrLoss) < DELTA) {
                profitOrLoss = 0.0;
            }

            assetProfits.put(asset, profitOrLoss);
        }

        return new OverallWalletSummary(assetProfits);
    }

    private void validateAssetId(String assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("AssetID cannot be null.");
        }
    }

    private void validateAssetsCatalog(AssetsCatalog catalog) {
        if (catalog == null) {
            throw new IllegalArgumentException("AssetsCatalog cannot be null.");
        }
    }
}
