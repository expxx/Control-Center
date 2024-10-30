package dev.expx.ctrlctr.center.modules.commands.sub;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.Statics;
import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.modules.Module;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

/**
 * Reloads a module.
 */
@SuppressWarnings("UnstableApiUsage") @ApiStatus.Internal
public class ReloadCommand {

    /**
     * Creates a new reload command.
     * @param stack Command source stack
     * @param args Arguments
     */
    public ReloadCommand(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        Lang lang = Statics.lang;
        if(!sender.hasPermission("controlcenter.module.reload")) {
            sender.sendMessage(lang.langComponent("command-noperm"));
            return;
        }
        if(args.length != 2) {
            sender.sendMessage(lang.langComponent("command-reload-usage"));
            return;
        }
        String modName = args[1];
        if(!Statics.modules.containsKey(modName)) {
            sender.sendMessage(lang.langComponent("command-module-not-found"));
            return;
        }

        Module module = Statics.modules.get(modName);
        module.reload(sender);
        sender.sendMessage(lang.langComponent("command-reload-reloaded"));
    }

}
