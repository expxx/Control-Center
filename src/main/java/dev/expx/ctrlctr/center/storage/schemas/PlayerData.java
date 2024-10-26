package dev.expx.ctrlctr.center.storage.schemas;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Serializable;
import java.util.UUID;

/**
 * Player Data Schema
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
