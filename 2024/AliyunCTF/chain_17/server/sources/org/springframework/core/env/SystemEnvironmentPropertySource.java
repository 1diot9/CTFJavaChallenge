package org.springframework.core.env;

import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/SystemEnvironmentPropertySource.class */
public class SystemEnvironmentPropertySource extends MapPropertySource {
    public SystemEnvironmentPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    @Override // org.springframework.core.env.MapPropertySource, org.springframework.core.env.EnumerablePropertySource, org.springframework.core.env.PropertySource
    public boolean containsProperty(String name) {
        return getProperty(name) != null;
    }

    @Override // org.springframework.core.env.MapPropertySource, org.springframework.core.env.PropertySource
    @Nullable
    public Object getProperty(String name) {
        String actualName = resolvePropertyName(name);
        if (this.logger.isDebugEnabled() && !name.equals(actualName)) {
            this.logger.debug("PropertySource '" + getName() + "' does not contain property '" + name + "', but found equivalent '" + actualName + "'");
        }
        return super.getProperty(actualName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String resolvePropertyName(String name) {
        String resolvedName;
        Assert.notNull(name, "Property name must not be null");
        String resolvedName2 = checkPropertyName(name);
        if (resolvedName2 != null) {
            return resolvedName2;
        }
        String uppercasedName = name.toUpperCase();
        if (!name.equals(uppercasedName) && (resolvedName = checkPropertyName(uppercasedName)) != null) {
            return resolvedName;
        }
        return name;
    }

    @Nullable
    private String checkPropertyName(String name) {
        if (((Map) this.source).containsKey(name)) {
            return name;
        }
        String noDotName = name.replace('.', '_');
        if (!name.equals(noDotName) && ((Map) this.source).containsKey(noDotName)) {
            return noDotName;
        }
        String noHyphenName = name.replace('-', '_');
        if (!name.equals(noHyphenName) && ((Map) this.source).containsKey(noHyphenName)) {
            return noHyphenName;
        }
        String noDotNoHyphenName = noDotName.replace('-', '_');
        if (!noDotName.equals(noDotNoHyphenName) && ((Map) this.source).containsKey(noDotNoHyphenName)) {
            return noDotNoHyphenName;
        }
        return null;
    }
}
