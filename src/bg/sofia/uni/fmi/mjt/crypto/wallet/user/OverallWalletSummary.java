package bg.sofia.uni.fmi.mjt.crypto.wallet.user;

import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;

import java.util.Map;

public record OverallWalletSummary(Map<Asset, Double> assetProfits) {
    private static final String OVERALL_SUMMARY_HEADER = "Overall Wallet Summary:";
    private static final String ASSET = "Asset: ";
    private static final String PROFIT = "Profit: ";
    private static final String LOSS = "Loss: ";
    private static final String NO_PROFIT = "No Profit/Loss from this asset";
    private static final char CURRENCY = '$';

    @Override
    public String toString() {
        StringBuilder summary = new StringBuilder();
        summary.append(OVERALL_SUMMARY_HEADER).append(System.lineSeparator());

        for (var entry : assetProfits.entrySet()) {
            summary.append(ASSET).append(entry.getKey().getName()).append(", ");

            double profit = entry.getValue();
            if (profit > 0.0) {
                summary.append(PROFIT).append(profit).append(CURRENCY);
            } else if (profit < 0.0) {
                summary.append(LOSS).append(profit).append(CURRENCY);
            } else {
                summary.append(NO_PROFIT);
            }

            summary.append(System.lineSeparator());
        }

        return summary.toString();
    }
}
