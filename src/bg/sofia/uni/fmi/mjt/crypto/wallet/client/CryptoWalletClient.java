package bg.sofia.uni.fmi.mjt.crypto.wallet.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CryptoWalletClient {
    private static final int BUFFER_SIZE = 8192;
    private static final String EXIT_MESSAGE = "disconnect";
    private static final String WELCOME_MESSAGE = "Welcome to the CryptoWalletManager. " +
            "You can type help for a list of commands.";
    private static final String COMMAND_PREFIX = "$";
    private static final String INVALID_COMMAND = "Invalid command. Please try again.";

    private final String host;
    private final int port;
    private ByteBuffer buffer;

    public CryptoWalletClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (SocketChannel clientSocketChannel = SocketChannel.open()) {
            clientSocketChannel.connect(new InetSocketAddress(host, port));
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            System.out.println(WELCOME_MESSAGE);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print(COMMAND_PREFIX);
                String command = scanner.nextLine();

                if (command == null || command.isEmpty() || command.isBlank()) {
                    System.out.println(INVALID_COMMAND);
                    continue;
                }
                if (command.equals(EXIT_MESSAGE)) {
                    clientSocketChannel.close();
                    break;
                }

                sendMessageToServer(clientSocketChannel, command);

                String serverResponse = getServerResponse(clientSocketChannel);
                System.out.println(serverResponse);
            }

        } catch (IOException e) {
            System.out.println("Unable to connect to server. Please try again.");
        }
    }

    private void handleCommand(SocketChannel client, String command) throws IOException {
        sendMessageToServer(client, command);
    }

    private void sendMessageToServer(SocketChannel client, String message) throws IOException {
        buffer.clear();
        buffer.put(message.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        client.write(buffer);
    }

    private String getServerResponse(SocketChannel client) throws IOException {
        buffer.clear();
        client.read(buffer);
        buffer.flip();
        byte[] messageBytes = new byte[buffer.remaining()];
        buffer.get(messageBytes);
        return new String(messageBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        CryptoWalletClient client = new CryptoWalletClient("localhost", 10001);
        client.start();
    }
}
