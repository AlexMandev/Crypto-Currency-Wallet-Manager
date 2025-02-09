package bg.sofia.uni.fmi.mjt.crypto.wallet.server;

import bg.sofia.uni.fmi.mjt.crypto.wallet.coin.api.AssetRequestClient;
import bg.sofia.uni.fmi.mjt.crypto.wallet.coin.api.CryptoAssetUpdaterRunnable;
import bg.sofia.uni.fmi.mjt.crypto.wallet.command.Command;
import bg.sofia.uni.fmi.mjt.crypto.wallet.command.CommandFactory;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnknownCommandException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.logs.Logs;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.InMemoryUserRepository;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.UserRepository;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CryptoCurrencyWalletManagerNioServer {
    private static final int BUFFER_SIZE = 8192;
    private static final String HOST = "localhost";

    private static final long INITIAL_DELAY = 0;
    private static final long MINUTES_BETWEEN_API_CALLS = 30;

    private static final String API_KEY = "5e7e8168-b1ca-460c-aab9-9b4905f7ef31";
    private static final int DEFAULT_PORT = 10001;

    private final int port;
    private ByteBuffer buffer;
    private Selector selector;
    private final CommandFactory commandFactory;
    private final CryptoAssetUpdaterRunnable cryptoAssetUpdaterRunnable;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public CryptoCurrencyWalletManagerNioServer(int port, CommandFactory commandFactory,
                                                CryptoAssetUpdaterRunnable cryptoAssetUpdaterRunnable) {
        this.port = port;
        this.commandFactory = commandFactory;
        this.cryptoAssetUpdaterRunnable = cryptoAssetUpdaterRunnable;
    }

    public void start() {
        try (ServerSocketChannel server = ServerSocketChannel.open();
             ScheduledExecutorService cryptoAssetService = Executors.newSingleThreadScheduledExecutor();
             ExecutorService stopper = Executors.newSingleThreadExecutor()) {
            cryptoAssetService.scheduleAtFixedRate(cryptoAssetUpdaterRunnable, INITIAL_DELAY,
                    MINUTES_BETWEEN_API_CALLS, TimeUnit.MINUTES);
            selector = Selector.open();
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            configureServerSocketChannel(server, selector);
            isRunning.set(true);
            stopper.submit(new ServerStopRunnable(this));
            selectKeys();
        } catch (IOException e) {
            Logs.logError("An IOException occurred while starting the server.", e);
        } catch (Exception e) {
            Logs.logError("An error occurred while the server was working.", e);
        }
    }

    public void stop() {
        this.isRunning.set(false);
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel server, Selector selector) throws IOException {
        server.bind(new InetSocketAddress(HOST, port));
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void selectKeys() {
        while (isRunning.get()) {
            try {
                int readyChannels = selector.select();
                if (readyChannels < 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey currentKey = iterator.next();
                    if (currentKey.isReadable()) {
                        handleReadableKey(currentKey);
                    } else if (currentKey.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) currentKey.channel();
                        SocketChannel currentClient = serverSocketChannel.accept();
                        currentClient.configureBlocking(false);
                        currentClient.register(selector, SelectionKey.OP_READ);
                    }
                    iterator.remove();
                }
            } catch (IOException e) {
                Logs.logError("An error occurred while server selector was handling the keys.", e);
            }
        }
    }

    private void handleReadableKey(SelectionKey key) throws IOException {
        SocketChannel client = null;
        try {
            client = (SocketChannel) key.channel();
            String clientMessage = getClientMessage(client, key);
            if (clientMessage == null) {
                return;
            }
            Command clientCommand = commandFactory.of(clientMessage, key);
            sendMessageToClient(client, key, clientCommand.execute());
        } catch (UnknownCommandException | InvalidCommandArgumentException | CommandArgumentCountException e) {
            sendMessageToClient(client, key, e.getMessage());
        } catch (Exception e) {
            String username = null;
            if (key.attachment() != null) {
                User user = (User) key.attachment();
                username = user.getUsername();
            }
            if (username != null) {
                Logs.logError("An error occurred while handling key by user: " + username, e);
            } else {
                Logs.logError("An error occurred while handling a selectable key from the selector", e);
            }

        }
    }

    private String getClientMessage(SocketChannel client, SelectionKey key) throws IOException {
        buffer.clear();
        int readBytes;
        try {
            readBytes = client.read(buffer);
        } catch (IOException e) {
            key.cancel();
            client.close();
            return null;
        }
        if (readBytes < 0) {
            client.close();
            return null;
        }
        buffer.flip();

        byte[] clientBytes = new byte[buffer.remaining()];
        buffer.get(clientBytes);

        return new String(clientBytes, StandardCharsets.UTF_8);
    }

    private void sendMessageToClient(SocketChannel client, SelectionKey key, String message) throws IOException {
        if (!client.isConnected()) {
            return;
        }
        buffer.clear();
        buffer.put(message.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        try {
            client.write(buffer);
        } catch (IOException e) {
            client.close();
            key.cancel();
            Logs.logError("An error occurred when sending server response to client's socket channel.", e);
        }
    }

    public static void main(String[] args) {
        try (UserRepository users = new InMemoryUserRepository()) {
            users.load();
            AssetsCatalog catalog = new AssetsCatalog();
            AssetRequestClient apiClient = new AssetRequestClient(HttpClient.newHttpClient(), API_KEY);
            CryptoAssetUpdaterRunnable runnable = new CryptoAssetUpdaterRunnable(apiClient, catalog);
            CommandFactory commandFactory = new CommandFactory(users, catalog);
            CryptoCurrencyWalletManagerNioServer server =
                    new CryptoCurrencyWalletManagerNioServer(DEFAULT_PORT, commandFactory, runnable);
            server.start();
        } catch (Exception e) {
            Logs.logError("Unknown error occurred in the server program.", e);
        }
    }
}

