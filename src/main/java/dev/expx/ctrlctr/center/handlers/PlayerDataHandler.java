package dev.expx.ctrlctr.center.handlers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.storage.schemas.PlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Handles player data loading and saving.
 */
@ApiStatus.Internal
public class PlayerDataHandler implements Listener {

    /**
     * Create a new player data handler.
     */
    public PlayerDataHandler() {}

    final Lang lang = Ctrlctr.getLang();
    final Logger l = LoggerFactory.getLogger(PlayerDataHandler.class);

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
            l.info(lang.lang("pd-loading", e.getPlayer().getName()));
            Player p = e.getPlayer();
            if(playerData.containsKey(p)) {
                l.warn(lang.lang("pd-incache", e.getPlayer().getName()));
                playerData.remove(p);
            }
            PlayerData pd = pdc.find(Filters.eq("playerUuid", p.getUniqueId())).first();
            if(pd == null) {
                l.info(lang.lang("pd-creating", e.getPlayer().getName()));
                pd = new PlayerData(p.getUniqueId());
                pdc.insertOne(pd);
            }
            if(pd.isPulled()) {
                l.warn(lang.lang("pd-loaded-elsewhere", e.getPlayer().getName()));
                Bukkit.getScheduler().runTask(Ctrlctr.getInstance(),
                        () ->
                                p.kick(lang.langComponent("pd-kick"),
                                        PlayerKickEvent.Cause.DUPLICATE_LOGIN)
                );
                return;
            }
            l.info(lang.lang("pd-loaded", e.getPlayer().getName()));
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
            l.info(lang.lang("pd-save", e.getPlayer().getName()));
            Player p = e.getPlayer();
            l.info(lang.lang("pd-replace", e.getPlayer().getName()));
            playerData.get(p).setPulled(false);
            pdc.replaceOne(Filters.eq("playerUuid", p.getUniqueId()), playerData.get(p));
            l.info(lang.lang("pd-remove", e.getPlayer().getName()));
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
            l.info(lang.lang("pd-save", e.getPlayer().getName()));
            Player p = e.getPlayer();
            l.info(lang.lang("pd-replace", e.getPlayer().getName()));
            playerData.get(p).setPulled(false);
            pdc.replaceOne(Filters.eq("playerUuid", p.getUniqueId()), playerData.get(p));
            l.info(lang.lang("pd-remove", e.getPlayer().getName()));
            playerData.remove(p);
        }, "PD Saver").start();
    }

}
