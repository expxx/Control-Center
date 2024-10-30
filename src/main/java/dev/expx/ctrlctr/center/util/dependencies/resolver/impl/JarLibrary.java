package dev.expx.ctrlctr.center.util.dependencies.resolver.impl;

import dev.expx.ctrlctr.center.util.dependencies.resolver.lib.ClassPathLibrary;
import dev.expx.ctrlctr.center.util.dependencies.resolver.lib.LibraryLoadingException;
import dev.expx.ctrlctr.center.util.dependencies.resolver.lib.LibraryStore;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

/**
 * @author PaperMC
 */
public class JarLibrary implements ClassPathLibrary {
    private final Path path;

    public JarLibrary(@NotNull Path path) {
        this.path = path;
    }

    public void register(@NotNull LibraryStore store) throws LibraryLoadingException {
        if (Files.notExists(this.path)) {
            throw new LibraryLoadingException("Could not find library at " + this.path);
        } else {
            store.addLibrary(this.path);
        }
    }
}
