package dev.expx.ctrlctr.center.util.dependencies.classpath;

import java.nio.file.Path;

public interface ClassPathAppender extends AutoCloseable {

    void addJarToClasspath(Path file);

    @Override
    default void close() {

    }
}

