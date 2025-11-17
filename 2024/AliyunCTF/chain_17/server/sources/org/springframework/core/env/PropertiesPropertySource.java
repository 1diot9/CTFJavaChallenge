package org.springframework.core.env;

import java.util.Map;
import java.util.Properties;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/PropertiesPropertySource.class */
public class PropertiesPropertySource extends MapPropertySource {
    public PropertiesPropertySource(String name, Properties source) {
        super(name, source);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PropertiesPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    @Override // org.springframework.core.env.MapPropertySource, org.springframework.core.env.EnumerablePropertySource
    public String[] getPropertyNames() {
        String[] propertyNames;
        synchronized (((Map) this.source)) {
            propertyNames = super.getPropertyNames();
        }
        return propertyNames;
    }
}
