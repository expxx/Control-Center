package dev.expx.ctrlctr.center.logger;

import dev.expx.ctrlctr.center.Ctrlctr;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple logger for the plugin.
 */
@SuppressWarnings("unused")
public class Log {

    /**
     * Utility class, do not instantiate.
     */
    private Log() {}

    /**
     * Get the logger for a class.
     * @param clazz The class to get the logger for.
     * @return The logger.
     */
    public static org.slf4j.Logger getLogger(Class<?> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }
}
