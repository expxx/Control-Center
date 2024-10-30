package dev.expx.ctrlctr.center.modules.commands.sub;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.Statics;
import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.modules.Module;
import dev.expx.ctrlctr.center.modules.ModuleInfo;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

/**
 * Information command.
 */
@SuppressWarnings({"UnstableApiUsage", "SpellCheckingInspection"})
@ApiStatus.Internal
public class InfoCommand {

    /**
     * Creates a new information command.
     * @param stack Command source stack
     * @param args Arguments
     */
    public InfoCommand(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        Lang lang = Statics.lang;
        if(!sender.hasPermission("controlcenter.module.info")) {
            sender.sendMessage(lang.langComponent("command-noperm"));
            return;
        }
        if(args.length != 2) {
            sender.sendMessage(lang.langComponent("command-info-usage"));
            return;
        }
        String modName = args[1];
        if(!Statics.modules.containsKey(modName)) {
            sender.sendMessage(lang.langComponent("command-module-not-found"));
            return;
        }

        Module module = Statics.modules.get(modName);
        ModuleInfo info = module.getData();
        sender.sendMessage(lang.langComponent("command-info-name", info.name()));
        sender.sendMessage(lang.langComponent("command-info-active", (module.isActive() ? "&aYES" : "&cNO")));
        sender.sendMessage(lang.langComponent("command-info-version", info.version()));

    }

}
