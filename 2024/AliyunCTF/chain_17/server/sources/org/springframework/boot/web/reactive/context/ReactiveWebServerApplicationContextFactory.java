package org.springframework.boot.web.reactive.context;

import org.springframework.aot.AotDetector;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/context/ReactiveWebServerApplicationContextFactory.class */
class ReactiveWebServerApplicationContextFactory implements ApplicationContextFactory {
    ReactiveWebServerApplicationContextFactory() {
    }

    @Override // org.springframework.boot.ApplicationContextFactory
    public Class<? extends ConfigurableEnvironment> getEnvironmentType(WebApplicationType webApplicationType) {
        if (webApplicationType != WebApplicationType.REACTIVE) {
            return null;
        }
        return ApplicationReactiveWebEnvironment.class;
    }

    @Override // org.springframework.boot.ApplicationContextFactory
    public ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
        if (webApplicationType != WebApplicationType.REACTIVE) {
            return null;
        }
        return new ApplicationReactiveWebEnvironment();
    }

    @Override // org.springframework.boot.ApplicationContextFactory
    public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
        if (webApplicationType != WebApplicationType.REACTIVE) {
            return null;
        }
        return createContext();
    }

    private ConfigurableApplicationContext createContext() {
        if (!AotDetector.useGeneratedArtifacts()) {
            return new AnnotationConfigReactiveWebServerApplicationContext();
        }
        return new ReactiveWebServerApplicationContext();
    }
}
