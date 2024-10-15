package dev.expx.ctrlctr.center.util;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Text utility class
 */
public class TextUtil {

    private TextUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Translates legacy minecraft chat into
     * the new Text Component form
     *
     * @param msg Legacy string of text
     * @return {@link TextComponent} version of text
     */
    public static TextComponent translate(String msg) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize("&r" + msg);
    }

}
