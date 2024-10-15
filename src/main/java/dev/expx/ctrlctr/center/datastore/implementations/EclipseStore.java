package dev.expx.ctrlctr.center.datastore.implementations;

import dev.expx.ctrlctr.center.datastore.Registry;
import org.eclipse.collections.api.factory.Maps;

import java.util.Map;

/**
 * EclipseStore implementation of the Registry interface.
 * @param <K> Key
 * @param <V> Value
 */
public class EclipseStore<K, V> implements Registry<K, V> {

    /**
     * Creates a new EclipseStore.
     */
    public EclipseStore() {}

    /**
     * The registry
     */
    protected final Map<K, V> map = Maps.mutable.empty();

    /**
     * Gets the registry.
     * @return Registry
     */
    @Override
    public Map<K, V> getRegistry() {
        return this.map;
    }
}