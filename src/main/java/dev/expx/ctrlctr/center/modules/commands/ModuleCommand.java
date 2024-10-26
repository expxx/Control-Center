package dev.expx.ctrlctr.center.modules.commands;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.modules.Module;
import dev.expx.ctrlctr.center.modules.commands.sub.*;
import dev.expx.ctrlctr.center.util.TextUtil;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Module command.
 */
@SuppressWarnings("UnstableApiUsage") @ApiStatus.Internal
public class ModuleCommand implements BasicCommand {

    private final Lang lang = Ctrlctr.getLang();

    /**
     * Utility class, do not instantiate.
     */
    public ModuleCommand() {}

    /**
     * Executes the module command.
     * @param stack Command source stack
     * @param args Arguments
     */
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        CommandSender sender = stack.getSender();
        if(args.length == 0) {
            List<Module> modules = Ctrlctr.getModules().values().stream().toList();
            TextComponent list = TextUtil.translate("&7Modules: &f");
            for(Module module : modules) {
                if(module.isActive())
                    list = list.append(
                            TextUtil.translate("&a" + module.getData().name() + " &f, ")
                                    .clickEvent(ClickEvent.runCommand("/module info " + module.getData().name()))
                    );
                else
                    list = list.append(
                            TextUtil.translate("&c" + module.getData().name() + "&f, ")
                                    .clickEvent(ClickEvent.runCommand("/module info " + module.getData().name()))
                    );
            }
            stack.getSender().sendMessage(list);
        } else if(args.length != 2) {
            sender.sendMessage(lang.langComponent("command-main-usage"));
        } else if(args[0].equals("info")) {
            new InfoCommand(stack, args);
        } else if(args[0].equals("reload")) {
            new ReloadCommand(stack, args);
        } else if(args[0].equals("update")) {
            new UpdatesCommand(stack, args);
        }
    }


    @Override @SuppressWarnings("UnstableApiUsage")
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        List<String> actionList = List.of("info", "reload", "update");
        Collection<Module> moduleList = Ctrlctr.getModules().values();
        if(args.length == 0)
            return actionList;
        else if(args.length == 1)
            return moduleList.stream().map(module -> module.getData().id()).toList();
        return List.of();
    }
}
