package dev.expx.ctrlctr.center.util;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
        try { mkdir(dir); } catch(IOException ex) { logger.error("An error occurred while creating the directory: {}", ex.getMessage()); }

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
        try { mkdir(file); } catch(IOException ex) { logger.error("An error occurred while creating the directory: {}", ex.getMessage()); }

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

}
