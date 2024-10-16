package dev.expx.ctrlctr.center;

import dev.expx.ctrlctr.center.modules.ModuleManager;
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

/**
 * The bootstrapper for the ControlCenter.
 */
@SuppressWarnings({"unused", "UnstableApiUsage"}) @ApiStatus.Internal
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
        ComponentLogger log = context.getLogger();

        log.info("");
        log.info("  -----  LOADING  -----  ");
        log.info("The ControlCenter is booting");
        log.info("up. Please give it a moment,");
        log.info("as this may take some time.");

        log.info("  -----  SYSINFO  -----  ");

        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor cpu = hal.getProcessor();
        log.info("JVM VERSION: {}", System.getProperty("java.version", "21"));
        log.info("OS: {} ({}) ({})", os.getFamily(), os.getVersionInfo().getVersion(), os.getManufacturer());
        log.info("CPU MODEL: {}", cpu.getProcessorIdentifier().getModel());
        log.info("CPU VENDOR: {}", cpu.getProcessorIdentifier().getVendor());
        log.info("CPU PHYSICAL CORES: {}", cpu.getPhysicalProcessorCount());
        log.info("CPU LOGICAL CORES: {}", cpu.getLogicalProcessorCount());
        log.info("CPU TOTAL THREADS: {}", os.getThreadCount());
        log.info("CPU MHZ: {}", cpu.getMaxFreq());
        log.info("TOTAL RAM: {}", hal.getMemory().getTotal() / 1024);
        log.info("RAM AVAILABLE: {}", hal.getMemory().getAvailable() / 1024);
        log.info("RAM USED: {}", (hal.getMemory().getTotal() - hal.getMemory().getAvailable()) / 1024);
        log.info("VIRTUAL MEMORY SWAP TOTAL: {}", hal.getMemory().getVirtualMemory().getSwapTotal() / 1024);
        log.info("VIRTUAL MEMORY SWAP USED: {}", hal.getMemory().getVirtualMemory().getSwapUsed() / 1024);
        log.info("  -----  SYSINFO  -----  ");
        log.info("");
        log.info("Done!");
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
