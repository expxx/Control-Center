package dev.expx.ctrlctr.center.logger.errors;

/**
 * Thrown when an error occurs while loading a configuration file.
 */
public class ConfigLoadException extends RuntimeException {

    /**
     * Constructs a new argument syntax exception with the given message.
     * @param msg The message of the exception.
     */
    public ConfigLoadException(String msg) {
        super(msg);
    }

}
