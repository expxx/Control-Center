package dev.expx.ctrlctr.center;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.expx.ctrlctr.center.communication.data.AuthSet;
import dev.expx.ctrlctr.center.communication.data.ConnSet;
import dev.expx.ctrlctr.center.communication.rabbit.Rabbit;
import dev.expx.ctrlctr.center.communication.redis.Redis;
import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.modules.Module;
import dev.expx.ctrlctr.center.modules.ModuleCommand;
import dev.expx.ctrlctr.center.storage.Mongo;
import dev.expx.ctrlctr.center.util.ServerIF;
import dev.expx.ctrlctr.center.util.ServerType;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter @Setter
public class Statics {

    public static ServerType serverType;
    public static ServerIF serverInterface;

    public static String dataDir;

    public static final HashMap<String, Module> modules = new HashMap<>();
    public static final List<ModuleCommand> toRegister = new ArrayList<>();

    public static YamlDocument storageConfig;
    public static YamlDocument mainConfig;

    public static boolean mongoConnected = false;
    public static boolean rabbitConnected = false;
    public static boolean redisConnected = false;

    public static ConnSet mongoConnSet;
    public static ConnSet rabbitConnSet;
    public static ConnSet redisConnSet;

    public static AuthSet mongoAuthSet;
    public static AuthSet rabbitAuthSet;
    public static AuthSet redisAuthSet;

    public static Rabbit globalRabbit;
    public static Redis globalRedis;
    public static Mongo mongo;

    public static Lang lang;
    public static Economy econ;

}
