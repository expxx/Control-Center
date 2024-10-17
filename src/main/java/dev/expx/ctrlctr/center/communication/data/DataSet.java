package dev.expx.ctrlctr.center.communication.data;


import lombok.Getter;

/**
 * Represents a data set, used as a key-value pair while
 * transmitting data between the 2 or more locations.
 */
@Getter
public final class DataSet {

    private final String key;
    private final Object value;

    /**
     * Creates a new data set.
     * @param key Key
     * @param value Value
     */
    public DataSet(final String key, final Object value) {
        this.key = key;
        this.value = value;
    }

}