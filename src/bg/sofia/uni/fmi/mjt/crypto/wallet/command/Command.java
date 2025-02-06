package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import java.nio.channels.SelectionKey;

public abstract class Command {
    protected static final String HELP_MESSAGE = "Enter help in the console, if you want to see all commands.";
    public static final String NOT_LOGGED_IN_MESSAGE = "You are not logged in. Please register or login first.";
    protected final String[] arguments;
    protected final SelectionKey selectionKey;

    public Command(String[] arguments, SelectionKey selectionKey) {
        this.arguments = arguments;
        this.selectionKey = selectionKey;
    }

    public void validateArguments() {
        if (arguments == null) {
            throw new IllegalArgumentException("Command arguments cannot be null.");
        }
        if (selectionKey == null) {
            throw new IllegalArgumentException("SelectionKey of command cannot be null.");
        }
    }

    public abstract String execute();
}
