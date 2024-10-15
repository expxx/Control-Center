package dev.expx.ctrlctr.center.modules.commands.sub;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.modules.Module;
import dev.expx.ctrlctr.center.modules.ModuleInfo;
import dev.expx.ctrlctr.center.util.TextUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

/**
 * Information command.
 */
@SuppressWarnings("UnstableApiUsage") @ApiStatus.Internal
public class InfoCommand {

    /**
     * Creates a new information command.
     * @param stack Command source stack
     * @param args Arguments
     */
    public InfoCommand(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if(!sender.hasPermission("controlcenter.module.info")) {
            sender.sendMessage(TextUtil.translate("&cYou do not have permission to use this command."));
            return;
        }
        if(args.length != 2) {
            sender.sendMessage(TextUtil.translate("&cUsage: /module info <module>"));
            return;
        }
        String modName = args[1];
        if(!Ctrlctr.getModules().containsKey(modName)) {
            sender.sendMessage(TextUtil.translate("&cThat module is not loaded or does not exist."));
            return;
        }

        Module module = Ctrlctr.getModules().get(modName);
        ModuleInfo info = module.getData();
        sender.sendMessage(TextUtil.translate("&7Module &b" + info.name));
        sender.sendMessage(TextUtil.translate("&7Active " + (module.isActive() ? "&aYES" : "&cNO")));
        sender.sendMessage(TextUtil.translate("&7Version " + info.version));

    }

}
