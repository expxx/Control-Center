package dev.expx.ctrlctr.center.logger.errors;

/**
 * Exception thrown when an error occurs during module update
 */
public class ModuleUpdateException extends RuntimeException {

    /**
     * Create a new module update exception
     * @param message Message
     */
    public ModuleUpdateException(String message) {
        super(message);
    }
}
