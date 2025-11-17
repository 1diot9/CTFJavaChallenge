package org.springframework.boot.autoconfigure.groovy.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.boot.autoconfigure.template.PathBasedTemplateAvailabilityProvider;
import org.springframework.boot.context.properties.bind.BindableRuntimeHintsRegistrar;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/groovy/template/GroovyTemplateAvailabilityProvider.class */
public class GroovyTemplateAvailabilityProvider extends PathBasedTemplateAvailabilityProvider {
    private static final String REQUIRED_CLASS_NAME = "groovy.text.TemplateEngine";

    public GroovyTemplateAvailabilityProvider() {
        super(REQUIRED_CLASS_NAME, GroovyTemplateAvailabilityProperties.class, "spring.groovy.template");
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/groovy/template/GroovyTemplateAvailabilityProvider$GroovyTemplateAvailabilityProperties.class */
    protected static final class GroovyTemplateAvailabilityProperties extends PathBasedTemplateAvailabilityProvider.TemplateAvailabilityProperties {
        private List<String> resourceLoaderPath;

        GroovyTemplateAvailabilityProperties() {
            super("", GroovyTemplateProperties.DEFAULT_SUFFIX);
            this.resourceLoaderPath = new ArrayList(Arrays.asList("classpath:/templates/"));
        }

        @Override // org.springframework.boot.autoconfigure.template.PathBasedTemplateAvailabilityProvider.TemplateAvailabilityProperties
        protected List<String> getLoaderPath() {
            return this.resourceLoaderPath;
        }

        public List<String> getResourceLoaderPath() {
            return this.resourceLoaderPath;
        }

        public void setResourceLoaderPath(List<String> resourceLoaderPath) {
            this.resourceLoaderPath = resourceLoaderPath;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/groovy/template/GroovyTemplateAvailabilityProvider$GroovyTemplateAvailabilityRuntimeHints.class */
    static class GroovyTemplateAvailabilityRuntimeHints extends BindableRuntimeHintsRegistrar {
        GroovyTemplateAvailabilityRuntimeHints() {
            super((Class<?>[]) new Class[]{GroovyTemplateAvailabilityProperties.class});
        }

        @Override // org.springframework.boot.context.properties.bind.BindableRuntimeHintsRegistrar, org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            if (ClassUtils.isPresent(GroovyTemplateAvailabilityProvider.REQUIRED_CLASS_NAME, classLoader)) {
                super.registerHints(hints, classLoader);
            }
        }
    }
}
