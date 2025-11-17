package org.springframework.core.env;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/MapPropertySource.class */
public class MapPropertySource extends EnumerablePropertySource<Map<String, Object>> {
    public MapPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    @Override // org.springframework.core.env.PropertySource
    @Nullable
    public Object getProperty(String name) {
        return ((Map) this.source).get(name);
    }

    @Override // org.springframework.core.env.EnumerablePropertySource, org.springframework.core.env.PropertySource
    public boolean containsProperty(String name) {
        return ((Map) this.source).containsKey(name);
    }

    @Override // org.springframework.core.env.EnumerablePropertySource
    public String[] getPropertyNames() {
        return StringUtils.toStringArray(((Map) this.source).keySet());
    }
}
