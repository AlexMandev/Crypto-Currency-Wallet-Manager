package bg.sofia.uni.fmi.mjt.crypto.wallet.user;

import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;

import java.util.Map;

public record WalletSummary(double balance, Map<Asset, Double> ownedAssets) {
    private static final String WALLET_SUMMARY_HEADER = "Wallet Summary:";
    private static final String BALANCE = "Balance: ";
    private static final char CURRENCY = '$';
    private static final String ASSET = "Asset: ";
    private static final String AMOUNT = ", Amount: ";

    @Override
    public String toString() {
        StringBuilder summary = new StringBuilder();
        summary.append(WALLET_SUMMARY_HEADER).append(System.lineSeparator());
        summary.append(BALANCE).append(balance).append(CURRENCY).append(System.lineSeparator());

        ownedAssets.forEach((asset, amount) ->
                summary.append(ASSET).append(asset.getName())
                        .append(AMOUNT).append(amount)
                        .append(System.lineSeparator())
        );

        return summary.toString();
    }
}
