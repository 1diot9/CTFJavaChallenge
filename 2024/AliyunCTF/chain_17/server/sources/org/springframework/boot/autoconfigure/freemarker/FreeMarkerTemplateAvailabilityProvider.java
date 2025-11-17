package org.springframework.boot.autoconfigure.freemarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.boot.autoconfigure.template.PathBasedTemplateAvailabilityProvider;
import org.springframework.boot.context.properties.bind.BindableRuntimeHintsRegistrar;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerTemplateAvailabilityProvider.class */
public class FreeMarkerTemplateAvailabilityProvider extends PathBasedTemplateAvailabilityProvider {
    private static final String REQUIRED_CLASS_NAME = "freemarker.template.Configuration";

    public FreeMarkerTemplateAvailabilityProvider() {
        super(REQUIRED_CLASS_NAME, FreeMarkerTemplateAvailabilityProperties.class, "spring.freemarker");
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerTemplateAvailabilityProvider$FreeMarkerTemplateAvailabilityProperties.class */
    protected static final class FreeMarkerTemplateAvailabilityProperties extends PathBasedTemplateAvailabilityProvider.TemplateAvailabilityProperties {
        private List<String> templateLoaderPath;

        FreeMarkerTemplateAvailabilityProperties() {
            super("", FreeMarkerProperties.DEFAULT_SUFFIX);
            this.templateLoaderPath = new ArrayList(Arrays.asList("classpath:/templates/"));
        }

        @Override // org.springframework.boot.autoconfigure.template.PathBasedTemplateAvailabilityProvider.TemplateAvailabilityProperties
        protected List<String> getLoaderPath() {
            return this.templateLoaderPath;
        }

        public List<String> getTemplateLoaderPath() {
            return this.templateLoaderPath;
        }

        public void setTemplateLoaderPath(List<String> templateLoaderPath) {
            this.templateLoaderPath = templateLoaderPath;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerTemplateAvailabilityProvider$FreeMarkerTemplateAvailabilityRuntimeHints.class */
    static class FreeMarkerTemplateAvailabilityRuntimeHints extends BindableRuntimeHintsRegistrar {
        FreeMarkerTemplateAvailabilityRuntimeHints() {
            super((Class<?>[]) new Class[]{FreeMarkerTemplateAvailabilityProperties.class});
        }

        @Override // org.springframework.boot.context.properties.bind.BindableRuntimeHintsRegistrar, org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            if (ClassUtils.isPresent(FreeMarkerTemplateAvailabilityProvider.REQUIRED_CLASS_NAME, classLoader)) {
                super.registerHints(hints, classLoader);
            }
        }
    }
}
