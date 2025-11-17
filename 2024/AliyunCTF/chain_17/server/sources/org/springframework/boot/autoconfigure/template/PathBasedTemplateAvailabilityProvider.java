package org.springframework.boot.autoconfigure.template;

import java.util.List;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/template/PathBasedTemplateAvailabilityProvider.class */
public abstract class PathBasedTemplateAvailabilityProvider implements TemplateAvailabilityProvider {
    private final String className;
    private final Class<TemplateAvailabilityProperties> propertiesClass;
    private final String propertyPrefix;

    /* JADX WARN: Multi-variable type inference failed */
    public PathBasedTemplateAvailabilityProvider(String className, Class<? extends TemplateAvailabilityProperties> propertiesClass, String propertyPrefix) {
        this.className = className;
        this.propertiesClass = propertiesClass;
        this.propertyPrefix = propertyPrefix;
    }

    @Override // org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider
    public boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader, ResourceLoader resourceLoader) {
        if (ClassUtils.isPresent(this.className, classLoader)) {
            Binder binder = Binder.get(environment);
            TemplateAvailabilityProperties properties = (TemplateAvailabilityProperties) binder.bindOrCreate(this.propertyPrefix, this.propertiesClass);
            return isTemplateAvailable(view, resourceLoader, properties);
        }
        return false;
    }

    private boolean isTemplateAvailable(String view, ResourceLoader resourceLoader, TemplateAvailabilityProperties properties) {
        String location = properties.getPrefix() + view + properties.getSuffix();
        for (String path : properties.getLoaderPath()) {
            if (resourceLoader.getResource(path + location).exists()) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/template/PathBasedTemplateAvailabilityProvider$TemplateAvailabilityProperties.class */
    public static abstract class TemplateAvailabilityProperties {
        private String prefix;
        private String suffix;

        protected abstract List<String> getLoaderPath();

        /* JADX INFO: Access modifiers changed from: protected */
        public TemplateAvailabilityProperties(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSuffix() {
            return this.suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }
}
