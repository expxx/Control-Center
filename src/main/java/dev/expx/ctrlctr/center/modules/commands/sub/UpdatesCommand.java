package dev.expx.ctrlctr.center.modules.commands.sub;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.modules.Module;
import dev.expx.ctrlctr.center.update.UpdateDownloader;
import dev.expx.ctrlctr.center.update.UpdateHandler;
import dev.expx.ctrlctr.center.update.Version;
import dev.expx.ctrlctr.center.util.TextUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.util.Map;

/**
 * Updates a module.
 */
@ApiStatus.Internal
public class UpdatesCommand {

    public UpdatesCommand(CommandSourceStack stack, String[] args) {
        CommandSender s = stack.getSender();
        if(!s.hasPermission("controlcenter.module.update")) {
            s.sendMessage(TextUtil.translate("&cYou do not have permission to use this command."));
            return;
        }
        new Thread(() -> {
            s.sendMessage(TextUtil.translate("&eChecking for updates..."));
            Map<Module, Version> updates = UpdateHandler.checkForUpdates();
            if (updates.isEmpty()) {
                s.sendMessage(TextUtil.translate("&aNo updates available."));
                return;
            }
            s.sendMessage(TextUtil.translate("&eUpdates available:"));
            for (Map.Entry<Module, Version> entry : updates.entrySet()) {
                s.sendMessage(TextUtil.translate("&e- " + entry.getKey().getData().name));
                s.sendMessage(TextUtil.translate("  &eCurrent: &7" + entry.getKey().getData().version));
                s.sendMessage(TextUtil.translate("  &eLatest: &7" + entry.getValue().latestVersion()));
            }
            if (args.length == 2 && args[1].equals("--autoupdate")) {
                s.sendMessage(TextUtil.translate("&eDownloading updates..."));
                Ctrlctr.getInstance().loadUpdates(updates);
                s.sendMessage(TextUtil.translate("&ePlease restart the server to apply the updates."));
            } else {
                s.sendMessage(TextUtil.translate("&eUse &7/module update --autoupdate &eto download and apply updates."));
            }
        }).start();
    }

}
