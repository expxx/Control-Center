package dev.expx.ctrlctr.center.logger.errors;

/**
 * Represents an exception that is thrown when RabbitMQ is already listening.
 */
@SuppressWarnings("unused")
public class RabbitAlreadyListening extends RuntimeException {
    /**
     * Constructs a new argument syntax exception with the given message.
     * @param message The message of the exception.
     */
    public RabbitAlreadyListening(String message) {
        super(message);
    }
}
