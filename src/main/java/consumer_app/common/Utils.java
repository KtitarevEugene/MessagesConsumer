package consumer_app.common;

import org.ini4j.Ini;
import org.ini4j.Profile;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public class Utils {
    private Utils() {}

    public static void addConfigParams(@NotNull Ini config, String sectionName, @NotNull String[] params, Properties properties) {
        Profile.Section section = config.get(sectionName);

        for (String param : params) {
            String val = section.get(param);

            if (val != null) {
                properties.setProperty(param, val);
            }
        }
    }

    public static void addAllConfigParamsFromSection(@NotNull Ini config,
                                                     @NotNull String sectionName,
                                                     @NotNull Properties properties) {

        Profile.Section section = config.get(sectionName);

        for (String key : section.keySet()) {
            properties.setProperty(key, section.get(key));
        }
    }
}
