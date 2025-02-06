package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;

import java.nio.channels.SelectionKey;

public class WalletSummaryCommand extends Command {

    private static final int WALLET_SUMMARY_ARGUMENTS_COUNT = 0;
    private static final String INVALID_ARGUMENTS_COUNT_MESSAGE =
            "The get-wallet-summary command doesn't have any arguments";

    public WalletSummaryCommand(String[] arguments, SelectionKey selectionKey) {
        super(arguments, selectionKey);
        validateArguments();
    }

    @Override
    public String execute() {
        if (selectionKey.attachment() == null) {
            return NOT_LOGGED_IN_MESSAGE;
        }

        User user = (User) selectionKey.attachment();

        return user.getWallet().getSummary().toString();
    }

    @Override
    public void validateArguments() {
        super.validateArguments();
        if (arguments.length != WALLET_SUMMARY_ARGUMENTS_COUNT) {
            throw new CommandArgumentCountException(INVALID_ARGUMENTS_COUNT_MESSAGE);
        }
    }
}
