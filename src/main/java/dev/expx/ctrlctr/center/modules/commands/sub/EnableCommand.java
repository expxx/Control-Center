package dev.expx.ctrlctr.center.modules.commands.sub;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.logger.Log;
import dev.expx.ctrlctr.center.modules.Module;
import dev.expx.ctrlctr.center.util.TextUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

import java.util.logging.Level;

/**
 * Enable Command
 */
@SuppressWarnings("UnstableApiUsage") @ApiStatus.Internal
public class EnableCommand {

    /**
     * Constructor
     * @param stack CommandSourceStack
     * @param args Arguments
     */
    public EnableCommand(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if(!sender.hasPermission("controlcenter.module.enable")) {
            sender.sendMessage(TextUtil.translate("&cYou do not have permission to use this command."));
            return;
        }
        if(args.length != 2) {
            sender.sendMessage(TextUtil.translate("&cUsage: /module enable <module>"));
            return;
        }
        String modName = args[1];
        if(!Ctrlctr.getModules().containsKey(modName)) {
            sender.sendMessage(TextUtil.translate("&cThat module is not loaded or does not exist."));
            return;
        }

        Module module = Ctrlctr.getModules().get(modName);
        if(module.isActive())
            sender.sendMessage(TextUtil.translate("&cThis module is already enabled."));
        else {
            sender.sendMessage(TextUtil.translate("&eLoading the module..."));
            Log.log(Level.WARNING, "Loading module {0}", module.getData().name);
            module.create();
            module.setActive(true);
            sender.sendMessage(TextUtil.translate("&aModule loaded successfully."));
        }
    }

}
