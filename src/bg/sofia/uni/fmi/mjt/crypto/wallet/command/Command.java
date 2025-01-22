package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import java.nio.channels.SelectionKey;

public abstract class Command {

    protected final String[] arguments;
    protected final SelectionKey selectionKey;

    public Command(String[] arguments, SelectionKey selectionKey) {
        this.arguments = arguments;
        this.selectionKey = selectionKey;
    }

    public void validateArguments() {
        if (selectionKey == null) {
            throw new IllegalArgumentException("SelectionKey of command cannot be null.");
        }
    }

    public abstract String execute();
}
