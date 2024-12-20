package dev.expx.ctrlctr.center.update;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.logger.errors.ModuleLoadException;
import dev.expx.ctrlctr.center.logger.errors.ModuleUpdateException;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class UpdateDownloader {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UpdateDownloader.class);
    private static final Lang lang = Ctrlctr.getLang();

    public static void download(String url, Path target, String expectedSha1, String expectedMd5) {
        try {
            URL uri = new URI(url).toURL();
            try (InputStream in = uri.openStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                if (!verifyChecksums(target, expectedSha1, expectedMd5)) {
                    logger.error("Checksum verification failed for " + target.getFileName());
                    Files.delete(target);
                }
            } catch (IOException ignored) {}
        } catch (Exception e) {
            throw new ModuleUpdateException(e.getMessage());
        }
    }

    public static String calculateChecksum(Path filePath, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            try (InputStream fis = Files.newInputStream(filePath);
                 DigestInputStream dis = new DigestInputStream(fis, digest)) {
                byte[] buffer = new byte[4096];
                //noinspection StatementWithEmptyBody
                while (dis.read(buffer) != -1) {
                    // Continue reading and updating the digest
                }
            }
            // Convert the digest bytes to a readable hex format
            StringBuilder checksum = new StringBuilder();
            for (byte b : digest.digest()) {
                checksum.append(String.format("%02x", b));
            }
            return checksum.toString();
        } catch (Exception e) {
            throw new ModuleLoadException(e.getMessage());
        }
    }

    public static boolean verifyChecksums(Path filePath, String expectedSha1, String expectedMd5) {
        String sha1 = calculateChecksum(filePath, "SHA-1");
        String md5 = calculateChecksum(filePath, "MD5");

        return sha1.equalsIgnoreCase(expectedSha1) && md5.equalsIgnoreCase(expectedMd5);
    }

}
