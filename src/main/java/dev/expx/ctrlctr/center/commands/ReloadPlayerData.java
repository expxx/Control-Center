package dev.expx.ctrlctr.center.commands;

import com.mongodb.client.model.Filters;
import dev.expx.ctrlctr.center.handlers.PlayerDataHandler;
import dev.expx.ctrlctr.center.logger.Log;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Reloads player data.
 */
@SuppressWarnings("UnstableApiUsage") @ApiStatus.Internal
public class ReloadPlayerData implements BasicCommand {

    /**
     * Creates a new reload player data command.
     */
    public ReloadPlayerData() {}

    /**
     * Executes the command.
     * @param commandSourceStack Command source stack
     * @param strings Arguments
     */
    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        CommandSender sender = commandSourceStack.getSender();
        if(sender.hasPermission("ctrlctr.reloaddata")) {
            sender.sendMessage("Reloading player data... (MULTI-THREADED)");
            new Thread(() -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    Log.log(Level.WARNING, "Reloading player data for " + p.getName());
                    PlayerDataHandler.getPlayerData().remove(p);
                    PlayerDataHandler.getPlayerData().put(p, PlayerDataHandler.getPdc().find(Filters.eq("playerUuid", p.getUniqueId())).first());
                    Log.log(Level.WARNING, "Player data reloaded for " + p.getName());
                }
            }, "Player Reloader").start();
            sender.sendMessage("Player data reloaded.");
        }
    }
}
