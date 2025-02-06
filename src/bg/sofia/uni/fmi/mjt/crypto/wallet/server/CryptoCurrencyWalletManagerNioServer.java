package bg.sofia.uni.fmi.mjt.crypto.wallet.server;

import bg.sofia.uni.fmi.mjt.crypto.wallet.coin.api.CryptoAssetUpdaterRunnable;
import bg.sofia.uni.fmi.mjt.crypto.wallet.command.Command;
import bg.sofia.uni.fmi.mjt.crypto.wallet.command.CommandFactory;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnknownCommandException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CryptoCurrencyWalletManagerNioServer {
    private static final int BUFFER_SIZE = 8192;
    private static final String HOST = "localhost";

    private static final long INITIAL_DELAY = 0;
    private static final long MINUTES_BETWEEN_API_CALLS = 30;

    private final int port;
    private ByteBuffer buffer;
    private Selector selector;
    private final CommandFactory commandFactory;
    private final CryptoAssetUpdaterRunnable cryptoAssetUpdaterRunnable;
    private boolean isRunning = false;

    public CryptoCurrencyWalletManagerNioServer(int port, CommandFactory commandFactory,
                                                CryptoAssetUpdaterRunnable cryptoAssetUpdaterRunnable) {
        this.port = port;
        this.commandFactory = commandFactory;
        this.cryptoAssetUpdaterRunnable = cryptoAssetUpdaterRunnable;
    }

    public void start() {
        try (ServerSocketChannel server = ServerSocketChannel.open();
             ScheduledExecutorService cryptoAssetService = Executors.newSingleThreadScheduledExecutor()) {
            cryptoAssetService.scheduleAtFixedRate(cryptoAssetUpdaterRunnable, INITIAL_DELAY,
                    MINUTES_BETWEEN_API_CALLS, TimeUnit.MINUTES);
            selector = Selector.open();
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            configureServerSocketChannel(server, selector);
            isRunning = true;
            selectKeys();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            // log error
        }
    }

    public void stop() {
        this.isRunning = false;
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
        while (isRunning) {
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
                // log error while handling selected keys
            }
        }
    }

    private void handleReadableKey(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        String clientMessage = getClientMessage(client);
        try {
            Command clientCommand = commandFactory.of(clientMessage, key);
            sendMessageToClient(client, clientCommand.execute());
        } catch (UnknownCommandException | InvalidCommandArgumentException | CommandArgumentCountException e) {
            sendMessageToClient(client, e.getMessage());
        } catch (Exception e) {
            // log client request error
        }
    }

    private String getClientMessage(SocketChannel client) throws IOException {
        buffer.clear();

        int readBytes = client.read(buffer);
        if (readBytes < 0) {
            client.close();
            return null;
        }
        buffer.flip();

        byte[] clientBytes = new byte[buffer.remaining()];
        buffer.get(clientBytes);

        return new String(clientBytes, StandardCharsets.UTF_8);
    }

    private void sendMessageToClient(SocketChannel client, String message) throws IOException {
        if (!client.isConnected()) {
            return;
        }
        buffer.clear();
        buffer.put(message.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        client.write(buffer);
    }
}

