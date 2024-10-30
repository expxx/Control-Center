package dev.expx.ctrlctr.center.util.dependencies.resolver.lib;

import org.jetbrains.annotations.NotNull;

/**
 * @author PaperMC
 */
public interface ClassPathLibrary {
    void register(@NotNull LibraryStore var1) throws LibraryLoadingException;
}

