package org.yaml.snakeyaml.introspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.util.ArrayUtils;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/introspector/FieldProperty.class */
public class FieldProperty extends GenericProperty {
    private final Field field;

    public FieldProperty(Field field) {
        super(field.getName(), field.getType(), field.getGenericType());
        this.field = field;
        field.setAccessible(true);
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public void set(Object object, Object value) throws Exception {
        this.field.set(object, value);
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public Object get(Object object) {
        try {
            return this.field.get(object);
        } catch (Exception e) {
            throw new YAMLException("Unable to access field " + this.field.getName() + " on object " + object + " : " + e);
        }
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public List<Annotation> getAnnotations() {
        return ArrayUtils.toUnmodifiableList(this.field.getAnnotations());
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public <A extends Annotation> A getAnnotation(Class<A> cls) {
        return (A) this.field.getAnnotation(cls);
    }
}
