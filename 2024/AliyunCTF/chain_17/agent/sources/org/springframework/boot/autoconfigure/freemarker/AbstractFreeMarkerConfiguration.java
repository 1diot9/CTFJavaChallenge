package org.springframework.boot.autoconfigure.freemarker;

import java.util.Properties;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/freemarker/AbstractFreeMarkerConfiguration.class */
abstract class AbstractFreeMarkerConfiguration {
    private final FreeMarkerProperties properties;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractFreeMarkerConfiguration(FreeMarkerProperties properties) {
        this.properties = properties;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final FreeMarkerProperties getProperties() {
        return this.properties;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyProperties(FreeMarkerConfigurationFactory factory) {
        factory.setTemplateLoaderPaths(this.properties.getTemplateLoaderPath());
        factory.setPreferFileSystemAccess(this.properties.isPreferFileSystemAccess());
        factory.setDefaultEncoding(this.properties.getCharsetName());
        Properties settings = new Properties();
        settings.put("recognize_standard_file_extensions", "true");
        settings.putAll(this.properties.getSettings());
        factory.setFreemarkerSettings(settings);
    }
}
