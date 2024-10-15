package dev.expx.ctrlctr.center.logger.errors;

/**
 * Thrown when an error occurs while connecting to a storage system.
 */
@SuppressWarnings("unused")
public class StorageConnectionException extends RuntimeException {
    /**
     * Constructs a new argument syntax exception with the given message.
     * @param message The message of the exception.
     */
    public StorageConnectionException(String message) {
        super(message);
    }
}
