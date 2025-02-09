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
    static final double DELTA = 0.01;
    private static final double INITIAL_BALANCE = 0.0;

    private double balance;
    private final Map<Asset, Double> ownedAssets;
    private final Map<Asset, Double> buyHistory;
    private final TransactionManager transactionManager;

    public Wallet() {
        this.balance = INITIAL_BALANCE;
        this.ownedAssets = new HashMap<>();
        this.buyHistory = new HashMap<>();
        this.transactionManager = new TransactionManager(ownedAssets, buyHistory);
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

    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount < INITIAL_BALANCE) {
            throw new IllegalArgumentException("Withdraw amount cannot be negative.");
        }
        if (amount > balance) {
            throw new InsufficientBalanceException("Insufficient funds. Please check your wallet's balance.");
        }
        balance -= amount;
    }

    public void buy(String assetId, double dollarAmount, AssetsCatalog currentAssetsCatalog)
            throws InsufficientBalanceException, UnavailableAssetException {
        validateTransaction(assetId, dollarAmount, currentAssetsCatalog);

        Asset asset = currentAssetsCatalog.findById(assetId);
        transactionManager.buy(asset, dollarAmount, asset.getPrice());
        balance -= dollarAmount;
    }

    public void sell(String assetId, AssetsCatalog currentAssetsCatalog)
            throws AssetNotOwnedException, UnavailableAssetException {
        validateAssetId(assetId);
        validateAssetsCatalog(currentAssetsCatalog);

        Asset asset = currentAssetsCatalog.findById(assetId);
        if (asset == null) {
            throw new UnavailableAssetException("The asset with the provided ID is unavailable.");
        }

        double income = transactionManager.sell(asset, asset.getPrice());
        balance += income;
    }

    public WalletSummary getSummary() {
        return new WalletSummary(balance, Collections.unmodifiableMap(ownedAssets));
    }

    public OverallWalletSummary getOverallSummary(AssetsCatalog assetsCatalog) {
        validateAssetsCatalog(assetsCatalog);
        return new OverallWalletSummary(transactionManager.calculateProfits(assetsCatalog));
    }

    private void validateTransaction(String assetId, double dollarAmount, AssetsCatalog catalog)
            throws InsufficientBalanceException, UnavailableAssetException {
        validateAssetId(assetId);
        validateAssetsCatalog(catalog);

        if (dollarAmount <= INITIAL_BALANCE) {
            throw new IllegalArgumentException("DollarAmount should be positive.");
        }
        if (dollarAmount > balance) {
            throw new InsufficientBalanceException("Not enough funds to buy. Please check the wallet's balance.");
        }

        if (catalog.findById(assetId) == null) {
            throw new UnavailableAssetException("The asset with the provided ID is unavailable.");
        }
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