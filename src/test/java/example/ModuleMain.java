package example;

import com.google.auto.service.AutoService;
import dev.expx.ctrlctr.center.modules.Module;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@AutoService(Module.class)
public class ModuleMain extends Module {

    @Getter
    private static ModuleMain instance;

    @Override
    public void create() {
        instance = this;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void reload(CommandSender executor) {

    }
}
