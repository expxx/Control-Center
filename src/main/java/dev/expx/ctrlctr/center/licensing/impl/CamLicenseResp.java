package dev.expx.ctrlctr.center.licensing.impl;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a CAM license response.
 * @param valid Whether the license is valid
 * @param localKey Local key
 * @param discordId Discord ID
 * @param outdated Whether the license is outdated
 * @param latestVersion Latest version
 * @param safeToShow Safe to show
 */
@SuppressWarnings("unused")
@ApiStatus.Experimental
public record CamLicenseResp(
        boolean valid,
        @Nullable String localKey,
        @Nullable String discordId,
        boolean outdated,
        @Nullable String latestVersion,
        @Nullable String safeToShow
) {
}
