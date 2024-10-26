package dev.expx.ctrlctr.center.papi;

import dev.expx.ctrlctr.center.Ctrlctr;
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
        LoggerFactory.getLogger(PAPIAPI.class).info(Ctrlctr.getLang().lang("placeholder-register", id));
        PAPIExpansion.handlers.register(id, handler);
    }

}
