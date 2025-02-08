package bg.sofia.uni.fmi.mjt.crypto.wallet.command;

import bg.sofia.uni.fmi.mjt.crypto.wallet.exception.CommandArgumentCountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SelectionKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class HelpCommandTest {

    private static SelectionKey selectionKey;

    @BeforeEach
    void setUp() {
        selectionKey = mock();
    }

    @Test
    void testSuccessfulHelpExecution() {
        String[] arguments = {};
        Command helpCommand = new HelpCommand(arguments, selectionKey);
        helpCommand.execute();
        verify(selectionKey, times(0)).attachment();
    }

    @Test
    void testInvalidArgumentsThrowsException() {
        String[] arguments = {"extraArg"};
        assertThrows(CommandArgumentCountException.class, () -> new HelpCommand(arguments, selectionKey),
                "HelpCommand should throw an exception if arguments are provided.");
    }
}