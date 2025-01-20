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
}
