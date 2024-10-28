package dev.expx.ctrlctr.mongodb.internal.diagnostics.logging;

class NoOpLogger implements Logger {
    private final String name;

    NoOpLogger(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
