package dev.expx.ctrlctr.center.commands;

import com.mongodb.client.model.Filters;
import dev.expx.ctrlctr.center.handlers.PlayerDataHandler;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

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
                    LoggerFactory.getLogger(ReloadPlayerData.class).info("Reloading data for {}", p.getName());
                    PlayerDataHandler.getPlayerData().remove(p);
                    PlayerDataHandler.getPlayerData().put(p, PlayerDataHandler.getPdc().find(Filters.eq("playerUuid", p.getUniqueId())).first());
                    LoggerFactory.getLogger(ReloadPlayerData.class).info("Reloaded data for {}", p.getName());
                }
            }, "Player Reloader").start();
            sender.sendMessage("Player data reloaded.");
        }
    }
}
