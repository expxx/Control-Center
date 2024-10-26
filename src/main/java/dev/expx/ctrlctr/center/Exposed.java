package dev.expx.ctrlctr.center;

import dev.expx.ctrlctr.center.datastore.Registry;
import dev.expx.ctrlctr.center.datastore.implementations.EclipseStore;

@SuppressWarnings("unused")
public class Exposed {

    protected static final Registry<String, Expose> exposable = new EclipseStore<>();

    /**
     * Register a method to expose
     * to the external world of
     * plugins
     * @param key Callable ID
     * @param expose Expose method
     */
    public static void register(String key, Expose expose) {
        exposable.register(key, expose);
    }

    /**
     * Usually used outside the plugin, but
     * can be used inside the plugin. Used for
     * calling a method that was registered
     * through the Exposed API
     * @param key Callable ID
     * @param data Data to pass to the method
     * @return Literally whatever the exposed method returns
     */
    public static Object callExposed(String key, Object... data) {
        return exposable.get(key).map(expose -> expose.expose(data)).orElse(null);
    }


    @FunctionalInterface
    public interface Expose {
        Object expose(Object... data);
    }
}
