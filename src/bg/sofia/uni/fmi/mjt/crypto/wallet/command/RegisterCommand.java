package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.InvalidCommandArgumentException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.UserRepository;
import bg.sofia.uni.fmi.mjt.crypto.wallet.user.User;

import java.nio.channels.SelectionKey;

public class RegisterCommand extends Command {
    private static final int REGISTER_ARGUMENTS_COUNT = 2;

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 20;

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 50;

    private static final String NO_ARGUMENTS_MESSAGE = "You need to enter a username and a password to register.";
    private static final String ONLY_USERNAME_MESSAGE = "You need to enter a password to register.";
    private static final String MORE_ARGUMENTS_MESSAGE = "Invalid number of arguments for this command. "
            + HELP_MESSAGE;

    private static final String USERNAME_LENGTH_MESSAGE = "Username must be between " + MIN_USERNAME_LENGTH + " and " +
            MAX_USERNAME_LENGTH + " characters long.";

    private static final String USERNAME_FORMAT_MESSAGE = "Username must contain only lower and uppercase letters, " +
            "underscores or dashes. It can't start with a dash or underscore.";

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$";

    private static final String PASSWORD_LENGTH_MESSAGE = "Password must be between " + MIN_PASSWORD_LENGTH +
            " and " + MAX_PASSWORD_LENGTH + " characters long.";

    private static final String ALREADY_LOGGED_IN = "You are already logged in.";
    private static final String SUCCESSFUL_REGISTRATION = "You have registered successfully.";

    private final UserRepository userRepository;

    public RegisterCommand(String[] arguments, SelectionKey key, UserRepository userRepository) {
        super(arguments, key);
        validateArguments();
        this.userRepository = userRepository;
    }

    @Override
    public void validateArguments() {
        super.validateArguments();
        if (arguments.length == 0) {
            throw new CommandArgumentCountException(NO_ARGUMENTS_MESSAGE);
        } else if (arguments.length == 1) {
            throw new CommandArgumentCountException(ONLY_USERNAME_MESSAGE);
        } else if (arguments.length > REGISTER_ARGUMENTS_COUNT) {
            throw new CommandArgumentCountException(MORE_ARGUMENTS_MESSAGE);
        }
        validateUsername();
        validatePassword();
    }

    @Override
    public String execute() {
        if (selectionKey.attachment() != null) {
            return ALREADY_LOGGED_IN;
        }
        try {
            String username = arguments[0];
            String password = arguments[1];
            User user = new User(username, password);
            userRepository.addUser(user);
        } catch (UserAlreadyExistsException e) {
            return e.getMessage();
        } catch (Exception e) {
            // log error
        }
        return SUCCESSFUL_REGISTRATION;
    }

    private void validateUsername() {
        if (arguments[0].length() < MIN_USERNAME_LENGTH  || arguments[0].length() > MAX_USERNAME_LENGTH) {
            throw new InvalidCommandArgumentException(USERNAME_LENGTH_MESSAGE);
        }
        if (!arguments[0].matches(USERNAME_REGEX)) {
            throw new InvalidCommandArgumentException(USERNAME_FORMAT_MESSAGE);
        }
    }

    private void validatePassword() {
        if (arguments[1].length() < MIN_PASSWORD_LENGTH  || arguments[1].length() > MAX_PASSWORD_LENGTH) {
            throw new InvalidCommandArgumentException(PASSWORD_LENGTH_MESSAGE);
        }
    }
}
