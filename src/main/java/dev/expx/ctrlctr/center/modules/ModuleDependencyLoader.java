package dev.expx.ctrlctr.center.modules;

import dev.expx.ctrlctr.center.util.DirectMavenResolver;

/**
 * Represents a module dependency loader.
 */
@SuppressWarnings("unused")
public abstract class ModuleDependencyLoader {

    /**
     * Utility class, do not instantiate.
     */
    private ModuleDependencyLoader() {}

    /**
     * Loads the dependencies for the module.
     * @param mavenResolver The maven resolver to use.
     */
    public abstract void loadDependencies(DirectMavenResolver mavenResolver);

}
