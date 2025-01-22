package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;

import java.nio.channels.SelectionKey;

public class ListOfferingsCommand extends Command {
    private static final int LIST_OFFERINGS_ARGUMENTS_COUNT = 0;

    private final AssetsCatalog assetsCatalog;

    public ListOfferingsCommand(String[] arguments, SelectionKey selectionKey, AssetsCatalog assetsCatalog) {
        super(arguments, selectionKey);
        this.assetsCatalog = assetsCatalog;
        validateArguments();
    }

    @Override
    public void validateArguments() {
        super.validateArguments();
        if (arguments.length != LIST_OFFERINGS_ARGUMENTS_COUNT) {
            throw new CommandArgumentCountException("The list-offerings command doesn't have any arguments.");
        }
        if (assetsCatalog == null) {
            throw new IllegalArgumentException("AssetsCatalog cannot be null.");
        }
    }

    @Override
    public String execute() {
        return assetsCatalog.toString();
    }
}
