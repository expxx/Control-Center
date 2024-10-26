package dev.expx.ctrlctr.center.util;

import com.google.gson.Gson;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;
import java.util.Map;

/**
 * Item utility.
 */
@SuppressWarnings("unused")
public class ItemUtil {

    /**
     * Utility Class, do not instantiate.
     */
    private ItemUtil() {}

    private static final Gson gson = new Gson();

    /**
     * Serialize an item stack.
     * @param is Item stack
     * @return Serialized item stack
     */
    public static String serialize(ItemStack is) {
        Map<String, Object> map = is.serialize();
        String json = gson.toJson(map);
        return Base64.getEncoder().encodeToString(json.getBytes());
    }

    /**
     * Deserialize an item stack.
     * @param serialized Serialized item stack
     * @return Item stack
     */
    public static ItemStack deserialize(String serialized) {
        String json = new String(Base64.getDecoder().decode(serialized));
        @SuppressWarnings("unchecked") Map<String, Object> map = gson.fromJson(json, Map.class);
        return ItemStack.deserialize(map);
    }

}
