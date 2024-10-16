package dev.expx.ctrlctr.center.update;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a version.
 * @param hasUpdate Whether or not there is an update
 * @param currentVersion The current version
 * @param directDownloadUrl The direct download URL
 * @param latestVersion The latest version
 * @param downloadURL The download URL
 */
public record Version(
        boolean hasUpdate,
        @NotNull String currentVersion,
        @NotNull String directDownloadUrl,
        @Nullable String latestVersion,
        @Nullable String downloadURL,
        @Nullable String expectedSha1,
        @Nullable String expectedMd5
) {
}
