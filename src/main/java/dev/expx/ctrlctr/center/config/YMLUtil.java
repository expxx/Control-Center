package dev.expx.ctrlctr.center.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.expx.ctrlctr.center.logger.errors.ConfigLoadException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for YAML files
 */
public class YMLUtil {

    /**
     * Utility class, do not instantiate
     */
    private YMLUtil() {}

    /**
     * Create a YAML configuration file
     * @param file File to create
     * @param stream InputStream to write to the file
     * @return {@link YamlDocument} object
     */
    public static YamlDocument createConfig(@NotNull File file, @NotNull InputStream stream) {
        try {
            return YamlDocument.create(file, stream,
                    GeneralSettings.builder()
                            .setKeyFormat(GeneralSettings.KeyFormat.OBJECT)
                            .build(),
                    LoaderSettings.builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder()
                            .setAutoSave(true)
                            .setVersioning(new BasicVersioning("config-version"))
                            .build()
            );
        } catch(IOException ex) {
            throw new ConfigLoadException(ex.getMessage());
        }
    }

}
