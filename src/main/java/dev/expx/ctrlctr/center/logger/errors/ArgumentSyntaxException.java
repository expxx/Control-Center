package dev.expx.ctrlctr.center.logger.errors;

/**
 * Thrown when an argument does not match the expected syntax.
 */
@SuppressWarnings("unused")
public class ArgumentSyntaxException extends RuntimeException {
    /**
     * Constructs a new argument syntax exception with the given message.
     * @param message The message of the exception.
     */
    public ArgumentSyntaxException(String message) {
        super(message);
    }
}
