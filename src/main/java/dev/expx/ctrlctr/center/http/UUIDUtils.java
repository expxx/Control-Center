package dev.expx.ctrlctr.center.http;

import com.google.gson.JsonObject;
import dev.expx.ctrlctr.center.Ctrlctr;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
/**
 * Utility class for UUIDs
 */
@SuppressWarnings("unused")
public class UUIDUtils {

    private UUIDUtils() {
        throw new UnsupportedOperationException();
    }

    private static final String UUID_REGEX             = "[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}";
    private static final String TRIMMED_UUID_REGEX     = "[a-f0-9]{12}4[a-f0-9]{3}[89aAbB][a-f0-9]{15}";
    private static final String ADD_UUID_HYPHENS_REGEX = "([a-f0-9]{8})([a-f0-9]{4})(4[a-f0-9]{3})([89aAbB][a-f0-9]{3})([a-f0-9]{12})";

    /**
     * Turn a Minecraft Username into a Minecraft
     * UUID
     * @param username Username of the player to look up
     * @return String UUID
     */
    public static String fromName(String username) {
        try {
            JsonObject obj = JSONRequest.request(
                    "https://api.mojang.com/users/profiles/minecraft/" + username,
                    "GET",
                    HashMap.newHashMap(0),
                    false,
                    null
            );
            if(obj.get("errorMessage") != null) { return ""; }
            return obj.get("id").getAsString();
        }catch(IOException e) {
            LoggerFactory.getLogger(UUIDUtils.class).error(Ctrlctr.getLang().lang("http-uuid-fetch-error", e.getMessage()));
        }
        return "";
    }

    /**
     * Transforms a trimmed UUID string into a UUID object
     *
     * @param input A UUID string (may or may not be trimmed)
     * @return The input string as a UUID object
     * @throws IllegalArgumentException If the input string is not a valid trimmed/untrimmed UUID
     */
    @SuppressWarnings("unused")
    public static UUID fromTrimmed(String input) {
        if (!isUuid(input)) {
            throw new IllegalArgumentException(Ctrlctr.getLang().lang("http-uuid-not-uuid", input));

        } else if (input.contains("-")) {
            // Already has hyphens
            return UUID.fromString(input);
        }

        return UUID.fromString(input.replaceAll(ADD_UUID_HYPHENS_REGEX, "$1-$2-$3-$4-$5"));
    }

    /**
     * Trims a UUID of all it's hyphens
     *
     * @param input A UUID string (may or may not be trimmed)
     * @return UUID as a string without hyphens
     */
    public static String trim(UUID input) {
        return trim(input.toString());
    }

    /**
     * Trims a UUID of all it's hyphens
     *
     * @param input Input string
     * @return Input string stripped of hyphens
     */
    public static String trim(String input) {
        return input.replace("-", "");
    }

    /**
     * Checks if a string is a UUID
     *
     * @param input Input string
     * @return Whether the input string is a UUID (may or may not be trimmed)
     */
    public static boolean isUuid(String input) {
        return input.matches(TRIMMED_UUID_REGEX) || input.matches(UUID_REGEX);
    }

}
