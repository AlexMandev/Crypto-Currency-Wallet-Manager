package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnavailableAssetException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;

import java.nio.channels.SelectionKey;

public class BuyCommand extends Command {

    private static final int BUY_ARGUMENTS_COUNT = 2; // assetId and dollarAmount
    private static final String INSUFFICIENT_ARGUMENTS_MESSAGE = "You need to provide an asset ID and an amount.";
    private static final String INVALID_ARGUMENTS_COUNT_MESSAGE = "Invalid number of arguments for this command.";
    private static final String INVALID_DOLLAR_AMOUNT_MESSAGE = "The amount must be a positive number.";
    private static final String SUCCESSFUL_PURCHASE_MESSAGE = "Purchase successful.";
    private static final String INSUFFICIENT_BALANCE_MESSAGE =
            "Insufficient balance to complete the purchase. Please check your wallet's balance";
    private static final String UNAVAILABLE_ASSET_MESSAGE = "The asset with the given ID is unavailable.";
    private static final String REMAINING_BALANCE = "Remaining balance: ";

    private final AssetsCatalog assetsCatalog;
    private double amount;
    public BuyCommand(String[] arguments, SelectionKey selectionKey, AssetsCatalog assetsCatalog) {
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
            user.getWallet().buy(assetId, amount, assetsCatalog);
        } catch (InsufficientBalanceException e) {
            return INSUFFICIENT_BALANCE_MESSAGE;
        } catch (UnavailableAssetException e) {
            return UNAVAILABLE_ASSET_MESSAGE;
        } catch (Exception e) {
            //log error
        }

        return SUCCESSFUL_PURCHASE_MESSAGE + " " + REMAINING_BALANCE + user.getWallet().getBalance();
    }

    @Override
    public void validateArguments() {
        super.validateArguments();
        if (arguments.length == 0) {
            throw new CommandArgumentCountException(INSUFFICIENT_ARGUMENTS_MESSAGE);
        }
        if (arguments.length != BUY_ARGUMENTS_COUNT) {
            throw new CommandArgumentCountException(INVALID_ARGUMENTS_COUNT_MESSAGE);
        }
        try {
            amount = Double.parseDouble(arguments[1]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandArgumentException(INVALID_DOLLAR_AMOUNT_MESSAGE);
        }

    }
}
