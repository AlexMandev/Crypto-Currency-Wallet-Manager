package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.UserRepository;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;

import java.nio.channels.SelectionKey;

public class LoginCommand extends Command {

    private static final int LOGIN_ARGUMENTS_COUNT = 2;

    private static final String NO_ARGUMENTS_MESSAGE = "You need to enter a username and a password to login.";
    private static final String ONLY_USERNAME_MESSAGE = "You need to enter a password to login.";
    private static final String MORE_ARGUMENTS_MESSAGE = "Invalid number of arguments for this command. "
            + HELP_MESSAGE;

    private static final String USERNAME_NOT_FOUND_MESSAGE = "Username not found. Please try again.";
    private static final String INCORRECT_PASSWORD_MESSAGE = "Incorrect password.";
    private static final String ALREADY_LOGGED_IN_MESSAGE = "You are already logged in.";
    private static final String SUCCESSFUL_LOGIN_MESSAGE = "You have logged in successfully.";

    private final UserRepository userRepository;

    public LoginCommand(String[] arguments, SelectionKey selectionKey, UserRepository userRepository) {
        super(arguments, selectionKey);
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null.");
        }
        this.userRepository = userRepository;
        validateArguments();
    }

    @Override
    public String execute() {
        if (selectionKey.attachment() != null) {
            return ALREADY_LOGGED_IN_MESSAGE;
        }

        String username = arguments[0];
        String password = arguments[1];

        User user = userRepository.getUser(username);
        if (user == null) {
            return USERNAME_NOT_FOUND_MESSAGE;
        }

        if (!user.matchesPassword(password)) {
            return INCORRECT_PASSWORD_MESSAGE;
        }

        selectionKey.attach(user);
        return SUCCESSFUL_LOGIN_MESSAGE;
    }

    @Override
    public void validateArguments() {
        super.validateArguments();
        if (arguments == null || arguments.length == 0) {
            throw new CommandArgumentCountException(NO_ARGUMENTS_MESSAGE);
        } else if (arguments.length == 1) {
            throw new CommandArgumentCountException(ONLY_USERNAME_MESSAGE);
        } else if (arguments.length > LOGIN_ARGUMENTS_COUNT) {
            throw new CommandArgumentCountException(MORE_ARGUMENTS_MESSAGE);
        }
    }

}