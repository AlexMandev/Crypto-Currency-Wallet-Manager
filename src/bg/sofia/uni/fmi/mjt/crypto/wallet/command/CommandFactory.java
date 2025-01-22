package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnknownCommandException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.UserRepository;

import java.nio.channels.SelectionKey;

public class CommandFactory {
    private static final String WHITESPACE_REGEX = "\\s+";
    private static final int COMMAND_ARGUMENTS_LIMIT = 2;
    private static final String UNKNOWN_COMMAND_MESSAGE = "Unknown command.";
    private static final String HELP_COMMAND = "help";
    private static final String LIST_OFFERINGS_COMMAND = "list-offerings";

    private final UserRepository users;
    private final AssetsCatalog assetsCatalog;

    public CommandFactory(UserRepository userRepository, AssetsCatalog assetsCatalog) {
        this.users = userRepository;
        this.assetsCatalog = assetsCatalog;
    }

    public Command of(String input, SelectionKey key) throws UnknownCommandException {
        if (input == null || input.isEmpty() || input.isBlank()) {
            throw new IllegalArgumentException("Command input cannot be null, empty or blank.");
        }
        String trimmedInput = input.replaceAll(WHITESPACE_REGEX, " ").trim();

        String[] parts = trimmedInput.split(" ", COMMAND_ARGUMENTS_LIMIT);
        String command = parts[0].toLowerCase();
        String[] arguments = parts.length > 1 ? parts[1].split(" ") : new String[0];

        return switch (command) {
            case LIST_OFFERINGS_COMMAND -> new ListOfferingsCommand(arguments, key, assetsCatalog);
            case HELP_COMMAND -> new HelpCommand(arguments, key);
            default -> throw new UnknownCommandException(UNKNOWN_COMMAND_MESSAGE);
        };
    }
}
