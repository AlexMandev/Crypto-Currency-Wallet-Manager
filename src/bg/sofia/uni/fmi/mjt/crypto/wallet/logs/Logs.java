package bg.sofia.uni.fmi.mjt.crypto.wallet.logs;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logs {
    private static final Logger LOGGER = Logger.getLogger(Logs.class.getName());
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String LOGS_PATH = "logs";
    private static final String LOGS_FILE = "errors.log";

    static {
        setupLogger();
    }

    private static void setupLogger() {
        try {
            Path logDir = Paths.get(LOGS_PATH);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }

            FileHandler fileHandler = new FileHandler(
                    LOGS_PATH + File.separator + LOGS_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);

            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static void logError(String message, Throwable throwable) {
        LOGGER.log(Level.ALL, message, throwable);
        System.err.println("ERROR - " + message + " " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN)));
    }
}
