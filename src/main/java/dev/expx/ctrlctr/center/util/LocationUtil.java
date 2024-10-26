package dev.expx.ctrlctr.center.util;

import org.bukkit.Location;

/**
 * Location utilities
 */
@SuppressWarnings("unused")
public class LocationUtil {

    /**
     * Utility Class, do not instantiate.
     */
    private LocationUtil() {}

    /**
     * Encodes a location to a string
     * @param l Location to encode
     * @return Encoded location
     */
    public static String encodeLocation(Location l) {
        return l.getWorld().getName() + "_" + l.getX() + "_" + l.getY() + "_" + l.getZ();
    }

    /**
     * Decodes a location from a string
     * @param s Encoded location
     * @return Decoded location
     */
    public static Location decodeLocation(String s) {
        String[] parts = s.split("_");
        return new Location(org.bukkit.Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }

}
