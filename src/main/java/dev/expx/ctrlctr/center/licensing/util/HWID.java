package dev.expx.ctrlctr.center.licensing.util;

import dev.expx.ctrlctr.center.Ctrlctr;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Utility class for reading the HWID.
 */
@ApiStatus.Experimental
public class HWID {

    /**
     * Utility class, do not instantiate.
     */
    private HWID() {}

    /**
     * Reads or creates an HWID
     * @return HWID
     */
    public static UUID read() {
        File readFromPath = new File(Ctrlctr.getInstance().getDataFolder(), "modules/local");
        File readFrom = new File(readFromPath, "id");
        try {
            if(!readFromPath.exists() || !readFromPath.isDirectory())
                readFromPath.mkdirs();
            if (!readFrom.exists()) {
                // Write to file
                UUID uuid = UUID.randomUUID();
                if(System.getenv("P_SERVER_UUID") != null) uuid = UUID.fromString(System.getenv("P_SERVER_UUID"));
                Files.write(readFrom.toPath(), uuid.toString().getBytes());
                return uuid;
            } else {
                return UUID.fromString(Files.readString(readFrom.toPath()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
