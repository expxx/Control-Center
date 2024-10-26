package dev.expx.ctrlctr.center.update;

import dev.expx.ctrlctr.center.datastore.Registry;
import dev.expx.ctrlctr.center.datastore.implementations.EclipseStore;
import dev.expx.ctrlctr.center.modules.Module;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Update handler
 */
@SuppressWarnings("unused")
public class UpdateHandler {

    /**
     * Utility class, do not instantiate
     */
    private UpdateHandler() {}

    private static final Registry<Module, Update> updateRegistry = new EclipseStore<>();

    /**
     * Register an update from a module
     * @param module Module
     * @param update Update handler
     */
    public static void registerUpdate(Module module, Update update) {
        updateRegistry.register(module, update);
    }

    /**
     * Check for updates
     * @return Map of modules and their new versions
     */
    public static Map<Module, Version> checkForUpdates() {
        HashMap<Module, Version> updates = new HashMap<>();

        for (Module module : updateRegistry.getRegistry().keySet()) {
            Optional<Update> update = updateRegistry.get(module);
            if (update.isPresent()) {
                Update update1 = update.get();
                Version newVersion = update1.handle();
                if (newVersion != null) {
                    updates.put(module, newVersion);
                }
            }
        }

        return updates;
    }

    /**
     * Called when asked for an update
     * check by a player or console
     */
    @FunctionalInterface
    public interface Update {

        /**
         * Handle the update
         * @return New version
         */
        Version handle();
    }

}
