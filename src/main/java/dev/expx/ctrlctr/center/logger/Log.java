package dev.expx.ctrlctr.center.logger;

import dev.expx.ctrlctr.center.Ctrlctr;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple logger for the plugin.
 */
public class Log {

    /**
     * Utility class, do not instantiate.
     */
    private Log() {}
    
    public static final Logger logger = Ctrlctr.getInstance().getLogger();

    /**
     * Logs a message to the console through
     * the plugin's logger.
     * @param level The level of the message.
     * @param msg The message to log.
     * @param args The arguments to format the message with.
     * @deprecated Use {@link Log#getLogger(Class)} instead.
     * @since 1.0.8
     */
    @Deprecated(since = "1.0.8")
    public static void log(Level level, String msg, Object... args) {
        logger.log(level, MessageFormat.format(msg, args));
    }

    /**
     * Get the logger for a class.
     * @param clazz The class to get the logger for.
     * @return The logger.
     */
    public static org.slf4j.Logger getLogger(Class<?> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }
}
