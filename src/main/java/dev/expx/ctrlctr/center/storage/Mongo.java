package dev.expx.ctrlctr.center.storage;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.expx.ctrlctr.center.Ctrlctr;
import dev.expx.ctrlctr.center.communication.data.AuthSet;
import dev.expx.ctrlctr.center.communication.data.ConnSet;
import dev.expx.ctrlctr.center.lang.Lang;
import dev.expx.ctrlctr.center.storage.schemas.PlayerData;
import lombok.Getter;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * MongoDB Connection Handler
 */
@Getter
public class Mongo {

    private final Lang lang = Ctrlctr.getLang();

    /**
     * Instigate a MongoDB Connection
     */
    public Mongo() {}

    /**
     * MongoDB Client
     * <p>
     * Avoid using this, use {@link Mongo#getDatabase()} instead
     */
    private MongoClient client;

    /**
     * MongoDB Database
     */
    private MongoDatabase database;

    /**
     * MongoDB Collection for PlayerData
     */
    private MongoCollection<PlayerData> playerDataMongoCollection;

    /**
     * Create a connection for a MongoDB Server
     * This is an internal method, and called to the
     * configured MongoDB Server in the config-ctrlctr.yml.
     * <p>
     * If access to the database is required in modules,
     * use Mongo#getDatabase() or Mongo#getClient()
     *
     * @param connSet Connection Settings
     * @param authSet Authentication Settings
     *
     * @return The MongoDB Connection Handler
     */
    @ApiStatus.Internal
    public Mongo connectMongo(ConnSet connSet, AuthSet authSet, String dbName) {
        try {
            java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.SEVERE);
            java.util.logging.Logger.getLogger("org.mongodb.driver.client").setLevel(java.util.logging.Level.SEVERE);
            java.util.logging.Logger.getLogger("org.mongodb.driver.connection").setLevel(java.util.logging.Level.SEVERE);


            Logger l = LoggerFactory.getLogger(Mongo.class);
            PojoCodecProvider codecProvider = PojoCodecProvider.builder()
                    .automatic(true)
                    .build();
            CodecRegistry pojoCodecRegistry = fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(codecProvider)
            );
            MongoClientSettings settings = MongoClientSettings.builder()
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .applyConnectionString(new ConnectionString(
                            "mongodb://" + authSet.user() + ":" + authSet.pass() + "@" + connSet.ip() + ":" +
                                    connSet.port() + "/" + dbName + "?authSource=admin"
                    ))
                    .codecRegistry(pojoCodecRegistry)
                    .build();
            client = MongoClients.create(settings);
            database = client.getDatabase(dbName).withCodecRegistry(pojoCodecRegistry);
            Ctrlctr.setMongoConnected(true);
            return this;
        } catch(Exception ex) {
            LoggerFactory.getLogger(Mongo.class).error(ex.getMessage());
            Ctrlctr.setMongoConnected(false);
            return null;
        }
    }

    public void setPlayerDataMongoCollection(String collectionName) {
        playerDataMongoCollection = database.getCollection(collectionName, PlayerData.class);
    }
}
