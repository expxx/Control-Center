package dev.expx.ctrlctr.center.modules;

import dev.expx.ctrlctr.center.Ctrlctr;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ModuleInfo class
 *
 * @param id      Module ID
 * @param name    Module name
 * @param desc    Module description
 * @param version Module version
 * @param deps    Module dependencies
 */
public record ModuleInfo(String id, String name, @SuppressWarnings("unused") String desc, String version,
                         List<String> deps) {

    /**
     * ModuleInfo constructor
     *
     * @param id      Module ID
     * @param name    Module Name
     * @param desc    Module Description
     * @param version Module Version
     * @param deps    Module Dependencies
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

        for (String dep : deps) {
            if (dep.matches("[0-9a-z-]+"))
                continue;
            LoggerFactory.getLogger(ModuleInfo.class).error(Ctrlctr.getLang().lang("module-error-invalid-dependencies", id, dep));
        }
    }
}
