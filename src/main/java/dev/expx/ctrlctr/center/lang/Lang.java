package dev.expx.ctrlctr.center.lang;

import dev.expx.ctrlctr.center.util.TextUtil;
import net.kyori.adventure.text.TextComponent;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Lang {

    private final ResourceBundle bundle;

    public Lang(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String lang(String key, Object... args) {
        if(bundle == null)
            throw new IllegalStateException("Bundle not yet loaded");
        String pattern = bundle.getString(key);
        return MessageFormat.format(pattern, args);
    }
    public TextComponent langComponent(String key, Object... args) { return TextUtil.translate(lang(key, args)); }

    public String lang(String key) {
        if(bundle == null)
            throw new IllegalStateException("Bundle not yet loaded");
        return bundle.getString(key);
    }
    public TextComponent langComponent(String key) { return TextUtil.translate(lang(key)); }
}
