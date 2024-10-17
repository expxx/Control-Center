package dev.expx.ctrlctr.center.modules;

import dev.expx.ctrlctr.center.util.DirectMavenResolver;

/**
 * Represents a module dependency loader.
 */
@SuppressWarnings("unused")
public abstract class ModuleDependencyLoader {

    /**
     * Loads the dependencies for the module.
     * @param mavenResolver The maven resolver to use.
     * @usage See {@link DirectMavenResolver} for usage.
     */
    public abstract void loadDependencies(DirectMavenResolver mavenResolver);

}
