package dev.expx.ctrlctr.center.communication.data;

import com.google.gson.Gson;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Represents a packet that is sent between 2 or more
 * locations to transmit data.
 */
public record Packet(String head, DataSet... body) {

    private static final Gson GSON = new Gson();

    /**
     * Creates a new packet with the given head and body.
     *
     * @param head The head of the packet.
     * @param body The body of the packet.
     */
    public Packet {
    }

    /**
     * Creates a new packet from a {@link String} JSON with the given head and body.
     *
     * @param json The JSON to create the packet from.
     * @return {@link Packet} The packet created from the JSON.
     */
    @NotNull
    public static Packet fromJSON(@NotNull final String json) {
        return Packet.GSON.fromJson(json, Packet.class);
    }

    /**
     * Converts the packet to a JSON {@link String}.
     *
     * @return {@link String} The JSON representation of the packet.
     */
    @NotNull
    public String toJSON() {
        return Packet.GSON.toJson(this);
    }

    /**
     * Gets the data with the given key from the packet.
     *
     * @param key The key of the data to get.
     * @return {@link DataSet} The data with the given key.
     */
    @Nullable
    @SuppressWarnings("unused")
    public DataSet getData(@NotNull final String key) {

        for (@NotNull final DataSet data : this.body) {
            if (!data.key().equals(key)) {
                continue;
            }

            return data;
        }

        return null;
    }

}