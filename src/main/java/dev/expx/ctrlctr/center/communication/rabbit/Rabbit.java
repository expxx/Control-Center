package dev.expx.ctrlctr.center.communication.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import dev.expx.ctrlctr.center.communication.data.Packet;
import dev.expx.ctrlctr.center.communication.data.AuthSet;
import dev.expx.ctrlctr.center.communication.data.ConnSet;
import dev.expx.ctrlctr.center.datastore.Registry;
import dev.expx.ctrlctr.center.datastore.implementations.EclipseStore;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.AlreadyBoundException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * RabbitMQ is a message broker that allows you to
 * send messages between different services.
 */
@SuppressWarnings("unused")
public class Rabbit {

    /**
     * Handlers for RabbitMQ messages. These
     * are registered by the user and are
     * called when a message is received.
     */
    private static final Registry<String, Consumer<Packet>> handlers = new EclipseStore<>();

    private Connection connection;
    private Channel channel;
    private String registeredQueue;

    /**
     * Subscribes to a queue from
     * RabbitMQ, handlers registered
     * later.
     * @param queue The queue to subscribe to
     * @param authSet The authentication settings for RabbitMQ
     * @param connSet The connection settings for RabbitMQ
     */
    public Rabbit(String queue, ConnSet connSet, AuthSet authSet) {
        try {
            boolean exists = false;
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getName().equals("RabbitMQ [" + queue + "]")) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                throw new AlreadyBoundException("Queue already exists");
            } else {
                registeredQueue = queue;
                new Thread(() -> {
                    try {
                        ConnectionFactory factory = new ConnectionFactory();
                        factory.setHost(connSet.ip());
                        factory.setPort(connSet.port());
                        factory.setUsername(authSet.user());
                        factory.setPassword(authSet.pass());
                        try {
                            connection = factory.newConnection();
                            channel = connection.createChannel();
                            channel.queueDeclare(queue, false, false, false, null);

                            DeliverCallback callback = (tag, deliver) -> {
                                String msg = new String(deliver.getBody(), StandardCharsets.UTF_8);
                                receive(queue, Packet.fromJSON(msg));
                            };
                            channel.basicConsume(queue, true, callback, tag -> {
                            });
                        } catch (Exception e) {
                            LoggerFactory.getLogger(Rabbit.class).error(e.getMessage());
                        }
                        LoggerFactory.getLogger(Rabbit.class).info("RabbitMQ Connected");
                    } catch (Exception e) {
                        LoggerFactory.getLogger(Rabbit.class).error(e.getMessage());
                    }
                }, "RabbitMQ [" + queue + "]").start();
            }
        } catch(Exception e) {
            LoggerFactory.getLogger(Rabbit.class).error(e.getMessage());
        }
    }

    /**
     * Used internally to run consumers
     * on any RabbitMQ message received.
     * @param queue The queue that the message came from
     * @param packet The packet that came through
     */
    @ApiStatus.Internal
    public void receive(final String queue, final Packet packet) {
        final Optional<Consumer<Packet>> consumer = handlers.get(queue);
        consumer.ifPresent(packetConsumer -> packetConsumer.accept(packet));
    }

    /**
     * Register a consumer to handle
     * messages from the specific
     * queue in RabbitMQ
     * @param queue The queue to listen for
     * @param consumer Handle results
     */
    @SuppressWarnings("unused")
    public void register(String queue, Consumer<Packet> consumer) {
        handlers.register(queue, consumer);
    }

    /**
     * Send a message through the
     * RabbitMQ connection this
     * instance is connected to.
     *
     * @param packet The packet to send
     */
    public void send(Packet packet) {
        try {
            channel.queueDeclare(registeredQueue, false, false, false, null);
            channel.basicPublish("", registeredQueue, null, packet.toJSON().getBytes());
        } catch(IOException ex) {
            LoggerFactory.getLogger(Rabbit.class).error(ex.getMessage());
        }
    }

    /**
     * Delete a queue from RabbitMQ
     * @param queue The queue to delete
     */
    public void delete(String queue) {
        try {
            channel.queueDelete(queue);
        } catch(IOException ex) {
            LoggerFactory.getLogger(Rabbit.class).error(ex.getMessage());
        }
    }

    /**
     * Close the RabbitMQ connection
     */
    public void close() {
        try {
            channel.close();
            connection.close();
        } catch(IOException | TimeoutException ex) {
            LoggerFactory.getLogger(Rabbit.class).error(ex.getMessage());
        }
    }

}
