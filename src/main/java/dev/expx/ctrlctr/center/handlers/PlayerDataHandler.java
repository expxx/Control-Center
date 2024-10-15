package dev.expx.ctrlctr.center.handlers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.logger.Log;
import dev.expx.ctrlctr.center.storage.schemas.PlayerData;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * Handles player data loading and saving.
 */
@ApiStatus.Internal
public class PlayerDataHandler implements Listener {

    /**
     * Create a new player data handler.
     */
    public PlayerDataHandler() {}

    @Getter
    static final MongoCollection<PlayerData> pdc = Ctrlctr.getInstance().getMongo().getPlayerDataMongoCollection();
    @Getter
    static final HashMap<Player, PlayerData> playerData = new HashMap<>();

    /**
     * Get the player data for a player.
     * @param player The player to get the data for.
     * @return A {@link PlayerData} object.
     */
    @SuppressWarnings("unused")
    public static PlayerData getPlayerData(Player player) {
        return playerData.get(player);
    }

    /**
     * Load player data when a player joins.
     * @param e The {@link PlayerJoinEvent} event.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        new Thread(() -> {
            Log.log(Level.INFO, "Loading player data for " + e.getPlayer().getName());
            Player p = e.getPlayer();
            if(playerData.containsKey(p)) {
                Log.log(Level.INFO, "Player data already in cache for " + p.getName());
                playerData.remove(p);
            }
            PlayerData pd = pdc.find(Filters.eq("playerUuid", p.getUniqueId())).first();
            if(pd == null) {
                Log.log(Level.INFO, "Creating new player data for " + p.getName());
                pd = new PlayerData(p.getUniqueId());
                pdc.insertOne(pd);
            }
            if(pd.isPulled()) {
                Log.log(Level.INFO, "Player data active in another location for " + p.getName());
                Bukkit.getScheduler().runTask(Ctrlctr.getInstance(),
                        () ->
                                p.kick(Component.text(
                                        "Your player data is actively loaded in another location. If you believe this is an error, please contact an administrator.",
                                                NamedTextColor.DARK_RED
                                        ),
                                        PlayerKickEvent.Cause.DUPLICATE_LOGIN)
                );
                return;
            }
            Log.log(Level.INFO, "Player data loaded for " + p.getName());
            pdc.updateOne(Filters.eq("playerUuid", p.getUniqueId()), Updates.set("pulled", true));
            pd.setPulled(true);
            playerData.put(p, pd);
        }, "PD Loader").start();
    }

    /**
     * Save player data when a player quits.
     * @param e The {@link PlayerQuitEvent} event.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(e.getReason() == PlayerQuitEvent.QuitReason.KICKED) return;
        new Thread(() -> {
            Log.log(Level.INFO, "Saving player data for " + e.getPlayer().getName());
            Player p = e.getPlayer();
            Log.log(Level.INFO, "Replacing player data for " + p.getName());
            playerData.get(p).setPulled(false);
            pdc.replaceOne(Filters.eq("playerUuid", p.getUniqueId()), playerData.get(p));
            Log.log(Level.INFO, "Removing player data for " + p.getName());
            playerData.remove(p);
        }, "PD Saver").start();
    }

    /**
     * Save player data when a player is kicked.
     * @param e The {@link PlayerKickEvent} event.
     */
    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if(e.getCause() == PlayerKickEvent.Cause.DUPLICATE_LOGIN) return;
        new Thread(() -> {
            Log.log(Level.INFO, "Saving player data for " + e.getPlayer().getName());
            Player p = e.getPlayer();
            Log.log(Level.INFO, "Replacing player data for " + p.getName());
            playerData.get(p).setPulled(false);
            pdc.replaceOne(Filters.eq("playerUuid", p.getUniqueId()), playerData.get(p));
            Log.log(Level.INFO, "Removing player data for " + p.getName());
            playerData.remove(p);
        }, "PD Saver").start();
    }

}
