package bg.sofia.uni.fmi.mjt.crypto.wallet.server;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServerStopRunnableTest {

    private static CryptoCurrencyWalletManagerNioServer serverMock;
    private static InputStream sourceMock;

    @BeforeAll
    static void setupMocks() {
        serverMock = mock();
        sourceMock = mock();
    }

    @Test
    void testNullServer() {
        assertThrows(IllegalArgumentException.class,
                () -> new ServerStopRunnable(null, sourceMock),
                "Should throw for null server.");
    }

    @Test
    void testNullSource() {
        assertThrows(IllegalArgumentException.class,
                () -> new ServerStopRunnable(serverMock, null),
                "Should throw for null source.");
    }

    @Test
    void testRun() {
        String text = "stop\n";
        InputStream byteStream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        doNothing().when(serverMock).stop();

        ServerStopRunnable runnable = new ServerStopRunnable(serverMock, byteStream);
        runnable.run();
        verify(serverMock, times(1)).stop();
    }
}
