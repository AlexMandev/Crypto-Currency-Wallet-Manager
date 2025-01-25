package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.AssetNotOwnedException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnavailableAssetException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;

import java.nio.channels.SelectionKey;

public class SellCommand extends Command {

    private static final int SELL_ARGUMENTS_COUNT = 1;
    private static final String SUCCESSFUL_SELL_MESSAGE = "Sell successful.";
    private static final String ASSET_NOT_OWNED_MESSAGE = "You do not own this asset.";
    private static final String ASSET_UNAVAILABLE_MESSAGE = "The asset with the given ID is unavailable.";
    private static final String INSUFFICIENT_ARGUMENTS_MESSAGE = "You need to provide an asset ID.";
    private static final String INVALID_ARGUMENTS_COUNT_MESSAGE = "Invalid number of arguments for this command."
            + HELP_MESSAGE;
    private static final String UPDATED_BALANCE = "Updated balance: ";

    private final AssetsCatalog assetsCatalog;

    public SellCommand(String[] arguments, SelectionKey selectionKey, AssetsCatalog assetsCatalog) {
        super(arguments, selectionKey);
        this.assetsCatalog = assetsCatalog;
        validateArguments();
    }

    @Override
    public String execute() {
        if (selectionKey.attachment() == null) {
            return NOT_LOGGED_IN_MESSAGE;
        }

        User user = (User) selectionKey.attachment();
        String assetId = arguments[0];

        try {
            user.getWallet().sell(assetId, assetsCatalog);
        } catch (AssetNotOwnedException e) {
            return ASSET_NOT_OWNED_MESSAGE;
        } catch (UnavailableAssetException e) {
            return ASSET_UNAVAILABLE_MESSAGE;
        } catch (Exception e) {
            // log error
        }

        return SUCCESSFUL_SELL_MESSAGE + " " + UPDATED_BALANCE + user.getWallet().getBalance();
    }

    @Override
    public void validateArguments() {
        super.validateArguments();
        if (arguments.length == 0) {
            throw new CommandArgumentCountException(INSUFFICIENT_ARGUMENTS_MESSAGE);
        }
        if (arguments.length != SELL_ARGUMENTS_COUNT) {
            throw new CommandArgumentCountException(INVALID_ARGUMENTS_COUNT_MESSAGE);
        }
    }
}
