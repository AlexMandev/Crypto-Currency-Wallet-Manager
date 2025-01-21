package bg.sofia.uni.fmi.mjt.crypto.wallet.coin.api;

import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;

public class CryptoAssetUpdaterRunnable implements Runnable {

    private static final Gson GSON = new Gson();

    private final AssetRequestClient assetRequestClient;
    private final AssetsCatalog catalogToUpdate;

    public CryptoAssetUpdaterRunnable(AssetRequestClient assetRequestClient, AssetsCatalog catalogToUpdate) {
        this.assetRequestClient = assetRequestClient;
        this.catalogToUpdate = catalogToUpdate;
    }

    @Override
    public void run() {
        HttpResponse<String> apiResponse = assetRequestClient.getAllAssets();

        if (apiResponse != null) {
            Type bodyType = new TypeToken<Asset[]>() { }.getType();
            catalogToUpdate.updateCatalog(GSON.fromJson(apiResponse.body(), bodyType));
        }
    }
}
