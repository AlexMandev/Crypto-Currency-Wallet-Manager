package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import java.nio.channels.SelectionKey;

public abstract class Command {

    protected final String[] arguments;
    protected final SelectionKey selectionKey;

    public Command(String[] arguments, SelectionKey selectionKey) {
        this.arguments = arguments;
        this.selectionKey = selectionKey;
    }

    public boolean validateArguments() {
        return arguments != null;
    }

    public abstract String execute();
}
