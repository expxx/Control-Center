package dev.expx.ctrlctr.center.communication.socket;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.communication.data.ConnSet;
import dev.expx.ctrlctr.center.logger.errors.StorageConnectionException;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SocketIO is a program that allows for
 * transferring data between servers with
 * a callback.
 */
@ApiStatus.Experimental
@SuppressWarnings("unused")
public class SocketIO {

    private SocketIO() {}

    final static Logger l = LoggerFactory.getLogger(SocketIO.class);

    @Getter
    @Setter
    public static Socket io;
    private static final List<SocketListener> pendingRegistration = new ArrayList<>();

    /**
     * Connects to a socket server.
     *
     * @param connSet The connection data.
     * @param secure  Whether the connection is secure.
     * @return {@link Socket} The socket.
     */
    @SuppressWarnings("unused")
    public static Socket connect(ConnSet connSet, boolean secure) {
        URI uri;
        if (secure)
            uri = URI.create("https://" + connSet.ip() + ":" + connSet.port());
        else
            uri = URI.create("http://" + connSet.ip() + ":" + connSet.port());

        try {
            io = IO.socket(uri);
            Bukkit.getScheduler().runTaskTimerAsynchronously(Ctrlctr.getInstance(), () -> {
                if (!pendingRegistration.isEmpty() && (!io.connected())) {
                    for (SocketListener listener : pendingRegistration) {
                        l.info(Ctrlctr.getLang().lang("socket-start", listener.getName()));
                        io.on(listener.getChannel(), obj -> {
                            if (Arrays.stream(obj).toList().getLast() instanceof Ack ack) {
                                listener.listen(obj, ack);
                            } else {
                                listener.listen(obj, null);
                            }
                        });
                    }
                }
            }, 0, 5000);
            return io;
        } catch (Exception e) {
            throw new StorageConnectionException(e.getMessage());
        }
    }

    /**
     * Registers a listener to the socket.
     *
     * @param listener {@link SocketListener} The listener to register.
     */
    @SuppressWarnings("unused")
    public static void register(SocketListener listener) {
        if (io != null) {
            if (!io.connected()) {
                l.info(Ctrlctr.getLang().lang("socket-pending", listener.getName(), listener.getClass().getName()));
                pendingRegistration.add(listener);
            } else {
                l.info(Ctrlctr.getLang().lang("socket-live", listener.getName(), listener.getClass().getName()));
                io.on(listener.getChannel(), obj -> {
                    if (Arrays.stream(obj).toList().getLast() instanceof Ack ack) {
                        listener.listen(obj, ack);
                    } else {
                        listener.listen(obj, null);
                    }
                });
            }
        } else {
            l.info(Ctrlctr.getLang().lang("socket-noio", listener.getName(), listener.getClass().getName()));
            pendingRegistration.add(listener);
        }
    }
}
