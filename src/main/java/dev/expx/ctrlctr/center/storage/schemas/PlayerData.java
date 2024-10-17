package dev.expx.ctrlctr.center.storage.schemas;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.UUID;

/**
 * Player Data Schema
 *
 * @usage Internally, not really intended to store any actual data, more-so just for determining whether a player's data is currently in-use somewhere so we don't get duplicate data.
 */
@Getter @Setter
public class PlayerData implements Serializable {

    /**
     * Player UUID
     */
    @Unmodifiable
    private UUID playerUuid;

    /**
     * Whether the player's data is
     * currently pulled somewhere
     * else.
     */
    private boolean pulled = false;

    /**
     * Used for Mongo POJO
     */
    @SuppressWarnings("unused")
    public PlayerData() {}

    /**
     * Create a new PlayerData object
     * @param playerUuid Player UUID
     */
    public PlayerData(@NotNull UUID playerUuid) {
        this.playerUuid = playerUuid;
    }
}
