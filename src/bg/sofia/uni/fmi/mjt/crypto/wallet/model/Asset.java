package bg.sofia.uni.fmi.mjt.crypto.wallet.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Asset {
    private static final int TYPE_IS_CRYPTO_VALUE = 1;

    @SerializedName("asset_id")
    private final String assetId;
    private final String name;
    @SerializedName("price_usd")
    private final double price;
    @SerializedName("type_is_crypto")
    private final int isCrypto;

    public Asset(String assetId, String name, double price, int isCrypto) {
        if (assetId == null || assetId.isEmpty() || assetId.isBlank()) {
            throw new IllegalArgumentException("AssetId cannot be null, empty or blank.");
        }
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null, empty or blank.");
        }
        if (price < 0.0) {
            throw new IllegalArgumentException("Asset price cannot be negative.");
        }
        this.assetId = assetId;
        this.name = name;
        this.price = price;
        this.isCrypto = isCrypto;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isCrypto() {
        return isCrypto == TYPE_IS_CRYPTO_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Objects.equals(assetId, asset.assetId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(assetId);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(assetId).append(", ").append(name).append(", ").append("Price: ")
                .append(price).append("$");
        return builder.toString();
    }
}
