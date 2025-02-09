package bg.sofia.uni.fmi.mjt.crypto.wallet.coin.api;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.ApiRequestException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.logs.Logs;
import bg.sofia.uni.fmi.mjt.crypto.wallet.model.Asset;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;

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

        try {
            HttpResponse<String> apiResponse = assetRequestClient.getAllAssets();

            if (apiResponse != null) {
                Type bodyType = new TypeToken<List<Asset>>() { }.getType();
                catalogToUpdate.updateCatalog(GSON.fromJson(apiResponse.body(), bodyType));
            }
        } catch (ApiRequestException e) {
            Logs.logError("An error occurred: couldn't update assets.", e);
        }

    }
}
