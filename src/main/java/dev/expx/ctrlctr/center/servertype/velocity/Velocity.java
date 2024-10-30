package dev.expx.ctrlctr.center.servertype.velocity;

import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.expx.ctrlctr.center.Statics;
import dev.expx.ctrlctr.center.communication.data.AuthSet;
import dev.expx.ctrlctr.center.communication.data.ConnSet;
import dev.expx.ctrlctr.center.communication.rabbit.Rabbit;
import dev.expx.ctrlctr.center.communication.redis.Redis;
import dev.expx.ctrlctr.center.config.YMLUtil;
import dev.expx.ctrlctr.center.http.XMLRequest;
import dev.expx.ctrlctr.center.modules.Module;
import dev.expx.ctrlctr.center.modules.ModuleCommand;
import dev.expx.ctrlctr.center.modules.ModuleManager;
import dev.expx.ctrlctr.center.storage.Mongo;
import dev.expx.ctrlctr.center.update.UpdateDownloader;
import dev.expx.ctrlctr.center.update.UpdateHandler;
import dev.expx.ctrlctr.center.update.Version;
import dev.expx.ctrlctr.center.util.ServerIF;
import dev.expx.ctrlctr.center.util.ServerType;
import dev.expx.ctrlctr.center.util.dependencies.resolver.DirectMavenResolver;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static dev.expx.ctrlctr.center.Statics.*;
import static dev.expx.ctrlctr.center.Statics.mongoAuthSet;

@Plugin(
        id="ctrlctr",
        authors = {"cammyzed"},
        version = "1.0",
        name = "Control-Center",
        url = "https://expx.dev"
)
public class Velocity implements ServerIF {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDir;

    @Inject
    public Velocity(ProxyServer server, Logger logger, @DataDirectory Path dataDir) {
        this.server = server;
        this.logger = logger;
        this.dataDir = dataDir;

        Statics.serverType = ServerType.VELOCITY;
        Statics.serverInterface = this;

        try {
            Thread t = ModuleManager.updateFolder(dataDir);
            t.start();
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            ModuleManager.setupModuleLoader(dataDir);
            new DepMngr(new DirectMavenResolver(), dataDir, this);
        }
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent e) {
        storageConfig = YMLUtil.createConfig(
                new File(dataDir.toFile(), "storage.yml"),
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("storage.yml"))
        );
        mainConfig = YMLUtil.createConfig(
                new File(dataDir.toFile(), "config.yml"),
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.yml"))
        );

        mongoConnSet = new ConnSet(
                storageConfig.getString("mongo.ip"),
                storageConfig.getInt("mongo.port")
        );
        mongoAuthSet = new AuthSet(
                storageConfig.getString("mongo.user"),
                storageConfig.getString("mongo.pass")
        );

        mongo = new Mongo().connectMongo(Statics.mongoConnSet, Statics.mongoAuthSet);


        redisAuthSet = new AuthSet(
                null,
                storageConfig.getString("redis.password")
        );
        redisConnSet = new ConnSet(
                storageConfig.getString("redis.ip"),
                storageConfig.getInt("redis.port")
        );
        globalRedis = new Redis(
                redisConnSet.ip(),
                redisConnSet.port(),
                redisAuthSet.pass(),
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
            ModuleManager.setup(dataDir.toFile());
        } finally {
            if(!toRegister.isEmpty()) {
                for(ModuleCommand mc : toRegister) {
                    if(mc.name() != null && mc.command() != null)
                        server.getCommandManager().register(mc.name(), (Command) mc.command());
                }
            }
        }

        try {
            JsonObject obj = XMLRequest.request(
                    "https://repo.expx.dev/repository/public-releases/dev/expx/ctrlctr/ctrlctr/maven-metadata.xml",
                    "GET",
                    new HashMap<>(),
                    false,
                    null
            );
            String currentVersion = server.getVersion().getVersion();
            JsonObject metadata = obj.getAsJsonObject("metadata");
            JsonObject versioning = metadata.getAsJsonObject("versioning");
            String latestVersion = versioning.get("release").getAsString();
            if(!Objects.equals(currentVersion, latestVersion)) {
                logger.warn(lang.lang("init-new-version", latestVersion));
            }
            loadUpdates(UpdateHandler.checkForUpdates());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }



    }

    /**
     * Loads updates for modules.
     * @param updates Updates
     */
    @ApiStatus.Internal
    public void loadUpdates(Map<dev.expx.ctrlctr.center.modules.Module, Version> updates) {
        new Thread(() -> {
            if(!updates.isEmpty()) {
                for(Map.Entry<Module, Version> entry : updates.entrySet()) {
                    if(entry.getValue().currentVersion().equals(entry.getValue().latestVersion()))
                        continue;
                    if(mainConfig.getBoolean("auto-update-enabled")) {
                        logger.warn(lang.lang("module-updating"));
                        UpdateDownloader.download(
                                entry.getValue().directDownloadUrl(),
                                new File(dataDir.toFile(), "updates/" + entry.getKey().getData().name() + "-" + entry.getValue().latestVersion() + ".jar").toPath(),
                                entry.getValue().expectedSha1(),
                                entry.getValue().expectedMd5()
                        );
                    }
                }
                logger.info(lang.lang("module-update-downloaded"));
            }
        }).start();
    }

}
