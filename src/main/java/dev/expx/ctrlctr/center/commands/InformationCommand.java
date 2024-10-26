package dev.expx.ctrlctr.center.commands;

import dev.expx.ctrlctr.center.Ctrlctr;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Information command.
 */
@SuppressWarnings({"UnstableApiUsage", "SpellCheckingInspection"})
@ApiStatus.Internal
public class InformationCommand implements BasicCommand {

    /**
     * Information command constructor.
     */
    public InformationCommand() {}

    /**
     * Executes the information command.
     * @param commandSourceStack Command source stack
     * @param strings Arguments
     */
    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        CommandSender sender = commandSourceStack.getSender();

        sender.sendMessage("§7§m---------------------------------");
        sender.sendMessage("§6§lCtrlCtr §7- §fInformation");
        sender.sendMessage("§7§m---------------------------------");
        sender.sendMessage("§7Version: §f" + Ctrlctr.getInstance().getPluginMeta().getVersion());
        sender.sendMessage("§7Author: §fcammyzed");
        sender.sendMessage("§7Website: §fhttps://expx.dev");
        sender.sendMessage("§7§m---------------------------------");
        sender.sendMessage("§7Rabbit Connected: §f" + (Ctrlctr.isRabbitConnected() ? "§aYes" : "§cNo"));
        sender.sendMessage("§7Redis Connected: §f" + (Ctrlctr.isRedisConnected() ? "§aYes" : "§cNo"));
        sender.sendMessage("§7Mongo Connected: §f" + (Ctrlctr.isMongoConnected() ? "§aYes" : "§cNo"));

    }
}
