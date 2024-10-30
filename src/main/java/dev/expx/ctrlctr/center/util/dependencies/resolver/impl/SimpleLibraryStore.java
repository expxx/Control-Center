package dev.expx.ctrlctr.center.util.dependencies.resolver.impl;

import dev.expx.ctrlctr.center.util.dependencies.resolver.lib.LibraryStore;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author PaperMC
 */
public class SimpleLibraryStore implements LibraryStore {
    private final List<Path> paths = new ArrayList();

    public SimpleLibraryStore() {
    }

    public void addLibrary(@NotNull Path library) {
        this.paths.add(library);
    }

    public List<Path> getPaths() {
        return this.paths;
    }
}
