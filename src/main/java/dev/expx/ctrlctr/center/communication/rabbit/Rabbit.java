package dev.expx.ctrlctr.center.communication.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.communication.data.Packet;
import dev.expx.ctrlctr.center.communication.rabbit.data.AuthSet;
import dev.expx.ctrlctr.center.communication.rabbit.data.ConnSet;
import dev.expx.ctrlctr.center.datastore.Registry;
import dev.expx.ctrlctr.center.datastore.implementations.EclipseStore;
import dev.expx.ctrlctr.center.logger.Log;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.AlreadyBoundException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * RabbitMQ is a message broker that allows you to
 * send messages between different services.
 */
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
     * @param queues Additional queues you can subscribe to. Performant so there's fewer threads running around.
     * @param authSet The authentication settings for RabbitMQ
     * @param connSet The connection settings for RabbitMQ
     */
    public Rabbit(String queue, ConnSet connSet, AuthSet authSet, String... queues) {
        try {
            boolean exists = false;
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getName().equals("RabbitMQ [" + queue + " " + Arrays.toString(queues))) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                throw new AlreadyBoundException("The thread is already initialized.");
            } else {
                registeredQueue = queue;
                new Thread(() -> {
                    try {
                        ConnectionFactory factory = new ConnectionFactory();
                        factory.setHost(connSet.getIp());
                        factory.setPort(connSet.getPort());
                        factory.setUsername(authSet.getUser());
                        factory.setPassword(authSet.getPass());
                        try {
                            connection = factory.newConnection();
                            channel = connection.createChannel();
                            channel.queueDeclare(queue, false, false, false, null);
                            for (String additional : queues)
                                channel.queueDeclare(additional, false, false, false, null);
                            if (queues.length == 0)
                                LoggerFactory.getLogger(Rabbit.class).info("Listening in on {}", queue);
                            else
                                LoggerFactory.getLogger(Rabbit.class).info("Listening in on {} and {}", queue, Arrays.toString(queues));

                            DeliverCallback callback = (tag, deliver) -> {
                                String msg = new String(deliver.getBody(), StandardCharsets.UTF_8);
                                receive(tag, Packet.fromJSON(msg));
                            };
                            channel.basicConsume(queue, true, callback, tag -> {
                            });
                        } catch (Exception e) {
                            Log.log(Level.SEVERE, "Unable to connect to RabbitMQ Server");
                            Ctrlctr.setRabbitConnected(false);
                        }
                        Ctrlctr.setRabbitConnected(true);
                        Log.log(Level.INFO, "Connected to RabbitMQ Server");
                    } catch (Exception e) {
                        Log.log(Level.SEVERE, "Unable to connect to RabbitMQ Server");
                        Ctrlctr.setRabbitConnected(false);
                    }
                }, "RabbitMQ [" + queue + " " + Arrays.toString(queues)).start();
            }
        } catch(Exception e) {
            Log.log(Level.SEVERE, "Unable to connect to RabbitMQ Server");
            Ctrlctr.setRabbitConnected(false);
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
     * Send a message to a specific queue
     * in RabbitMQ with a packet.
     * @param packet The packet to send
     */
    public void send(Packet packet) {
        try {
            channel.queueDeclare(registeredQueue, false, false, false, null);
            channel.basicPublish("", registeredQueue, null, packet.toJSON().getBytes());
        } catch(IOException ex) {
            Log.log(Level.WARNING, "Unable to send RabbitMQ message to {0}: {1}", registeredQueue, ex.getMessage());
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
            Log.log(Level.WARNING, "Unable to delete RabbitMQ Channel {0}: {1}", queue, ex.getMessage());
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
            Log.log(Level.WARNING, "Unable to close RabbitMQ connection: {0}", ex.getMessage());
        }
    }

}
