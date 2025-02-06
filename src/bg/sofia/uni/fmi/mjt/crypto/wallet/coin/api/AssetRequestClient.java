package bg.sofia.uni.fmi.mjt.crypto.wallet.coin.api;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.ApiRequestException;

import java.io.IOException;
import java.net.HttpURLConnection;
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

    private static final int HTTP_TOO_MANY_REQUESTS = 429;

    private final HttpClient httpClient;
    private final String apiKey;

    public AssetRequestClient(HttpClient httpClient, String apiKey) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
    }

    public HttpResponse<String> getAllAssets() throws ApiRequestException {
        HttpResponse<String> apiResponse;

        try {
            URI uri = new URI(ENDPOINT_SCHEME, ENDPOINT_HOST,
                    ENDPOINT_PATH + API_KEY_PARAM_STRING + apiKey, null);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            apiResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            checkStatusCode(apiResponse);
        } catch (URISyntaxException e) {
            throw new ApiRequestException("Couldn't send http request. URI is invalid.", e);
        } catch (IOException e) {
            throw new ApiRequestException("Error occurred while sending http request.", e);
        } catch (InterruptedException e) {
            throw new ApiRequestException("A problem with the connection occurred while sending http request", e);
        }

        return apiResponse;
    }

    private void checkStatusCode(HttpResponse<String> response) throws ApiRequestException {
        int statusCode = response.statusCode();
        String errorMessage
                = "API request failed with status code: " + statusCode + " and response: " + response.body();

        switch (statusCode) {
            case HttpURLConnection.HTTP_BAD_REQUEST -> throw new ApiRequestException("Bad Request: " + errorMessage);
            case HttpURLConnection.HTTP_UNAUTHORIZED -> throw new ApiRequestException("Unauthorized: " + errorMessage);
            case HttpURLConnection.HTTP_FORBIDDEN -> throw new ApiRequestException("Forbidden: " + errorMessage);
            case HTTP_TOO_MANY_REQUESTS -> throw new ApiRequestException("Too many requests: " + errorMessage);
        }
    }
}
