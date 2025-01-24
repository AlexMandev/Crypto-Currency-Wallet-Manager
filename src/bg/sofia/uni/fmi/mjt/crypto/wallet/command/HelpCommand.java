package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;

import java.nio.channels.SelectionKey;

public class HelpCommand extends Command {
    private static final int HELP_ARGUMENTS_COUNT = 0;

    public HelpCommand(String[] arguments, SelectionKey selectionKey) {
        super(arguments, selectionKey);
        validateArguments();
    }

    @Override
    public void validateArguments() {
        super.validateArguments();
        if (arguments.length != HELP_ARGUMENTS_COUNT) {
            throw new CommandArgumentCountException("The help command doesn't have any arguments.");
        }
    }

    @Override
    public String execute() {
        return "Available commands: " + System.lineSeparator() +
                "login <username> <password>" + System.lineSeparator() +
                "register <username> <password>" + System.lineSeparator() +
                "deposit <amount>" + System.lineSeparator() +
                "list-offerings" + System.lineSeparator() +
                "buy <asset_id> <amount>" + System.lineSeparator() +
                "sell <asset_id>" + System.lineSeparator() +
                "get-wallet-summary" + System.lineSeparator() +
                "get-wallet-overall-summary" + System.lineSeparator() +
                "quit" + System.lineSeparator();
    }
}
