package dev.expx.ctrlctr.center.modules;

import com.moandjiezana.toml.Toml;
import dev.expx.ctrlctr.center.util.DirectMavenResolver;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

/**
 * ClassLoader for loading modules
 */
@ApiStatus.Internal
public class ModuleClassLoader extends URLClassLoader {

    /**
     * List of packages that should be loaded by the parent classloader
     */
    protected static final List<String> SHARED_PACKAGES = Arrays.asList(
            "dev.expx.ctrlctr.center",
            "dev.dejvokep.boostedyaml",
            "com.moandjiezana.toml",
            "io.papermc.paper",
            "io.socket.client",
            "io.socket.engineio",
            "org.bukkit",
            "net.kyori.adventure",
            "org.slf4j",
            "net.milkbowl.vault",
            "net.milkbowl.vault2",
            "org.eclipse.aether",
            "com.mongodb",
            "org.bson",
            "org.json",
            "java.sql",
            "kotlin.jvm",
            "okhttp3",
            "okio"
    );

    /**
     * Parent classloader
     */
    private final ClassLoader parentClassLoader;

    private final File dataDir;

    /**
     * Constructor
     * @param urls URLs to load
     * @param dataDir Data directory
     * @param parentClassLoader Parent classloader
     */
    public ModuleClassLoader(URL[] urls, ClassLoader parentClassLoader, File dataDir) {
        super(urls, null);
        this.parentClassLoader = parentClassLoader;
        this.dataDir = dataDir;
    }

    /**
     * Load a class
     * @param name
     *          The <a href="#binary-name">binary name</a> of the class
     *
     * @param resolve
     *          If {@code true} then resolve the class
     *
     * @return The {@link Class} object
     */
    @Override
    public Class<?> loadClass(String name, boolean resolve) {
        try {
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass == null) {
                boolean isShared = (
                        SHARED_PACKAGES.stream().anyMatch(name::startsWith) ||
                                DirectMavenResolver.SHARED_PACKAGES.stream().anyMatch(name::startsWith));
                if (new File(dataDir, "enableDebug").exists())
                    LoggerFactory.getLogger(ModuleClassLoader.class).info("Loading class {} (shared: {})", name, isShared);
                if (isShared) {
                    loadedClass = parentClassLoader.loadClass(name);
                } else {
                    loadedClass = super.loadClass(name, resolve);
                }
            }
            if (resolve)
                super.resolveClass(loadedClass);
            return loadedClass;
        } catch(ClassNotFoundException ex) {
            LoggerFactory.getLogger(ModuleClassLoader.class).error("Failed to load class {}: {}", name, ex.getMessage());

            return null;
        }
    }

    /**
     * Get the module info
     * @return A {@link ModuleInfo} object
     */
    public ModuleInfo getModuleInfo() {
        InputStream is = getResourceAsStream("module.toml");
        if(is == null) {
            LoggerFactory.getLogger(ModuleClassLoader.class).warn("Module found with no module.toml ({})", (Object) getURLs());
            return null;
        }
        return new Toml().read(is).to(ModuleInfo.class);
    }
}