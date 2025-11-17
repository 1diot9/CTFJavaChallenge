package org.springframework.boot.env;

import java.io.IOException;
import java.util.List;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/PropertySourceLoader.class */
public interface PropertySourceLoader {
    String[] getFileExtensions();

    List<PropertySource<?>> load(String name, Resource resource) throws IOException;
}
