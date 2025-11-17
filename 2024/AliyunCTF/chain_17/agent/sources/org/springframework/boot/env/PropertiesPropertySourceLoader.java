package org.springframework.boot.env;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.boot.env.OriginTrackedPropertiesLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/env/PropertiesPropertySourceLoader.class */
public class PropertiesPropertySourceLoader implements PropertySourceLoader {
    private static final String XML_FILE_EXTENSION = ".xml";

    @Override // org.springframework.boot.env.PropertySourceLoader
    public String[] getFileExtensions() {
        return new String[]{"properties", "xml"};
    }

    @Override // org.springframework.boot.env.PropertySourceLoader
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        List<Map<String, ?>> properties = loadProperties(resource);
        if (properties.isEmpty()) {
            return Collections.emptyList();
        }
        List<PropertySource<?>> propertySources = new ArrayList<>(properties.size());
        for (int i = 0; i < properties.size(); i++) {
            String documentNumber = properties.size() != 1 ? " (document #" + i + ")" : "";
            propertySources.add(new OriginTrackedMapPropertySource(name + documentNumber, Collections.unmodifiableMap(properties.get(i)), true));
        }
        return propertySources;
    }

    private List<Map<String, ?>> loadProperties(Resource resource) throws IOException {
        String filename = resource.getFilename();
        List<Map<String, ?>> result = new ArrayList<>();
        if (filename != null && filename.endsWith(".xml")) {
            result.add(PropertiesLoaderUtils.loadProperties(resource));
        } else {
            List<OriginTrackedPropertiesLoader.Document> documents = new OriginTrackedPropertiesLoader(resource).load();
            documents.forEach(document -> {
                result.add(document.asMap());
            });
        }
        return result;
    }
}
