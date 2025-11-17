package org.springframework.boot.autoconfigure.template;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/template/TemplateAvailabilityProvider.class */
public interface TemplateAvailabilityProvider {
    boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader, ResourceLoader resourceLoader);
}
