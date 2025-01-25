package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;

import java.nio.channels.SelectionKey;

public class LogoutCommand extends Command {

    private static final int LOGOUT_ARGUMENTS_COUNT = 0;
    private static final String NOT_LOGGED_IN_MESSAGE = "You are not logged in.";
    private static final String SUCCESSFUL_LOGOUT_MESSAGE = "You have logged out successfully from your account.";
    private static final String INVALID_ARGUMENTS_COUNT_MESSAGE = "Invalid number of arguments for this command. " +
            HELP_MESSAGE;

    public LogoutCommand(String[] arguments, SelectionKey selectionKey) {
        super(arguments, selectionKey);
        validateArguments();
    }

    @Override
    public String execute() {
        if (selectionKey.attachment() == null) {
            return NOT_LOGGED_IN_MESSAGE;
        }

        selectionKey.attach(null);
        return SUCCESSFUL_LOGOUT_MESSAGE;
    }

    @Override
    public void validateArguments() {
        super.validateArguments();

        if (arguments.length != LOGOUT_ARGUMENTS_COUNT) {
            throw new CommandArgumentCountException(INVALID_ARGUMENTS_COUNT_MESSAGE);
        }
    }
}
