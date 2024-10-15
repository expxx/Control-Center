package dev.expx.ctrlctr.center.modules;

import com.moandjiezana.toml.Toml;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.config.TOMLUtil;
import dev.expx.ctrlctr.center.config.YMLUtil;
import io.papermc.paper.command.brigadier.BasicCommand;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * The base class for all modules.
 */
public abstract class Module {

    /**
     * Utility class, do not instantiate.
     */
    private Module() {}

    /**
     * The plugin instance.
     */
    @Getter @Setter
    public Ctrlctr plugin;

    /**
     * A collection of {@link ModuleInfo} data.
     */
    @Getter
    public ModuleInfo data;
    /**
     * The location of the jar file.
     */
    public final URL jarLocation = this.getClass().getProtectionDomain().getCodeSource().getLocation();

    /**
     * The class loader for the module.
     */
    @Getter @Setter
    private ModuleClassLoader loader;

    /**
     * The public path for the module, you can store data here.
     */
    @Getter @Setter
    private File modulePublicPath;
    /**
     * The data directory for the plugin.
     */
    @Setter
    @SuppressWarnings("unused")
    private File pluginDataDir;
    /**
     * The list of commands for the module.
     */
    @Getter
    private final List<ModuleCommand> commands = new ArrayList<>();

    /**
     * Whether the module is active.
     */
    @Getter @Setter
    private boolean isActive = false;
    /**
     * An error message to log, if one has occurred. Null if no error.
     */
    @Getter @Setter
    private String error = null;


    /**
     * Called when the module first loads up.
     */
    public abstract void create();

    /**
     * Called when the module is disabled.
     */
    public abstract void destroy();

    /**
     * Called when the module is reloaded.
     * @param executor The command sender that reloaded the module.
     */
    public abstract void reload(CommandSender executor);

    /**
     * Saves a configuration file to the module's public path.
     * @param clazz The class to load the file from.
     * @param inputFile The name of the file to load.
     * @param outputFile The name of the file to save.
     * @return The {@link YamlDocument} of the saved file.
     */
    @SuppressWarnings("SameParameterValue")
    protected final YamlDocument saveConfig(Class<?> clazz, String inputFile, String outputFile) {
        InputStream stream = clazz.getResourceAsStream("/" + inputFile);
        if(stream == null) {
            getLogger().error("Attempted to load missing file {} from resources for module {}", inputFile, getData().name);
            return null;
        }
        if(
                !getModulePublicPath().exists() ||
                        !getModulePublicPath().isDirectory()
        ) {
            try {
                Files.createDirectory(modulePublicPath.toPath());
            } catch(IOException ex) {
                getLogger().error("An error occurred while creating the module public path: {}", ex.getMessage());
            }
        }

        File toWrite = new File(modulePublicPath, outputFile);
        return YMLUtil.createConfig(toWrite, stream);
    }

    /**
     * Saves a configuration file to the module's public path.
     * @param clazz The class to load the file from.
     * @param inputFile The name of the file to load.
     * @param outputFile The name of the file to save.
     * @return The {@link YamlDocument} of the saved file.
     */
    @SuppressWarnings("SameParameterValue")
    protected final Toml saveTomlConfig(Class<?> clazz, String inputFile, String outputFile) {
        InputStream stream = clazz.getResourceAsStream("/" + inputFile);
        if(stream == null) {
            getLogger().error("Attempted to load missing file {} from resources for module {}", inputFile, getData().name);
            return null;
        }
        if(
                !getModulePublicPath().exists() ||
                        !getModulePublicPath().isDirectory()
        ) {
            try {
                Files.createDirectory(modulePublicPath.toPath());
            } catch(IOException ex) {
                getLogger().error("An error occurred while creating the module public path: {}", ex.getMessage());
            }
        }

        File toWrite = new File(modulePublicPath, outputFile);
        return TOMLUtil.create(toWrite, stream);
    }

    /**
     * Registers a command to the module.
     * @param name The name of the command.
     * @param desc The description of the command.
     * @param executor The command executor.
     */
    @SuppressWarnings("UnstableApiUsage")
    protected final void registerCommand(String name, String desc, BasicCommand executor) {
        ModuleCommand cmd = new ModuleCommand(name, desc, executor);
        Ctrlctr.getToRegister().add(cmd);
        this.commands.add(cmd);
    }

    /**
     * Logs a message to the module's logger.
     * @return An {@link Logger} for the module.
     */
    protected static Logger getLogger() {
        String name = Thread.currentThread().getStackTrace()[2].getClassName();
        return LoggerFactory.getLogger(name);
    }

}
