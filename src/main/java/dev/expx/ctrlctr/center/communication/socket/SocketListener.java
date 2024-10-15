package dev.expx.ctrlctr.center.communication.socket;

import io.socket.client.Ack;
import org.jetbrains.annotations.Nullable;

/**
 * Socket listener interface.
 */
public interface SocketListener {

    /**
     * Listens for a socket event.
     * @param objects Objects
     * @param callback Callback
     */
    void listen(@Nullable Object[] objects, @Nullable Ack callback);

    /**
     * Gets the name of the listener.
     * @return Name
     */
    String getName();

    /**
     * Gets the channel of the listener.
     * @return Channel
     */
    String getChannel();

}
