package dev.expx.ctrlctr.center.communication.data;


import lombok.Getter;

/**
 * Represents a data set, used as a key-value pair while
 * transmitting data between the 2 or more locations.
 */
@SuppressWarnings("unused")
public record DataSet(String key, Object value) {

    /**
     * Creates a new data set.
     *
     * @param key   Key
     * @param value Value
     */
    public DataSet {
    }

}