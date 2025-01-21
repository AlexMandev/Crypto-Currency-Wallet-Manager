package bg.sofia.uni.fmi.mjt.crypto.wallet.coin.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AssetRequestClient {

    private static final String ENDPOINT_SCHEME = "https";
    private static final String ENDPOINT_HOST = "rest.coinapi.io";
    private static final String ENDPOINT_PATH = "/v1/assets/";
    private static final String PATH_DELIMITER = "/";

    private static final String API_KEY_PARAM_STRING = "APIKEY-";

    private final HttpClient httpClient;
    private final String apiKey;

    public AssetRequestClient(HttpClient httpClient, String apiKey) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
    }

    public HttpResponse<String> getAllAssets() {
        HttpResponse<String> apiResponse;

        try {
            URI uri = new URI(ENDPOINT_SCHEME, ENDPOINT_HOST,
                    ENDPOINT_PATH + API_KEY_PARAM_STRING + apiKey, null);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            apiResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Couldn't send http request. URI is invalid." , e);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while sending http request.", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("A problem with the connection occurred while sending http request", e);
        }

        return apiResponse;
    }

    public HttpResponse<String> getAssetById(String assetId) {
        if (assetId == null || assetId.isEmpty() || assetId.isBlank()) {
            throw new IllegalArgumentException("assetId cannot be null, empty or blank.");
        }
        HttpResponse<String> apiResponse;

        try {
            URI uri = new URI(ENDPOINT_SCHEME, ENDPOINT_HOST,
                    ENDPOINT_PATH + assetId + PATH_DELIMITER + API_KEY_PARAM_STRING + apiKey, null);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            apiResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Couldn't send http request. URI is invalid." , e);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while sending http request.", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("A problem with the connection occurred while sending http request", e);
        }

        return apiResponse;
    }

}
