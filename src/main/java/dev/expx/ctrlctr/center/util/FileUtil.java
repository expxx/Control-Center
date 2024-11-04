package dev.expx.ctrlctr.center.util;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.lang.LangLoader;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * File utility class
 */
@SuppressWarnings("unused")
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Will search the entire directory for files
     * and return them in a List
     *
     * @param dir Directory to list files in
     * @return List of all files in the directory
     */
    public static List<File> listInDir(File dir) {
        try { mkdir(dir); } catch(IOException ex) { logger.error(Ctrlctr.getLang().lang("error-file-create-dir", ex.getMessage())); }

        if(dir.listFiles() == null) return Collections.emptyList();
        if(Objects.requireNonNull(dir.listFiles()).length == 0) return Collections.emptyList();

        List<File> list = Arrays.stream(Objects.requireNonNull(dir.listFiles())).toList();
        if(list.isEmpty())
            return Collections.emptyList();
        return list;
    }

    /**
     * Will search the entire directory for files
     * and return them in a List
     *
     * @param file Directory to list files in
     * @param extension Filter for specific extensions
     * @return List of all files in the directory
     */
    @SuppressWarnings("unused")
    public static List<File> listInDir(File file, String extension) {
        try { mkdir(file); } catch(IOException ex) { logger.error(ex.getMessage()); }

        if(file.listFiles() == null) return Collections.emptyList();
        return Arrays.stream(
                Objects.requireNonNull(file.listFiles()))
                .toList()
                .stream()
                .filter(item -> FilenameUtils.getExtension(item.getName())
                        .equals(extension))
                .toList();
    }

    /**
     * Simply creates a directory if it doesn't
     * exist already.
     *
     * @param path Path to create
     * @throws RuntimeException Throws if failure to make directory
     */
    private static void mkdir(File path) throws IOException {
        if(path.exists()) return;
        Files.createDirectory(path.toPath());
    }

    /**
     * Attempts to save a file to the specified location,
     * however, if the file already exists, it will not
     * overwrite it.
     *
     * @param stream InputStream to save
     *               to the file
     * @param output File to save the InputStream to
     */
    public static void trySave(InputStream stream, File output) {
        if(output.exists()) return;
        try {
            Files.copy(stream, output.toPath());
        } catch(IOException | NullPointerException ignored) {
        }
    }

}
