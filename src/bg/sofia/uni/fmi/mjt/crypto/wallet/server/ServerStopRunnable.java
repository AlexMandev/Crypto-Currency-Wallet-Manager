package bg.sofia.uni.fmi.mjt.crypto.wallet.server;

import java.util.Scanner;

public class ServerStopRunnable implements Runnable {

    private static final String STOP_MESSAGE = "stop";
    private final CryptoCurrencyWalletManagerNioServer server;

    public ServerStopRunnable(CryptoCurrencyWalletManagerNioServer server) {
        this.server = server;
    }

    @Override
    public void run() {

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();

            if (message.equals(STOP_MESSAGE)) {
                server.stop();
                System.out.println("Stopping server...");
                break;
            }
        }

    }
}
