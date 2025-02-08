package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.UnknownCommandException;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.AssetsCatalog;
import bg.sofia.uni.fmi.mjt.crypto.wallet.storage.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class CommandFactoryTest {

    private static CommandFactory commandFactory;
    private static SelectionKey selectionKeyMock;
    private static UserRepository userRepositoryMock;
    private static AssetsCatalog assetsCatalogMock;

    @BeforeAll
    static void setUp() {
        userRepositoryMock = mock();
        assetsCatalogMock = mock();
        selectionKeyMock = mock();
        commandFactory = new CommandFactory(userRepositoryMock, assetsCatalogMock);
    }

    @Test
    void testUnknownCommandThrowsException() {
        assertThrows(UnknownCommandException.class,
                () -> commandFactory.of("unknownCommand", selectionKeyMock),
                "Unknown command should throw UnknownCommandException.");
    }

    @Test
    void testNullUserRepository() {
        assertThrows(IllegalArgumentException.class,
                () -> new CommandFactory(null, assetsCatalogMock),
                "Should throw for null userRepository.");
    }

    @Test
    void testNullAssetsCatalog() {
        assertThrows(IllegalArgumentException.class,
                () -> new CommandFactory(userRepositoryMock, null),
                "Should throw for null assetsCatalog.");
    }

    @Test
    void testNullInputLine() {
        assertThrows(IllegalArgumentException.class,
                () -> commandFactory.of(null, selectionKeyMock),
                "Should throw for null input.");
    }

    @Test
    void testOfReturnsHelpCommand() throws UnknownCommandException {
        assertInstanceOf(HelpCommand.class,
                commandFactory.of("help", selectionKeyMock),
                "Should return instance of a HelpCommand.");
    }

    @Test
    void testOfReturnsListOfferingsCommand() throws UnknownCommandException {
        assertInstanceOf(ListOfferingsCommand.class,
                commandFactory.of("list-offerings", selectionKeyMock),
                "Should return ListOfferingsCommand instance.");
    }

    @Test
    void testOfReturnsRegisterCommand() throws UnknownCommandException {
        assertInstanceOf(RegisterCommand.class,
                commandFactory.of("register username password123", selectionKeyMock),
                "Should return RegisterCommand instance.");
    }

    @Test
    void testOfReturnsLoginCommand() throws UnknownCommandException {
        assertInstanceOf(LoginCommand.class,
                commandFactory.of("login username password123", selectionKeyMock),
                "Should return LoginCommand instance.");
    }

    @Test
    void testOfReturnsDepositCommand() throws UnknownCommandException {
        assertInstanceOf(DepositCommand.class,
                commandFactory.of("deposit 10.00", selectionKeyMock),
                "Should return DepositCommand instance.");
    }

    @Test
    void testOfReturnsLogoutCommand() throws UnknownCommandException {
        assertInstanceOf(LogoutCommand.class,
                commandFactory.of("logout", selectionKeyMock),
                "Should return LogoutCommand instance.");
    }

    @Test
    void testOfReturnsBuyCommand() throws UnknownCommandException {
        assertInstanceOf(BuyCommand.class,
                commandFactory.of("buy BTC 100.00", selectionKeyMock),
                "Should return BuyCommand instance.");
    }

    @Test
    void testOfReturnsSellCommand() throws UnknownCommandException {
        assertInstanceOf(SellCommand.class,
                commandFactory.of("sell BTC", selectionKeyMock),
                "Should return SellCommand instance.");
    }

    @Test
    void testOfReturnsWalletSummaryCommand() throws UnknownCommandException {
        assertInstanceOf(WalletSummaryCommand.class,
                commandFactory.of("get-wallet-summary", selectionKeyMock),
                "Should return WalletSummaryCommand instance.");
    }

    @Test
    void testOfReturnsWalletOverallSummaryCommand() throws UnknownCommandException {
        assertInstanceOf(WalletOverallSummaryCommand.class,
                commandFactory.of("get-wallet-overall-summary", selectionKeyMock),
                "Should return WalletOverallSummaryCommand instance.");
    }

    @Test
    void testOfReturnsWithdrawCommand() throws UnknownCommandException {
        assertInstanceOf(WithdrawCommand.class,
                commandFactory.of("withdraw 10.00", selectionKeyMock),
                "Should return WithdrawCommand instance.");
    }
}
