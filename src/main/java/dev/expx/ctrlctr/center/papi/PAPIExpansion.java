package dev.expx.ctrlctr.center.papi;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.datastore.Registry;
import dev.expx.ctrlctr.center.datastore.implementations.EclipseStore;
import dev.expx.ctrlctr.center.logger.Log;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.logging.Level;

/**
 * PlaceholderAPI expansion for Ctrlctr. This
 * class is responsible for handling all
 * placeholders registered in the plugin.
 */
public class PAPIExpansion extends PlaceholderExpansion {

    /**
     * Creates a new PAPIExpansion.
     */
    public PAPIExpansion() {}

    /**
     * The registry of all placeholder handlers.
     */
    static final Registry<String, PlaceholderHandler<Player, String>> handlers = new EclipseStore<>();

    /**
     * The identifier of the expansion.
     * @return An {@link String} representing the identifier.
     */
    @NotNull
    @Override
    public String getIdentifier() {
        return "ctrlctr";
    }

    /**
     * The author of the expansion.
     * @return An {@link String} representing the author.
     */
    @NotNull
    @Override
    public String getAuthor() {
        return "cammyzed";
    }

    /**
     * The version of the expansion.
     * @return An {@link String} representing the version.
     */
    @NotNull
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public String getVersion() {
        return Ctrlctr.getInstance().getPluginMeta().getVersion();
    }

    /**
     * The placeholder request handler.
     * @param p The player requesting the placeholder.
     * @param id The identifier of the placeholder.
     * @return An {@link String} representing the placeholder value.
     */
    @Override
    public @Nullable String onPlaceholderRequest(Player p, @NotNull String id) {
        return receive(p, id);
    }

    /**
     * Receives a placeholder request and returns the value.
     * @param p The player requesting the placeholder.
     * @param id The identifier of the placeholder.
     * @return An {@link String} representing the placeholder value.
     */
    @ApiStatus.Internal
    public String receive(@NotNull final Player p, @NotNull final String id) {
        final Optional<PlaceholderHandler<Player, String>> handler = handlers.get(id);
        return handler.map(m -> m.handle(p, id)).orElse(id);
    }

    /**
     * Creates a new placeholder handler.
     * @param <P> The type of the player.
     * @param <S> The type of the placeholder.
     */
    @FunctionalInterface
    public interface PlaceholderHandler<P, S> {

        /**
         * Handles the placeholder request.
         * @param p The player requesting the placeholder.
         * @param id The identifier of the placeholder.
         * @return An {@link String} representing the placeholder value.
         */
        String handle(P p, S id);
    }
}
