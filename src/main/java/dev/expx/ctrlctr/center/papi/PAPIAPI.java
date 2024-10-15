package dev.expx.ctrlctr.center.papi;

import dev.expx.ctrlctr.center.logger.Log;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * PlaceholderAPI API handler
 */
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
    public static void register(String id, PAPIExpansion.PlaceholderHandler<Player, String> handler) {
        Log.log(Level.INFO, "Saw Placeholder register request {0}: Live Registered", id);
        PAPIExpansion.handlers.register(id, handler);
    }

}
