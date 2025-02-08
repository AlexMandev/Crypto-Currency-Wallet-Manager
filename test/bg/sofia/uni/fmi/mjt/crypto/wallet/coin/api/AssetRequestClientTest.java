package bg.sofia.uni.fmi.mjt.crypto.wallet.coin.api;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.ApiRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssetRequestClientTest {

    private static HttpClient httpClientMock;
    private static HttpResponse<String> httpResponseMock;
    private static AssetRequestClient client;

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {
        httpClientMock = mock();
        httpResponseMock = mock();
        client = new AssetRequestClient(httpClientMock, "key");
    }

    @BeforeEach
    void setUpEach() throws IOException, InterruptedException {
        when(httpClientMock.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponseMock);
    }

    @Test
    void testGetAllAssetsUnexpectedStatusCode() {
        when(httpResponseMock.statusCode()).thenReturn(1000);
        assertThrows(ApiRequestException.class, () -> client.getAllAssets(),
                "Should throw for unexpected status code.");
    }

    @Test
    void testGetAllAssetsBadRequest() {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        assertThrows(ApiRequestException.class, () -> client.getAllAssets(),
                "Should throw for bad request code.");
    }

    @Test
    void testGetAllAssetsUnauthorized() {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
        assertThrows(ApiRequestException.class, () -> client.getAllAssets(),
                "Should throw for unauthorized status code.");
    }

    @Test
    void testGetAllAssetsTooManyRequests() {
        when(httpResponseMock.statusCode()).thenReturn(429);
        assertThrows(ApiRequestException.class, () -> client.getAllAssets(),
                "Should throw for too many requests status code.");
    }

    @Test
    void testGetAllAssetsForbidden() {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
        assertThrows(ApiRequestException.class, () -> client.getAllAssets(),
                "Should throw for forbidden status code.");
    }

    @Test
    void testGetAllAssets() throws ApiRequestException {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        assertSame(client.getAllAssets(), httpResponseMock,
                "Should return the same response returned by the HttpClient.");
    }

    @Test
    void testGetAllAssetsIOException() throws IOException, InterruptedException {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Simulated IOException"));

        assertThrows(ApiRequestException.class,
                () -> client.getAllAssets(),
                "Should throw ApiRequestException when IOException occurs.");
    }

    @Test
    void testGetAllAssetsInterruptedException() throws IOException, InterruptedException {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Simulated InterruptedException"));

        assertThrows(ApiRequestException.class,
                () -> client.getAllAssets(),
                "Should throw ApiRequestException when InterruptedException occurs.");
    }

    @Test
    void testNullHttpClient() {
        assertThrows(IllegalArgumentException.class,
                () -> new AssetRequestClient(null, "key"),
                "Should throw IllegalArgumentException when httpClient is null.");
    }

    @Test
    void testNullApiKey() {
        assertThrows(IllegalArgumentException.class,
                () -> new AssetRequestClient(httpClientMock, null),
                "Should throw IllegalArgumentException when apiKey is null.");
    }
}
