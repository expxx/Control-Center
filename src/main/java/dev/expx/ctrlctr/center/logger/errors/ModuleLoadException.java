package dev.expx.ctrlctr.center.logger.errors;

/**
 * Thrown when an error occurs while loading a module.
 */
@SuppressWarnings("unused")
public class ModuleLoadException extends RuntimeException {
    /**
     * Constructs a new argument syntax exception with the given message.
     * @param message The message of the exception.
     */
    public ModuleLoadException(String message) {
        super(message);
    }
}
