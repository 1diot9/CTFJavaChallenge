package org.springframework.boot.web.servlet.context;

import org.springframework.aot.AotDetector;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/context/ServletWebServerApplicationContextFactory.class */
class ServletWebServerApplicationContextFactory implements ApplicationContextFactory {
    ServletWebServerApplicationContextFactory() {
    }

    @Override // org.springframework.boot.ApplicationContextFactory
    public Class<? extends ConfigurableEnvironment> getEnvironmentType(WebApplicationType webApplicationType) {
        if (webApplicationType != WebApplicationType.SERVLET) {
            return null;
        }
        return ApplicationServletEnvironment.class;
    }

    @Override // org.springframework.boot.ApplicationContextFactory
    public ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
        if (webApplicationType != WebApplicationType.SERVLET) {
            return null;
        }
        return new ApplicationServletEnvironment();
    }

    @Override // org.springframework.boot.ApplicationContextFactory
    public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
        if (webApplicationType != WebApplicationType.SERVLET) {
            return null;
        }
        return createContext();
    }

    private ConfigurableApplicationContext createContext() {
        if (!AotDetector.useGeneratedArtifacts()) {
            return new AnnotationConfigServletWebServerApplicationContext();
        }
        return new ServletWebServerApplicationContext();
    }
}
