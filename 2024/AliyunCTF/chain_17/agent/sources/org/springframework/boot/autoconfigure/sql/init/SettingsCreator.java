package org.springframework.boot.autoconfigure.sql.init;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sql/init/SettingsCreator.class */
final class SettingsCreator {
    private SettingsCreator() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DatabaseInitializationSettings createFrom(SqlInitializationProperties properties) {
        DatabaseInitializationSettings settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(scriptLocations(properties.getSchemaLocations(), "schema", properties.getPlatform()));
        settings.setDataLocations(scriptLocations(properties.getDataLocations(), "data", properties.getPlatform()));
        settings.setContinueOnError(properties.isContinueOnError());
        settings.setSeparator(properties.getSeparator());
        settings.setEncoding(properties.getEncoding());
        settings.setMode(properties.getMode());
        return settings;
    }

    private static List<String> scriptLocations(List<String> locations, String fallback, String platform) {
        if (locations != null) {
            return locations;
        }
        List<String> fallbackLocations = new ArrayList<>();
        fallbackLocations.add("optional:classpath*:" + fallback + "-" + platform + ".sql");
        fallbackLocations.add("optional:classpath*:" + fallback + ".sql");
        return fallbackLocations;
    }
}
