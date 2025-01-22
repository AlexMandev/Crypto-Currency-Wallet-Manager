package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnknownCommandException;

public class CommandFactory {
    public Command of(String input) throws UnknownCommandException {
        throw new UnknownCommandException("Unknown command.");
    }
}
