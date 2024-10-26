package dev.expx.ctrlctr.center.licensing.util;

import com.google.gson.JsonObject;
import dev.expx.ctrlctr.center.http.JSONRequest;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

/**
 * Utility class for interacting
 * with Discord.
 */
@SuppressWarnings("unused")
@ApiStatus.Experimental
public class DiscordUtil {

    /**
     * Utility class, do not instantiate.
     */
    private DiscordUtil() {}

    /**
     * Gets the name of a user from their ID.
     * @param id ID
     * @return Name
     */
    public static String getNameFromId(String id) {
        try {
            JsonObject object = JSONRequest.request(
                    "https://auth.expx.dev/api/extra/v1/discordtoname?id=" + id,
                    "GET",
                    HashMap.newHashMap(0),
                    false,
                    null
            );
            JsonObject data = object.getAsJsonObject("data");
            if (data.has("global_name"))
                return data.get("global_name").getAsString();
            return data.get("username").getAsString();
        } catch (Exception e) {
            return "Unknown";
        }
    }

}
