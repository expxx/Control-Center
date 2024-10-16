package dev.expx.ctrlctr.center;

import com.google.gson.JsonObject;
import com.mongodb.client.model.Filters;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.expx.ctrlctr.center.commands.InformationCommand;
import dev.expx.ctrlctr.center.commands.ReloadPlayerData;
import dev.expx.ctrlctr.center.communication.rabbit.Rabbit;
import dev.expx.ctrlctr.center.communication.rabbit.data.AuthSet;
import dev.expx.ctrlctr.center.communication.rabbit.data.ConnSet;
import dev.expx.ctrlctr.center.communication.redis.Redis;
import dev.expx.ctrlctr.center.config.YMLUtil;
import dev.expx.ctrlctr.center.handlers.PlayerDataHandler;
import dev.expx.ctrlctr.center.http.XMLRequest;
import dev.expx.ctrlctr.center.modules.Module;
import dev.expx.ctrlctr.center.modules.ModuleCommand;
import dev.expx.ctrlctr.center.modules.ModuleManager;
import dev.expx.ctrlctr.center.papi.PAPIExpansion;
import dev.expx.ctrlctr.center.storage.Mongo;
import dev.expx.ctrlctr.center.storage.schemas.PlayerData;
import dev.expx.ctrlctr.center.update.UpdateDownloader;
import dev.expx.ctrlctr.center.update.UpdateHandler;
import dev.expx.ctrlctr.center.update.Version;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Main class for the Control Center plugin.
 */
@SuppressWarnings("UnstableApiUsage")
public final class Ctrlctr extends JavaPlugin {

    @Getter
    private static final HashMap<String, Module> modules = new HashMap<>();

    @Getter
    private static Ctrlctr instance;
    @Getter
    private static final List<ModuleCommand> toRegister = new ArrayList<>();

    @Getter
    private YamlDocument storageConfig;
    @Getter
    private YamlDocument mainConfig;

    @Getter @Setter
    private static boolean mongoConnected = false;
    @Getter @Setter
    private static boolean rabbitConnected = false;
    @Getter @Setter
    private static boolean redisConnected = false;

    @Getter
    private ConnSet mongoConnSet;
    @Getter
    private ConnSet rabbitConnSet;
    @Getter
    private ConnSet redisConnSet;
    @Getter
    private AuthSet mongoAuthSet;
    @Getter
    private AuthSet rabbitAuthSet;
    @Getter
    private AuthSet redisAuthSet;

    @Getter
    private Rabbit globalRabbit;
    @Getter
    private Redis globalRedis;
    @Getter
    private Mongo mongo;

    @Getter
    private Economy econ;


    /**
     * Called when the plugin is enabled.
     */
    @Override @ApiStatus.Internal
    public void onEnable() {
        instance = this;

        if (!setupEco()) {
            getLogger().severe("Failed to setup economy!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        storageConfig = YMLUtil.createConfig(
                new File(this.getDataFolder(), "storage.yml"),
                Objects.requireNonNull(getResource("storage.yml"))
        );
        mainConfig = YMLUtil.createConfig(
                new File(this.getDataFolder(), "config.yml"),
                Objects.requireNonNull(getResource("config.yml"))
        );

        mongoConnSet = new ConnSet(
                storageConfig.getString("mongo.ip"),
                storageConfig.getInt("mongo.port")
        );
        mongoAuthSet = new AuthSet(
                storageConfig.getString("mongo.user"),
                storageConfig.getString("mongo.pass")
        );
        mongo = new Mongo().connectMongo(mongoConnSet, mongoAuthSet);
        getLogger().info("");


        redisAuthSet = new AuthSet(
                null,
                storageConfig.getString("redis.password")
        );
        redisConnSet = new ConnSet(
                storageConfig.getString("redis.ip"),
                storageConfig.getInt("redis.port")
        );
        globalRedis = new Redis(
                redisConnSet.getIp(),
                redisConnSet.getPort(),
                redisAuthSet.getPass(),
                "global"
        );


        rabbitConnSet = new ConnSet(
                storageConfig.getString("rabbit.ip"),
                storageConfig.getInt("rabbit.port")
        );
        rabbitAuthSet = new AuthSet(
                storageConfig.getString("rabbit.user"),
                storageConfig.getString("rabbit.pass")
        );
        globalRabbit = new Rabbit("global", rabbitConnSet, rabbitAuthSet);


        try {
            new PAPIExpansion().register();
            ModuleManager.setup(getDataFolder());
            loadCommands();
        } finally {
            if (!toRegister.isEmpty()) {
                LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
                manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
                    final Commands commands = event.registrar();
                    for (ModuleCommand cmd : toRegister) {
                        if (cmd.getName() != null && cmd.getDesc() != null && cmd.getCommand() != null) {
                            commands.register(cmd.getName(), cmd.getDesc(), cmd.getCommand());
                        }
                    }
                });
            }
        }

        getServer().getPluginManager().registerEvents(new PlayerDataHandler(), this);

        try {
            JsonObject obj = XMLRequest.request(
                    "https://repo.expx.dev/repository/public-releases/dev/expx/ctrlctr/ctrlctr/maven-metadata.xml",
                    "GET",
                    new HashMap<>(),
                    false,
                    null
            );
            String currentVersion = getPluginMeta().getVersion();
            JsonObject metadata = obj.getAsJsonObject("metadata");
            JsonObject versioning = metadata.getAsJsonObject("versioning");
            String latestVersion = versioning.get("release").getAsString();
            if(!Objects.equals(currentVersion, latestVersion)) {
                getLogger().warning("New version available: " + latestVersion);
            }
            loadUpdates(UpdateHandler.checkForUpdates());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override @ApiStatus.Internal
    public void onDisable() {
        for(PlayerData data : PlayerDataHandler.getPlayerData().values()) {
            data.setPulled(false);
            PlayerDataHandler.getPdc().replaceOne(Filters.eq("playerUuid", data.getPlayerUuid()), data);
        }
        PlayerDataHandler.getPlayerData().clear();

        try {
            globalRabbit.delete("global");
            globalRabbit.close();
            globalRedis.close();
            mongo.getClient().close();
        } catch (Exception e) {
            getLogger().severe("Failed to close connections: " + e.getMessage());
        }


        ModuleManager.trashModules();
    }


    /**
     * Loads commands.
     */
    public void loadCommands() {
        toRegister.add(new ModuleCommand("ctrlctr", "Main command for Control Center", new InformationCommand()));
        toRegister.add(new ModuleCommand("reloaddata", "Reloads data from storage", new ReloadPlayerData()));
    }


    /**
     * Sets up the economy.
     * @return Whether the economy was set up successfully.
     */
    private boolean setupEco() {
        if(Bukkit.getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null)
            return false;

        econ = rsp.getProvider();
        return econ != null;
    }

    /**
     * Converts a duration to a human-readable string.
     * @param d Duration
     * @return Human-readable string
     */
    private String convertToHumanReadable(Duration d) {
        if(d.toMinutes() < 1) {
            return d.getSeconds() + " seconds ago";
        } else if(d.toHours() < 1) {
            return d.toMinutes() + " minutes ago";
        } else if(d.toDays() < 1) {
            return d.toHours() + " hours ago";
        } else if(d.toDays() < 7) {
            return d.toDays() + " days ago";
        } else {
            return d.toDays() / 7 + " weeks ago";
        }
    }

    /**
     * Loads updates for modules.
     * @param updates Updates
     */
    @ApiStatus.Internal
    public void loadUpdates(Map<Module, Version> updates) {
        new Thread(() -> {
            if(!updates.isEmpty()) {
                getLogger().warning("Updates available:");
                for(Map.Entry<Module, Version> entry : updates.entrySet()) {
                    getLogger().warning("  " + entry.getKey().getData().name + " - " + entry.getValue().latestVersion() + " (Current: " + entry.getValue().currentVersion() + ")");
                    if(getMainConfig().getBoolean("auto-update-enabled")) {
                        getLogger().warning("  Updating...");
                        UpdateDownloader.download(
                                entry.getValue().directDownloadUrl(),
                                new File(getDataFolder(), "updates/" + entry.getKey().getData().name + "-" + entry.getValue().latestVersion() + ".jar").toPath(),
                                entry.getValue().expectedSha1(),
                                entry.getValue().expectedMd5()
                        );
                    }
                }
                getLogger().info("Updates have been downloaded.");
                getLogger().info("Please restart the server to apply the updates.");
            }
        }).start();
    }

}
