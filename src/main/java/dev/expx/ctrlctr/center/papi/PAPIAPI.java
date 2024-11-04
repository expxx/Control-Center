package dev.expx.ctrlctr.center.papi;

import dev.expx.ctrlctr.center.Ctrlctr;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.slf4j.LoggerFactory;

/**
 * PlaceholderAPI API handler
 */
@SuppressWarnings("unused")
public class PAPIAPI {

    /**
     * Utility class, do not instantiate.
     */
    private PAPIAPI() {}

    /**
     * Register a placeholder
     * @param id Placeholder ID
     * @param handler Placeholder handler
     */
    @SuppressWarnings("unused")
    public static void register(String id, PAPIExpansion.PlaceholderHandler<Player, String> handler) {
        PAPIExpansion.handlers.register(id, handler);
    }

    /**
     * Resolves a placeholder from a
     * player using PlaceholderAPI
     * @param player Player
     * @param placeholder Placeholder
     * @return Resolved placeholder
     */
    public static String resolve(Player player, String placeholder) {
        return PlaceholderAPI.setPlaceholders(player, placeholder);
    }

}
