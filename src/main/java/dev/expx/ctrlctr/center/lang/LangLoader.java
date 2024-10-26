package dev.expx.ctrlctr.center.lang;

import dev.expx.ctrlctr.center.util.FileUtil;
import lombok.Getter;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

@Getter
public class LangLoader {

    final Locale locale;
    final ResourceBundle bundle;

    public LangLoader(Class<?> clazz, String title, String language, String variant, Path dir) {
        locale = Locale.of(language, variant);
        try {
            File file = new File(dir.toFile(), "lang");
            if(!file.exists()) //noinspection ResultOfMethodCallIgnored
                file.mkdirs();
            FileUtil.trySave(clazz.getClassLoader().getResourceAsStream(title + "_" + language + "_" + variant + ".properties"), new File(dir.toFile(), "lang/" + title + "_" + language + "_" + variant + ".properties"));
            URL[] urls = {file.toURI().toURL()};
            try(URLClassLoader loader = new URLClassLoader(urls)) {
                bundle = ResourceBundle.getBundle(title, locale, loader);
            }
            LoggerFactory.getLogger(LangLoader.class).info(bundle.getString("BUNDLE_LOADED"));
        } catch(Exception e) { throw new IllegalStateException(e); }
    }

}
