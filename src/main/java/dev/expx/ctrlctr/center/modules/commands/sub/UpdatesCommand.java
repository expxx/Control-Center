package dev.expx.ctrlctr.center.modules.commands.sub;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.modules.Module;
import dev.expx.ctrlctr.center.update.UpdateHandler;
import dev.expx.ctrlctr.center.update.Version;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Updates a module.
 */
@ApiStatus.Internal @SuppressWarnings("UnstableApiUsage")
public class UpdatesCommand {

    private final Lang lang = Ctrlctr.getLang();

    public UpdatesCommand(CommandSourceStack stack, String[] args) {
        CommandSender s = stack.getSender();
        if(!s.hasPermission("controlcenter.module.update")) {
            s.sendMessage(lang.langComponent("command-noperm"));
            return;
        }
        new Thread(() -> {
            s.sendMessage(lang.langComponent("command-update-checking"));
            Map<Module, Version> updates = UpdateHandler.checkForUpdates();
            if (updates.isEmpty()) {
                s.sendMessage(lang.langComponent("command-update-none"));
                return;
            }
            s.sendMessage(lang.langComponent("command-update-available"));
            for (Map.Entry<Module, Version> entry : updates.entrySet()) {
                s.sendMessage(lang.langComponent("command-update-name", entry.getKey().getData().name()));
                s.sendMessage(lang.langComponent("command-update-current", entry.getKey().getData().version()));
                s.sendMessage(lang.langComponent("command-update-latest", entry.getValue()));
            }
            if (args.length == 2 && args[1].equals("--autoupdate")) {
                s.sendMessage(lang.langComponent("command-update-auto"));
                Ctrlctr.getInstance().loadUpdates(updates);
                s.sendMessage(lang.langComponent("command-update-reboot"));
            } else {
                s.sendMessage(lang.langComponent("command-update-useautoupdate"));
            }
        }).start();
    }

}
