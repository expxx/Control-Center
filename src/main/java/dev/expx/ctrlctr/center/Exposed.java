package dev.expx.ctrlctr.center;

import dev.expx.ctrlctr.center.datastore.Registry;
import dev.expx.ctrlctr.center.datastore.implementations.EclipseStore;

public class Exposed {

    protected static final Registry<String, Expose> exposable = new EclipseStore<>();

    public static void register(String key, Expose expose) {
        exposable.register(key, expose);
    }

    public static Object[] callExposed(String key, Object... data) {
        return exposable.get(key).map(expose -> expose.expose(data)).orElse(null);
    }


    @FunctionalInterface
    public interface Expose {
        Object[] expose(Object... data);
    }
}
