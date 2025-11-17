package org.springframework.boot.context.properties.bind;

import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/BindContext.class */
public interface BindContext {
    Binder getBinder();

    int getDepth();

    Iterable<ConfigurationPropertySource> getSources();

    ConfigurationProperty getConfigurationProperty();
}
