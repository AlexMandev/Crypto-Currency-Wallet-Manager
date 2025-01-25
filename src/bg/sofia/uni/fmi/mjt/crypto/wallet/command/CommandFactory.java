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
    private static final String LOGIN_COMMAND = "login";
    private static final String REGISTER_COMMAND = "register";
    private static final String DEPOSIT_COMMAND = "deposit";
    private static final String LOGOUT_COMMAND = "logout";
    private static final String BUY_COMMAND = "buy";
    private static final String SELL_COMMAND = "sell";
    private static final String WALLET_SUMMARY_COMMAND = "get-wallet-summary";
    private static final String WALLET_OVERALL_SUMMARY_COMMAND = "get-wallet-overall-summary";

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
            case REGISTER_COMMAND -> new RegisterCommand(arguments, key, users);
            case LOGIN_COMMAND -> new LoginCommand(arguments, key, users);
            case DEPOSIT_COMMAND -> new DepositCommand(arguments, key);
            case BUY_COMMAND -> new BuyCommand(arguments, key, assetsCatalog);
            case LOGOUT_COMMAND -> new LogoutCommand(arguments, key);
            case SELL_COMMAND -> new SellCommand(arguments, key, assetsCatalog);
            case WALLET_SUMMARY_COMMAND -> new WalletSummaryCommand(arguments, key);
            case WALLET_OVERALL_SUMMARY_COMMAND -> new WalletOverallSummaryCommand(arguments, key);
            default -> throw new UnknownCommandException(UNKNOWN_COMMAND_MESSAGE);
        };
    }
}
