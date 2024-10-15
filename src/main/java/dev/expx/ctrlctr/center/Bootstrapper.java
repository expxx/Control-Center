package dev.expx.ctrlctr.center;

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
        log.info("SYSTEM MODEL: {}", hal.getComputerSystem().getModel());
        log.info("SYSTEM MANUFACTURER: {}", hal.getComputerSystem().getManufacturer());
        log.info("SYSTEM FIRMWARE: {}", hal.getComputerSystem().getFirmware());
        log.info("SYSTEM SERIAL: {}", hal.getComputerSystem().getSerialNumber());
        log.info("BOARD MODEL: {}", hal.getComputerSystem().getBaseboard().getModel());
        log.info("BOARD MANUFACTURER: {}", hal.getComputerSystem().getBaseboard().getManufacturer());
        log.info("BOARD VERSION: {}", hal.getComputerSystem().getBaseboard().getVersion());
        log.info("BOARD SERIAL: {}", hal.getComputerSystem().getBaseboard().getSerialNumber());
        int i = 0;
        for(PhysicalMemory memory : hal.getMemory().getPhysicalMemory()) {
            i++;
            log.info("-- RAM {}:", i);
            log.info("    - MANUFACTURER: {}", memory.getManufacturer());
            log.info("    - TYPE: {}", memory.getMemoryType());
            log.info("    - CAPACITY: {}", memory.getCapacity());
            log.info("    - CLOCK SPEED: {}", memory.getClockSpeed());
        }
        i = 0;
        for(PowerSource source : hal.getPowerSources()) {
            i++;
            log.info("-- POWER SOURCE {}:", i);
            log.info("    - NAME: {}", source.getName());
            log.info("    - AMPERAGE: {}", source.getAmperage());
            log.info("    - MANUFACTURER: {}", source.getManufacturer());
            log.info("    - MANUFACTUER DATE: {}", source.getManufactureDate());
            log.info("    - DEVICE NAME: {}", source.getDeviceName());
            log.info("    - POWER USAGE: {}", source.getPowerUsageRate());
            log.info("    - TEMPERATURE: {}", source.getTemperature());
            log.info("    - CURRENT CAPACITY: {}", source.getCurrentCapacity());
            log.info("    - CYCLE COUNT: {}", source.getCycleCount());
            log.info("    - MAX CAPACITY: {}", source.getMaxCapacity());
            log.info("    - VOLTAGE: {}", source.getVoltage());
            log.info("    - CHARGING: {}", source.isCharging());
            log.info("    - DISCHARGING: {}", source.isDischarging());
            log.info("    - POWER ONLINE: {}", source.isPowerOnLine());
        }
        i = 0;
        for(NetworkIF nif : hal.getNetworkIFs()) {
            i++;
            log.info("-- NETWORK INTERFACE {}:", i);
            log.info("    - NAME: {}", nif.getName());
            log.info("    - DISPLAY NAME: {}", nif.getDisplayName());
            log.info("    - BYTES IN: {}", nif.getBytesRecv());
            log.info("    - BYTES OUT: {}", nif.getBytesSent());
            log.info("    - ALIAS: {}", nif.getIfAlias());
            log.info("    - TYPE: {}", nif.getIfType());
            log.info("    - DROPS: {}", nif.getInDrops());
            log.info("    - IN ERRORS: {}", nif.getInErrors());
            log.info("    - OUT ERRORS: {}", nif.getOutErrors());
            log.info("    - IPv4: {}", nif.getIPv4addr() == null ? "Unknown" : nif.getIPv4addr());
            log.info("    - IPv6: {}", nif.getIPv6addr() == null ? "Unknown" : nif.getIPv6addr());
            log.info("    - MAC: {}", nif.getMacaddr());
            log.info("    - MTU: {}", nif.getMTU());
            log.info("    - PACKETS IN: {}", nif.getPacketsRecv());
            log.info("    - PACKETS OUT: {}", nif.getPacketsSent());
            log.info("    - SPEED: {}", nif.getSpeed());
        }
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
