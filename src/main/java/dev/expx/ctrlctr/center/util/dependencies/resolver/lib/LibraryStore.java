package dev.expx.ctrlctr.center.util.dependencies.resolver.lib;

import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * @author PaperMC
 */
@Internal
public interface LibraryStore {
    void addLibrary(@NotNull Path var1);
}