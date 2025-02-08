package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;

import java.nio.channels.SelectionKey;

public class WithdrawCommand extends Command {

    private static final int WITHDRAW_ARGUMENTS_COUNT = 1;
    private static final String INVALID_AMOUNT_MESSAGE = "The withdrawal amount must be a valid positive number.";
    private static final String INSUFFICIENT_ARGUMENTS_MESSAGE = "You need to specify an amount to withdraw.";
    private static final String SUCCESSFUL_WITHDRAWAL_MESSAGE = "Withdrawal successful.";
    private static final String INVALID_ARGUMENTS_COUNT = "Invalid number of arguments for this command.";
    private static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient funds to withdraw.";
    private static final String BALANCE = "Current balance: ";

    private double amount;

    public WithdrawCommand(String[] arguments, SelectionKey selectionKey) {
        super(arguments, selectionKey);
        validateArguments();
    }

    @Override
    public String execute() {
        if (selectionKey.attachment() == null) {
            return NOT_LOGGED_IN_MESSAGE;
        }
        User user = (User) selectionKey.attachment();

        try {
            user.getWallet().withdraw(amount);
        } catch (InsufficientBalanceException e) {
            return INSUFFICIENT_BALANCE_MESSAGE;
        }

        return SUCCESSFUL_WITHDRAWAL_MESSAGE + BALANCE + user.getWallet().getBalance();
    }

    @Override
    public void validateArguments() {
        super.validateArguments();
        if (arguments.length == 0) {
            throw new CommandArgumentCountException(INSUFFICIENT_ARGUMENTS_MESSAGE);
        }
        if (arguments.length != WITHDRAW_ARGUMENTS_COUNT) {
            throw new CommandArgumentCountException(INVALID_ARGUMENTS_COUNT);
        }
        try {
            amount = Double.parseDouble(arguments[0]);
        } catch (NumberFormatException e) {
            throw new InvalidCommandArgumentException(INVALID_AMOUNT_MESSAGE, e);
        }
        if (amount <= 0) {
            throw new InvalidCommandArgumentException(INVALID_AMOUNT_MESSAGE);
        }
    }
}
