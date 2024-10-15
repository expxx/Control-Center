package dev.expx.ctrlctr.center.modules.commands.sub;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.logger.Log;
import dev.expx.ctrlctr.center.modules.Module;
import dev.expx.ctrlctr.center.modules.ModuleCommand;
import dev.expx.ctrlctr.center.util.TextUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

import java.util.logging.Level;

/**
 * Disable Command
 */
@SuppressWarnings("UnstableApiUsage") @ApiStatus.Internal
public class DisableCommand {

    /**
     * Constructor
     * @param stack CommandSourceStack
     * @param args Arguments
     */
    @ApiStatus.Internal
    public DisableCommand(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if(!sender.hasPermission("controlcenter.module.disable")) {
            sender.sendMessage(TextUtil.translate("&cYou do not have permission to use this command."));
            return;
        }
        if(args.length != 2) {
            sender.sendMessage(TextUtil.translate("&cUsage: /module disable <module>"));
            return;
        }
        String modName = args[1];
        if(!Ctrlctr.getModules().containsKey(modName)) {
            sender.sendMessage(TextUtil.translate("&cThat module is not loaded or does not exist."));
            return;
        }
        Module module = Ctrlctr.getModules().get(modName);
        if(!module.isActive())
            sender.sendMessage(TextUtil.translate("&cThat module is already disabled"));
        else {
            sender.sendMessage(TextUtil.translate("&eUnloading the module..."));
            Log.log(Level.WARNING, "Unloading module: " + module.getData().name);
            module.destroy();
            module.setActive(false);
            for(ModuleCommand cmd : module.getCommands()) {
                Log.log(Level.INFO, "Unloading module command: " + cmd.getName());
            }
            sender.sendMessage(TextUtil.translate("&cModule unloaded successfully."));
        }
    }

}
