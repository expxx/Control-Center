package dev.expx.ctrlctr.center.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

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

    /**
     * Retrieve a PlayerHead object from a
     * Base64 head texture.
     * @param texture Base64 encoded texture
     * @return PlayerHead ItemStack
     */
    public static ItemStack getHeadFromTexture(String texture) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        PlayerProfile p = Bukkit.createProfile(UUID.randomUUID(), UUID.randomUUID().toString());
        p.setProperty(new ProfileProperty("textures", texture));
        meta.setOwnerProfile(p);
        head.setItemMeta(meta);

        return head;
    }

}
