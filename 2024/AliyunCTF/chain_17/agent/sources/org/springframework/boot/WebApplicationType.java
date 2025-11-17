package org.springframework.boot;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/WebApplicationType.class */
public enum WebApplicationType {
    NONE,
    SERVLET,
    REACTIVE;

    private static final String[] SERVLET_INDICATOR_CLASSES = {"jakarta.servlet.Servlet", "org.springframework.web.context.ConfigurableWebApplicationContext"};
    private static final String WEBMVC_INDICATOR_CLASS = "org.springframework.web.servlet.DispatcherServlet";
    private static final String WEBFLUX_INDICATOR_CLASS = "org.springframework.web.reactive.DispatcherHandler";
    private static final String JERSEY_INDICATOR_CLASS = "org.glassfish.jersey.servlet.ServletContainer";

    /* JADX INFO: Access modifiers changed from: package-private */
    public static WebApplicationType deduceFromClasspath() {
        if (ClassUtils.isPresent(WEBFLUX_INDICATOR_CLASS, null) && !ClassUtils.isPresent(WEBMVC_INDICATOR_CLASS, null) && !ClassUtils.isPresent(JERSEY_INDICATOR_CLASS, null)) {
            return REACTIVE;
        }
        for (String className : SERVLET_INDICATOR_CLASSES) {
            if (!ClassUtils.isPresent(className, null)) {
                return NONE;
            }
        }
        return SERVLET;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/WebApplicationType$WebApplicationTypeRuntimeHints.class */
    static class WebApplicationTypeRuntimeHints implements RuntimeHintsRegistrar {
        WebApplicationTypeRuntimeHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            for (String servletIndicatorClass : WebApplicationType.SERVLET_INDICATOR_CLASSES) {
                registerTypeIfPresent(servletIndicatorClass, classLoader, hints);
            }
            registerTypeIfPresent(WebApplicationType.JERSEY_INDICATOR_CLASS, classLoader, hints);
            registerTypeIfPresent(WebApplicationType.WEBFLUX_INDICATOR_CLASS, classLoader, hints);
            registerTypeIfPresent(WebApplicationType.WEBMVC_INDICATOR_CLASS, classLoader, hints);
        }

        private void registerTypeIfPresent(String typeName, ClassLoader classLoader, RuntimeHints hints) {
            if (ClassUtils.isPresent(typeName, classLoader)) {
                hints.reflection().registerType(TypeReference.of(typeName), new MemberCategory[0]);
            }
        }
    }
}
