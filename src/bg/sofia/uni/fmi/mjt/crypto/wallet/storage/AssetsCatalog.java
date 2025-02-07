package bg.sofia.uni.fmi.mjt.crypto.wallet.storage;

import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AssetsCatalog {
    private static final int MAX_ASSETS = 50;
    private static final String TO_STRING_HEADLINE = "Current available assets:" + System.lineSeparator();

    private final Map<String, Asset> assetsMap;

    public AssetsCatalog() {
        assetsMap = new ConcurrentHashMap<>();
    }

    public void updateCatalog(List<Asset> newAssets) {
        if (newAssets == null) {
            throw new IllegalArgumentException("List of new assets cannot be null.");
        }
        newAssets.stream()
                .filter(Asset::isCrypto)
                .limit(MAX_ASSETS)
                .forEach(asset -> assetsMap.put(asset.getAssetId(), asset));
    }

    public Asset findById(String assetId) {
        return assetsMap.getOrDefault(assetId, null);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(TO_STRING_HEADLINE);
        assetsMap.forEach((id, asset) -> builder.append(asset.toString()).append(System.lineSeparator()));
        return builder.toString();
    }
}
