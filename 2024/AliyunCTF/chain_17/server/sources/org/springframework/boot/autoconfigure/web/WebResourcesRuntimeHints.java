package org.springframework.boot.autoconfigure.web;

import java.util.List;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/WebResourcesRuntimeHints.class */
public class WebResourcesRuntimeHints implements RuntimeHintsRegistrar {
    private static final List<String> DEFAULT_LOCATIONS = List.of("META-INF/resources/", "resources/", "static/", "public/");

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ClassLoader classLoaderToUse = classLoader != null ? classLoader : getClass().getClassLoader();
        String[] locations = (String[]) DEFAULT_LOCATIONS.stream().filter(candidate -> {
            return classLoaderToUse.getResource(candidate) != null;
        }).map(location -> {
            return location + "*";
        }).toArray(x$0 -> {
            return new String[x$0];
        });
        if (locations.length > 0) {
            hints.resources().registerPattern(hint -> {
                hint.includes(locations);
            });
        }
    }
}
