package dev.expx.ctrlctr.mongodb.internal.diagnostics.logging;

import org.jetbrains.annotations.NotNull;

public final class Loggers {
    private static final String PREFIX = "org.mongodb.driver";

    public static Logger getLogger(@NotNull final String suffix) {
        return new NoOpLogger(PREFIX + "." + suffix);
    }
}