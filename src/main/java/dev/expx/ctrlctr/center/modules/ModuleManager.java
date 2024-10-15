package dev.expx.ctrlctr.center.modules;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.logger.Log;
import dev.expx.ctrlctr.center.util.DirectMavenResolver;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

/**
 * Module Manager
 */
@ApiStatus.Internal
public class ModuleManager {

    private ModuleManager() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Utility Class");
    }
    /**
     * Logger for ModuleManager
     */
    private static final Logger logger = LoggerFactory.getLogger(ModuleManager.class);


    /**
     * Set up the Module Manager
     * @param dataDir Data directory for the plugin
     */
    public static void setup(File dataDir) {
        Ctrlctr.getToRegister().add(new ModuleCommand("module", "Module Management", new dev.expx.ctrlctr.center.modules.commands.ModuleCommand()));
        try {
            setupModuleLoader(dataDir.toPath());
            loadModules(dataDir.toPath());
        } finally {
            installModules();
        }
    }

    /**
     * Module loader
     */
    protected static URLClassLoader moduleLoader;

    /**
     * Set up the module loader
     * @param dataDir Data directory for the plugin
     */
    public static void setupModuleLoader(Path dataDir) {
        try {
            final File moduleDir = new File(dataDir.toFile(), "modules");
            if (!moduleDir.exists() || !moduleDir.isDirectory()) {
                boolean success = moduleDir.mkdirs();
                if (!success)
                    throw new RuntimeException("Failed to create modules directory.");
            }
            if (moduleDir.listFiles() == null) return;
            List<File> jars = Arrays.asList(Objects.requireNonNull(moduleDir.listFiles()));
            URL[] urls = new URL[jars.size()];
            for (int i = 0; i < jars.size(); i++) {
                try {
                    urls[i] = jars.get(i).toURI().toURL();
                } catch (Exception ignored) {
                }
            }
            moduleLoader = new ModuleClassLoader(urls, ModuleManager.class.getClassLoader(), dataDir.toFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Load all modules from the modules directory
     * @param dataDir Data directory for the plugin
     */
    public static void loadModules(@NotNull Path dataDir) {
        try {
            final ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
            final Class<Module> moduleClass = Module.class;
            ServiceLoader<Module> serviceLoader;
            try {
                Thread.currentThread().setContextClassLoader(moduleLoader);
                serviceLoader = ServiceLoader.load(moduleClass);
            } finally {
                Thread.currentThread().setContextClassLoader(currentLoader);
            }
            for (Module mod : serviceLoader) {
                try {
                    Thread.currentThread().setContextClassLoader(moduleLoader);
                    mod.setLoader(
                            new ModuleClassLoader(new URL[]{mod.jarLocation}, ModuleManager.class.getClassLoader(), dataDir.toFile())
                    );
                    mod.data = mod.getLoader().getModuleInfo();
                    mod.setPluginDataDir(dataDir.toFile());
                    Thread.currentThread().setContextClassLoader(currentLoader);
                    if (mod.data != null) {
                        logger.info("Found module: {}", mod.getData().name);
                        mod.setModulePublicPath(new File(dataDir.toFile(), "modules/" + mod.getLoader().getModuleInfo().name.toLowerCase()));
                        Ctrlctr.getModules().put(mod.getData().id, mod);
                    } else {
                        logger.error("Failed to load module: {}: Missing module.toml", mod.getData().name);
                    }
                } catch (Exception e) {
                    logger.info("Failed to load module: {}: {}", mod.getData().name, e.getMessage());
                }
            }
        } catch(Exception e) {
            logger.error("Failed to load modules: {}", e.getMessage());
        }
    }

    /**
     * Load all dependencies for all modules
     * @param res Direct Maven Resolver
     */
    public static void dependencyModules(DirectMavenResolver res) {
        final ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        final Class<ModuleDependencyLoader> depClass = ModuleDependencyLoader.class;
        ServiceLoader<ModuleDependencyLoader> serviceLoader;
        try {
            Thread.currentThread().setContextClassLoader(moduleLoader);
            serviceLoader = ServiceLoader.load(depClass);
        } finally {
            Thread.currentThread().setContextClassLoader(currentLoader);
        }
        for (ModuleDependencyLoader dep : serviceLoader) {
            try {
                logger.info("Loading dependencies for {}", dep.getClass().getName());
                Thread.currentThread().setContextClassLoader(moduleLoader);
                dep.loadDependencies(res);
                Thread.currentThread().setContextClassLoader(currentLoader);
                logger.info("Loaded dependencies for {}", dep.getClass().getName());
            } catch (Exception e) {
                logger.error("Failed to load dependencies: {}", e.getMessage());
            }
        }
    }

    /**
     * Install all modules
     */
    public static void installModules() {
        Log.log(Level.INFO, "Loading {0} modules", Ctrlctr.getModules().size());
        for(Module module : Ctrlctr.getModules().values()) {
            ModuleInfo info = module.getData();
            module.setPlugin(Ctrlctr.getInstance());
            try {
                logger.info("Installing Module: {}", info.name);
                if (info.deps != null && !info.deps.isEmpty()) {
                    for (String id : info.deps) {
                        if (!validateDependency(id, module)) continue;
                        Module depend = Ctrlctr.getModules().get(id);
                        depend.create();
                        if (depend.getError() != null) {
                            logger.error("Module {} failed to install dependency {}.", info.name, depend.getData().name);
                            logger.error("Error: {}", depend.getError());
                            module.destroy();
                            break;
                        }
                        depend.setActive(true);
                    }
                }
            } catch(Exception e) {
                logger.error("Failed to install module: {}", e.getMessage());
            }
            try {
                module.create();
                if (module.getError() != null) {
                    logger.error("Module {} failed to install.", info.name);
                    logger.error("Error: {}", module.getError());
                    module.destroy();
                    continue;
                }
                module.setActive(true);
                logger.info("Module {} installed.", info.name);
            } catch(Exception e) {
                logger.error("Failed to install module: {}", e.getMessage());
            }
        }
    }

    /**
     * Validate a dependency
     * @param dependency Dependency to validate
     * @param parent Parent module
     * @return True if the dependency is valid
     */
    protected static boolean validateDependency(String dependency, Module parent) {
        if(!Ctrlctr.getModules().containsKey(dependency)) {
            logger.error("Module {} missing dependency {}", parent.getData().name, dependency);
            return false;
        }
        Module depend = Ctrlctr.getModules().get(dependency);
        if(depend.isActive() || depend.getData().deps.contains(parent.getData().id)) {
            logger.error("Module {} depending on dependency {} which is requiring module.", parent.getData().name, dependency);
            return false;
        }
        return true;
    }

    /**
     * Unload all modules
     */
    public static void trashModules() {
        for(Module module : Ctrlctr.getModules().values()) {
            try {
                module.destroy();
            } catch(Exception e) {
                logger.error("Failed to unload module: {}", e.getMessage());
            }
            module.setActive(false);
        }
    }
}
