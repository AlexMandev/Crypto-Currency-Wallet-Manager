package bg.sofia.uni.fmi.mjt.crypto.wallet.user;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.AssetNotOwnedException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnavailableAssetException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet implements Serializable {
    @Serial
    private static final long serialVersionUID = -7459989033155017236L;

    private static final String BALANCE = "Balance: ";
    private static final String WALLET_SUMMARY = "Wallet Summary:";
    private static final String OVERALL_WALLET_SUMMARY = "Overall Wallet Summary:";
    private static final String ASSET = "Asset: ";
    private static final String AMOUNT = ", Amount: ";
    private static final String PROFIT = "Profit: ";
    private static final String LOSS = "Loss: ";
    private static final String NO_PROFIT_OR_LOSS = "No Profit/Loss from this asset";
    private static final char DEFAULT_CURRENCY = '$';

    private static final double INITIAL_BALANCE = 0.0;

    private double balance;
    private final Map<Asset, Double> ownedAssets;
    private final Map<Asset, Double> assetProfits;
    private final Map<Asset, List<Double>> buyHistory;

    public Wallet() {
        this.balance = INITIAL_BALANCE;
        this.ownedAssets = new HashMap<>();
        this.assetProfits = new HashMap<>();
        this.buyHistory = new HashMap<>();
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
            throw new InsufficientBalanceException("Not enough funds to buy. Please check your balance.");
        }

        Asset asset = currentAssetsCatalog.findById(assetId);
        if (asset == null) {
            throw new UnavailableAssetException("The asset with the provided ID is unavailable.");
        }

        double amountBought = dollarAmount / asset.getPrice();
        balance -= dollarAmount;
        ownedAssets.put(asset, ownedAssets.getOrDefault(asset, 0.0) + amountBought);
        buyHistory.putIfAbsent(asset, new ArrayList<>());
        buyHistory.get(asset).add(dollarAmount);
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
        double totalSpentOnAsset = buyHistory.get(asset)
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double income = soldAmount * asset.getPrice();
        double diff = income - totalSpentOnAsset;

        ownedAssets.remove(asset);
        balance += income;

        assetProfits.put(asset, assetProfits.getOrDefault(asset, 0.0) + diff);
        buyHistory.remove(asset);
    }

    public String getSummary() {
        StringBuilder summary = new StringBuilder();

        summary.append(WALLET_SUMMARY).append(System.lineSeparator());
        summary.append(BALANCE).append(balance)
                .append(DEFAULT_CURRENCY).append(System.lineSeparator());

        ownedAssets.forEach((asset, amount) ->
                summary.append(ASSET).append(asset.getName())
                        .append(AMOUNT).append(amount).append(System.lineSeparator()));

        return summary.toString();
    }

    public String getOverallSummary() {
        StringBuilder overallSummary = new StringBuilder();

        overallSummary.append(OVERALL_WALLET_SUMMARY).append(System.lineSeparator());

        for (var entry : assetProfits.entrySet()) {
            overallSummary.append(ASSET)
                    .append(entry.getKey().getName())
                    .append(", ");

            double diff = entry.getValue();

            if (diff > 0.0) {
                overallSummary.append(PROFIT).append(diff + DEFAULT_CURRENCY);
            } else if (diff < 0.0) {
                overallSummary.append(LOSS).append(diff + DEFAULT_CURRENCY);
            } else {
                overallSummary.append(NO_PROFIT_OR_LOSS);
            }
            overallSummary.append(System.lineSeparator());
        }

        return overallSummary.toString();
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
