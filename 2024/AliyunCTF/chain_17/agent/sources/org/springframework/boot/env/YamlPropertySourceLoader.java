package org.springframework.boot.env;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/YamlPropertySourceLoader.class */
public class YamlPropertySourceLoader implements PropertySourceLoader {
    @Override // org.springframework.boot.env.PropertySourceLoader
    public String[] getFileExtensions() {
        return new String[]{"yml", "yaml"};
    }

    @Override // org.springframework.boot.env.PropertySourceLoader
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        if (!ClassUtils.isPresent("org.yaml.snakeyaml.Yaml", getClass().getClassLoader())) {
            throw new IllegalStateException("Attempted to load " + name + " but snakeyaml was not found on the classpath");
        }
        List<Map<String, Object>> loaded = new OriginTrackedYamlLoader(resource).load();
        if (loaded.isEmpty()) {
            return Collections.emptyList();
        }
        List<PropertySource<?>> propertySources = new ArrayList<>(loaded.size());
        for (int i = 0; i < loaded.size(); i++) {
            String documentNumber = loaded.size() != 1 ? " (document #" + i + ")" : "";
            propertySources.add(new OriginTrackedMapPropertySource(name + documentNumber, Collections.unmodifiableMap(loaded.get(i)), true));
        }
        return propertySources;
    }
}
