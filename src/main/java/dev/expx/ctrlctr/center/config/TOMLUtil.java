package dev.expx.ctrlctr.center.config;

import com.moandjiezana.toml.Toml;
import dev.expx.ctrlctr.center.logger.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

/**
 * Utility class for TOML files
 */
public class TOMLUtil {

    /**
     * Utility class, do not instantiate
     */
    private TOMLUtil() {}

    /**
     * Parse TOML from a File
     * @param file File to parse
     * @return {@link Toml} object
     */
    public static Toml parse(File file) {
        return new Toml().read(file);
    }

    /**
     * Parse TOML from an InputStream
     * @param is InputStream to parse
     * @return {@link Toml} object
     */
    @SuppressWarnings("unused")
    public static Toml parse(InputStream is) {
        return new Toml().read(is);
    }

    /**
     * Create a TOML file
     * @param file File to create
     * @param is InputStream to write to the file
     * @return {@link Toml} object
     */
    public static Toml create(File file, InputStream is) {
        if(!file.exists())
            try {
                Files.copy(is, file.toPath());
            } catch(IOException ex) {
                Log.log(Level.SEVERE, "An error occurred while creating the file: {0}", ex.getMessage());
            }
        return parse(file);
    }

}
