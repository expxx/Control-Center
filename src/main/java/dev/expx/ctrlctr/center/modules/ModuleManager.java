package dev.expx.ctrlctr.center.modules;

import com.moandjiezana.toml.Toml;
import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.lang.LangLoader;
import dev.expx.ctrlctr.center.logger.errors.ModuleLoadException;
import dev.expx.ctrlctr.center.util.DirectMavenResolver;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Module Manager
 */
@ApiStatus.Internal
public class ModuleManager {

    private static final Lang lang = Ctrlctr.getLang();

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
                    throw new RuntimeException(Ctrlctr.getLang().lang("module-error-main-dir"));
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
                        logger.info(lang.lang("module-load-found", mod.data.id()));
                        mod.setModulePublicPath(new File(dataDir.toFile(), "modules/" + mod.getLoader().getModuleInfo().name().toLowerCase()));
                        Ctrlctr.getModules().put(mod.getData().id(), mod);
                    } else {
                        logger.error(lang.lang("module-load-missing-toml", mod.getData().name()));
                    }
                } catch (Exception e) {
                    logger.error(lang.lang("module-load-module-error", e.getMessage()));
                }
            }
        } catch(Exception e) {
            logger.error(Ctrlctr.getLang().lang("module-load-error"), e.getMessage());
        }
    }

    /**
     * Load all dependencies for all modules
     * @param res Direct Maven Resolver
     */
    public static void dependencyModules(DirectMavenResolver res, Path dir) {
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
            Lang lang = new Lang(new LangLoader(Ctrlctr.class, "lang", "en", "US", dir).getBundle());
            try {
                logger.info(lang.lang("module-dep-loading", dep.getClass().getName()));
                Thread.currentThread().setContextClassLoader(moduleLoader);
                dep.loadDependencies(res);
                Thread.currentThread().setContextClassLoader(currentLoader);
                logger.info(lang.lang("module-dep-loaded", dep.getClass().getName()));
            } catch (Exception e) {
                logger.error(lang.lang("module-dep-error", e.getMessage()));
            }
        }
    }

    /**
     * Check for module updates if
     * the update folder exists
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Thread updateFolder(@NotNull Path dataDir) {
        return new Thread(() -> {
            Lang lang = new Lang(new LangLoader(Ctrlctr.class, "lang", "en", "US", dataDir).getBundle());
            logger.info(lang.lang("module-upd-check"));
            Path updatePath = new File(dataDir.toFile(), "updates").toPath();
            Path modulePath = new File(dataDir.toFile(), "modules").toPath();

            try {
                if(!updatePath.toFile().exists() || !modulePath.toFile().exists()) {
                    updatePath.toFile().mkdirs();
                    modulePath.toFile().mkdirs();
                }
                if(updatePath.toFile().listFiles() == null) return;
                List<File> jars = Arrays.asList(Objects.requireNonNull(updatePath.toFile().listFiles()));
                jars.forEach(e -> {
                    try {
                        String updateId = getModuleId(e.toPath());
                        if(updateId != null) {
                            if(modulePath.toFile().listFiles() == null) return;
                            File[] moduleJars = Objects.requireNonNull(modulePath.toFile().listFiles());
                            for(File moduleJar : moduleJars) {
                                if(moduleJar.isDirectory()) continue;
                                String moduleId = getModuleId(moduleJar.toPath());
                                if(moduleId != null && moduleId.equals(updateId)) {
                                    Files.delete(moduleJar.toPath());
                                    Files.move(e.toPath(), moduleJar.toPath());
                                    logger.info(lang.lang("module-upd-update", updateId));
                                    break;
                                }
                            }
                        }
                        logger.info(lang.lang("module-upd-error", e.getName()));
                    } catch (IOException ex) {
                        throw new ModuleLoadException(ex.getMessage());
                    }
                });
            } catch (Exception e) {
                throw new ModuleLoadException(e.getMessage());
            }
        });
    }

    /**
     * Install all modules
     */
    public static void installModules() {
        logger.info(lang.lang("module-install-loading", Ctrlctr.getModules().size()));
        for(Module module : Ctrlctr.getModules().values()) {
            ModuleInfo info = module.getData();
            module.setPlugin(Ctrlctr.getInstance());
            try {
                logger.info(lang.lang("module-install-installing", module.getData().name()));
                if (info.deps() != null && !info.deps().isEmpty()) {
                    for (String id : info.deps()) {
                        if (!validateDependency(id, module)) continue;
                        Module depend = Ctrlctr.getModules().get(id);
                        depend.create();
                        if (depend.getError() != null) {
                            logger.error(lang.lang("module-install-failed-dep", info.name(), depend.getData().name(), depend.getError()));
                            module.destroy();
                            break;
                        }
                        depend.setActive(true);
                    }
                }
            } catch(Exception e) {
                logger.error(lang.lang("module-install-failed", e.getMessage()));
            }
            try {
                module.create();
                if (module.getError() != null) {
                    logger.error(lang.lang("module-install-failed-create", module.getData().name(), module.getError()));
                    module.destroy();
                    continue;
                }
                module.setActive(true);
                logger.error(lang.lang("module-install-success", module.getData().name()));
            } catch(Exception e) {
                logger.error(lang.lang("module-install-failed", e.getMessage()));
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
            logger.error(lang.lang("module-install-missing-dep", parent.getData().name(), dependency));
            return false;
        }
        Module depend = Ctrlctr.getModules().get(dependency);
        if(depend.isActive() || depend.getData().deps().contains(parent.getData().id())) {
            logger.error(lang.lang("module-install-circular-dep", parent.getData().name(), depend.getData().name()));
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
                logger.error(lang.lang("module-unload-fail", e.getMessage()));
            }
            module.setActive(false);
        }
    }

    protected static String getModuleId(Path jarFilePath) throws IOException {
        try (JarFile jarFile = new JarFile(jarFilePath.toFile())) {
            JarEntry tomlEntry = jarFile.getJarEntry("module.toml");

            if (tomlEntry != null) {
                logger.info(lang.lang("module-file-found", tomlEntry.getName()));
                try (InputStream inputStream = jarFile.getInputStream(tomlEntry)) {
                    Toml toml = new Toml().read(inputStream);
                    logger.info(lang.lang("module-toml-found", toml.getString("name")));
                    return toml.getString("id");
                }
            }
        }
        return null;
    }
}
