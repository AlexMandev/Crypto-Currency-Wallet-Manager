package bg.sofia.uni.fmi.mjt.crypto.wallet.server;

import java.io.InputStream;
import java.util.Scanner;

public class ServerStopRunnable implements Runnable {

    private static final String STOP_MESSAGE = "stop";
    private final CryptoCurrencyWalletManagerNioServer server;
    private final InputStream source;

    public ServerStopRunnable(CryptoCurrencyWalletManagerNioServer server, InputStream source) {
        if (server == null) {
            throw new IllegalArgumentException("Server cannot be null.");
        }
        if (source == null) {
            throw new IllegalArgumentException("InputStream cannot be null.");
        }
        this.server = server;
        this.source = source;
    }

    @Override
    public void run() {

        while (true) {
            Scanner scanner = new Scanner(source);
            String message = scanner.nextLine();

            if (message.equals(STOP_MESSAGE)) {
                server.stop();
                System.out.println("Stopping server...");
                break;
            }
        }

    }
}
