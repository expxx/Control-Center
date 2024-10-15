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

    /**
     * The logger instance.
     */
    final public static Logger logger = Ctrlctr.getInstance().getLogger();

    /**
     * Logs a message to the console.
     * @param level The level of the message.
     * @param msg The message to log.
     * @param args The arguments to format the message with.
     */
    public static void log(Level level, String msg, Object... args) {
        logger.log(level, MessageFormat.format(msg, args));
    }
}
