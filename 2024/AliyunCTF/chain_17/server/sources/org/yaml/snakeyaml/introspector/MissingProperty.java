package org.yaml.snakeyaml.introspector;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/introspector/MissingProperty.class */
public class MissingProperty extends Property {
    public MissingProperty(String name) {
        super(name, Object.class);
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public Class<?>[] getActualTypeArguments() {
        return new Class[0];
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public void set(Object object, Object value) throws Exception {
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public Object get(Object object) {
        return object;
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public List<Annotation> getAnnotations() {
        return Collections.emptyList();
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }
}
