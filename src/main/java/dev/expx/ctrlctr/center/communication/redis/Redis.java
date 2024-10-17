package dev.expx.ctrlctr.center.communication.redis;

import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.communication.data.DataSet;
import dev.expx.ctrlctr.center.communication.data.Packet;
import dev.expx.ctrlctr.center.datastore.Registry;
import dev.expx.ctrlctr.center.datastore.implementations.EclipseStore;
import dev.expx.ctrlctr.center.logger.Log;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Redis is a program that allows for easy
 * communication between servers using the
 * Redis Pub/Sub system.
 */
@SuppressWarnings("unused")
public class Redis {

    /**
     * Handlers for Redis messages. These
     * are registered by the user and are
     * called when a message is received.
     */
    private final Registry<String, Consumer<Packet>> handler = new EclipseStore<>();


    private final String ip;
    private final int port;
    private final String password;
    private final String channel;

    private Jedis jedis;

    /**
     * Creates a new Redis instance.
     * @param ip The IP address of the Redis server.
     * @param port The port of the Redis server.
     * @param password The password of the Redis server.
     * @param channel The channel to subscribe to.
     */
    public Redis(final String ip, final int port, final String password, final String channel) {
        this.ip = ip;
        this.port = port;
        this.password = password;
        this.channel = channel;
        this.connect();
    }

    /**
     * Connects to the Redis server.
     */
    @ApiStatus.Internal
    public void connect() {
        try {
            this.jedis = new Jedis(this.ip, this.port);
            if (!password.isEmpty())
                this.jedis.auth(password);

            this.jedis.connect();

            new Thread(this::subscribe).start();
            Log.log(Level.INFO, "Connected to Redis Server");
            Ctrlctr.setRedisConnected(true);
        } catch(Exception e) {
            Log.log(Level.SEVERE, "Unable to connect to Redis Server");
            Ctrlctr.setRedisConnected(false);
        }
    }

    /**
     * Checks if the Redis server is connected.
     * @return True if the Redis server is connected.
     */
    public boolean isConnected() {
        return this.jedis.isConnected();
    }

    /**
     * Subscribes to the Redis channel.
     * Used internally to receive and
     * relay messages to the handlers.
     */
    @ApiStatus.Internal
    public void subscribe() {
        try {
            try (final Jedis subscriber = new Jedis(this.ip, this.port)) {
                if (!password.isEmpty())
                    subscriber.auth(password);
                final JedisPubSub pubSub = new JedisPubSub() {
                    @Override
                    public void onPMessage(String pattern, String channel, String message) {
                        final Packet p = Packet.fromJSON(message);
                        receive(p);
                    }
                };
                this.jedis.psubscribe(pubSub, this.channel);
            }
        } catch(Exception e) {
            Log.log(Level.SEVERE, "Unable to connect to Redis Server");
            Ctrlctr.setRedisConnected(false);
        }
    }

    /**
     * Closes the connection to the Redis server.
     */
    public void close() { this.jedis.close(); }

    /**
     * Registers a handler for a specific header.
     * @param header The header to register.
     * @param handler The handler to call when a message is received.
     */
    public void register(
            @NotNull final String header,
            @NotNull final Consumer<Packet> handler
    ) {
        this.handler.register(header, handler);
    }

    /**
     * Receives a packet from the Redis server.
     * @param packet The packet to receive.
     */
    @ApiStatus.Internal
    public void receive(@NotNull final Packet packet) {
        final Optional<Consumer<Packet>> handler = this.handler.get(packet.getHead());
        handler.ifPresent(consumer -> consumer.accept(packet));
    }

    /**
     * Publishes a packet to the Redis server.
     * @param head The header of the packet.
     * @param body The body of the packet.
     */
    public void publish(@NotNull final String head, @NotNull final DataSet... body) {
        try(final Jedis publisher = new Jedis(this.ip, this.port)) {
            publisher.auth(this.password);
            publisher.publish(this.channel, new Packet(head, body).toJSON());
        }
    }

    /**
     * Sets a packet in the Redis server.
     * @param key The key to set the packet to.
     * @param head The header of the packet.
     * @param body The body of the packet.
     */
    public void set(@NotNull final String key, @NotNull final String head, @NotNull final DataSet... body) {
        try(final Jedis setter = new Jedis(this.ip, this.port)) {
            if(!password.isEmpty())
                setter.auth(password);
            setter.set(key, new Packet(head, body).toJSON());
        }
    }

    /**
     * Adds a packet to the Redis server with a Time To Live.
     * @param key The key to get the packet from.
     * @param ttlInSeconds The Time To Live of the packet.
     * @param head The header of the packet.
     * @param body The body of the packet.
     */
    public void set(@NotNull final String key, int ttlInSeconds, @NotNull final String head, @NotNull final DataSet... body) {
        try(final Jedis setter = new Jedis(this.ip, this.port)) {
            if (!password.isEmpty())
                setter.auth(password);
            this.jedis.setex(key, ttlInSeconds, new Packet(head, body).toJSON());
        }
    }

    /**
     * Gets a packet from the Redis server.
     * @param key The key to get the packet from.
     * @return {@link Packet} The packet from the Redis server.
     */
    public Packet get(@NotNull final String key) {
        try(final Jedis getter = new Jedis(this.ip, this.port)) {
            if (!password.isEmpty())
                getter.auth(password);
            String packetJson = getter.get(key);
            Packet packet;
            try {
                packet = Packet.fromJSON(packetJson);
            } catch (Exception e) {
                return null;
            }
            return packet;
        }
    }

    /**
     * Deletes a packet from the Redis server.
     * @param keys The keys to delete the packet from.
     */
    public void delete(@NotNull final String... keys) {
        try(final Jedis deleter = new Jedis(this.ip, this.port)) {
            if (!password.isEmpty())
                deleter.auth(password);
            deleter.del(keys);
        }
    }
}
