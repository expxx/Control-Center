package dev.expx.ctrlctr.center.modules;

import io.papermc.paper.command.brigadier.BasicCommand;
import lombok.Getter;

/**
 * Represents a command that is part of a module.
 */
@Getter
public class ModuleCommand {

    /**
     * The name of the command.
     */
    public final String name;

    /**
     * The description of the command.
     */
    public final String desc;

    /**
     * The command itself.
     */
    @SuppressWarnings("UnstableApiUsage")
    public final BasicCommand command;

    /**
     * Creates a new module command.
     * @param name The name of the command.
     * @param desc The description of the command.
     * @param command The command itself.
     */
    @SuppressWarnings("UnstableApiUsage")
    public ModuleCommand(
            String name,
            String desc,
            BasicCommand command
    ) {
        this.name = name;
        this.desc = desc;
        this.command = command;
    }

}
