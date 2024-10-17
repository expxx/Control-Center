package dev.expx.ctrlctr.center.communication.data;

import lombok.Getter;

/**
 * Represents a set of connection data.
 */
@Getter
public class ConnSet {

    private final String ip;
    private final int port;

    /**
     * Creates a new connection set.
     * @param ip The IP address.
     * @param port The port.
     */
    public ConnSet(
            final String ip,
            final int port
    ) {
        this.ip = ip;
        this.port = port;
    }
}
