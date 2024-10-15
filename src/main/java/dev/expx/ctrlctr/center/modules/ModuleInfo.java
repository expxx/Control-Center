package dev.expx.ctrlctr.center.modules;

import dev.expx.ctrlctr.center.logger.Log;

import java.util.List;
import java.util.logging.Level;

/**
 * ModuleInfo class
 */
public class ModuleInfo {

    /**
     * Module ID
     */
    public final String id;

    /**
     * Module name
     */
    public final String name;

    /**
     * Module description
     */
    @SuppressWarnings("unused")
    public final String desc;

    /**
     * Module version
     */
    public final String version;

    /**
     * Module dependencies
     */
    public final List<String> deps;

    /**
     * ModuleInfo constructor
     * @param id Module ID
     * @param name Module Name
     * @param desc Module Description
     * @param version Module Version
     * @param deps Module Dependencies
     */
    public ModuleInfo(
            String id,
            String name,
            String desc,
            String version,
            List<String> deps
    ) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.version = version;
        this.deps = deps;

        for(String dep : deps) {
            if(dep.matches("[0-9a-z-]+"))
                continue;
            Log.log(Level.WARNING, "[MODULE] Module ({0}) has invalid dependencies: {1}", name, dep);
        }
    }
}
