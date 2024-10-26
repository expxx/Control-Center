package dev.expx.ctrlctr.center;

import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.lang.LangLoader;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

import java.util.ResourceBundle;

/**
 * The bootstrapper for the ControlCenter.
 */
@SuppressWarnings({"unused", "UnstableApiUsage", "SpellCheckingInspection"})
@ApiStatus.Internal
public class Bootstrapper implements PluginBootstrap {

    /**
     * Plugin bootstrapper, provided by PaperMC
     */
    public Bootstrapper() {}

    /**
     * Bootstraps the ControlCenter.
     * @param context Provided by PaperMC
     */
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        ResourceBundle bundle = new LangLoader(getClass(), "lang", "en", "US", context.getDataDirectory()).getBundle();
        Lang lang = new Lang(bundle);

        ComponentLogger log = context.getLogger();
        log.info("");
        log.info(lang.lang("load-header"));
        log.info(lang.lang("load-info1"));
        log.info(lang.lang("load-info2"));
        log.info(lang.lang("load-info3"));

        log.info(lang.lang("load-sysinfo-header"));

        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor cpu = hal.getProcessor();
        log.info(lang.lang("load-sysinfo-jvm", System.getProperty("java.version", "21")));
        log.info(lang.lang("load-sysinfo-os", os.getFamily(), os.getVersionInfo().getVersion(), os.getManufacturer()));
        log.info(lang.lang("load-sysinfo-cpu-model", cpu.getProcessorIdentifier().getModel()));
        log.info(lang.lang("load-sysinfo-cpu-vendor", cpu.getProcessorIdentifier().getVendor()));
        log.info(lang.lang("load-sysinfo-cpu-physical-cores", cpu.getPhysicalProcessorCount()));
        log.info(lang.lang("load-sysinfo-cpu-logical-cores", cpu.getLogicalProcessorCount()));
        log.info(lang.lang("load-sysinfo-cpu-threads", os.getThreadCount()));
        log.info(lang.lang("load-sysinfo-cpu-mhz", cpu.getMaxFreq()));
        log.info(lang.lang("load-sysinfo-total-ram", hal.getMemory().getTotal() / 1024));
        log.info(lang.lang("load-sysinfo-ram-available", hal.getMemory().getAvailable() / 1024));
        log.info(lang.lang("load-sysinfo-ram-used", (hal.getMemory().getTotal() - hal.getMemory().getAvailable()) / 1024));
        log.info(lang.lang("load-sysinfo-virtual-memory-swap-total", hal.getMemory().getVirtualMemory().getSwapTotal() / 1024));
        log.info(lang.lang("load-sysinfo-virtual-memory-swap-used", hal.getMemory().getVirtualMemory().getSwapUsed() / 1024));
        log.info(lang.lang("load-sysinfo-footer"));
        log.info("");
        log.info(lang.lang("load-done"));
    }

    /**
     * Creates the ControlCenter plugin.
     * @param context Provided by PaperMC
     * @return The ControlCenter plugin
     */
    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new Ctrlctr();
    }

}
