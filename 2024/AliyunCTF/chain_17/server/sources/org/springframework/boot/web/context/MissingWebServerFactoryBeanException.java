package org.springframework.boot.web.context;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.server.WebServerFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/context/MissingWebServerFactoryBeanException.class */
public class MissingWebServerFactoryBeanException extends NoSuchBeanDefinitionException {
    private final WebApplicationType webApplicationType;

    public MissingWebServerFactoryBeanException(Class<? extends WebServerApplicationContext> webServerApplicationContextClass, Class<? extends WebServerFactory> webServerFactoryClass, WebApplicationType webApplicationType) {
        super(webServerFactoryClass, String.format("Unable to start %s due to missing %s bean", webServerApplicationContextClass.getSimpleName(), webServerFactoryClass.getSimpleName()));
        this.webApplicationType = webApplicationType;
    }

    public WebApplicationType getWebApplicationType() {
        return this.webApplicationType;
    }
}
