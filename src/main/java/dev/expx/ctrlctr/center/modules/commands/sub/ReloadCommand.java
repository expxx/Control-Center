package dev.expx.ctrlctr.center.modules.commands.sub;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.modules.Module;
import dev.expx.ctrlctr.center.util.TextUtil;
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
        if(!sender.hasPermission("controlcenter.module.reload")) {
            sender.sendMessage(TextUtil.translate("&cYou do not have permission to use this command."));
            return;
        }
        if(args.length != 2) {
            sender.sendMessage(TextUtil.translate("&cUsage: /module reload <module>"));
            return;
        }
        String modName = args[1];
        if(!Ctrlctr.getModules().containsKey(modName)) {
            sender.sendMessage(TextUtil.translate("&cThat module is not loaded or does not exist."));
            return;
        }

        Module module = Ctrlctr.getModules().get(modName);
        sender.sendMessage(TextUtil.translate("&eReloading the module: " + module.getData().name));
        module.reload(sender);
        sender.sendMessage(TextUtil.translate("&aModule reloaded successfully."));
    }

}
