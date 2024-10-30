package dev.expx.ctrlctr.center.util;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.expx.ctrlctr.center.Statics;
import org.bukkit.plugin.java.JavaPlugin;

public interface ServerIF {

    default ProxyServer proxyInterface() {
        return null;
    }
    default JavaPlugin paperInterface() {
        return null;
    }

    default String version() {
        if(Statics.serverType == ServerType.VELOCITY) {
            return proxyInterface().getVersion().getVersion();
        } else if(Statics.serverType == ServerType.PAPERMC) {
            return paperInterface().getDescription().getVersion();
        }
        return null;
    }
}
