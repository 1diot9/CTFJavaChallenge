package org.yaml.snakeyaml.introspector;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.util.ArrayUtils;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/introspector/MethodProperty.class */
public class MethodProperty extends GenericProperty {
    private final PropertyDescriptor property;
    private final boolean readable;
    private final boolean writable;

    private static Type discoverGenericType(PropertyDescriptor property) {
        Method readMethod = property.getReadMethod();
        if (readMethod != null) {
            return readMethod.getGenericReturnType();
        }
        Method writeMethod = property.getWriteMethod();
        if (writeMethod != null) {
            Type[] paramTypes = writeMethod.getGenericParameterTypes();
            if (paramTypes.length > 0) {
                return paramTypes[0];
            }
            return null;
        }
        return null;
    }

    public MethodProperty(PropertyDescriptor property) {
        super(property.getName(), property.getPropertyType(), discoverGenericType(property));
        this.property = property;
        this.readable = property.getReadMethod() != null;
        this.writable = property.getWriteMethod() != null;
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public void set(Object object, Object value) throws Exception {
        if (!this.writable) {
            throw new YAMLException("No writable property '" + getName() + "' on class: " + object.getClass().getName());
        }
        this.property.getWriteMethod().invoke(object, value);
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public Object get(Object object) {
        try {
            this.property.getReadMethod().setAccessible(true);
            return this.property.getReadMethod().invoke(object, new Object[0]);
        } catch (Exception e) {
            throw new YAMLException("Unable to find getter for property '" + this.property.getName() + "' on object " + object + ":" + e);
        }
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public List<Annotation> getAnnotations() {
        List<Annotation> annotations;
        if (isReadable() && isWritable()) {
            annotations = ArrayUtils.toUnmodifiableCompositeList(this.property.getReadMethod().getAnnotations(), this.property.getWriteMethod().getAnnotations());
        } else if (isReadable()) {
            annotations = ArrayUtils.toUnmodifiableList(this.property.getReadMethod().getAnnotations());
        } else {
            annotations = ArrayUtils.toUnmodifiableList(this.property.getWriteMethod().getAnnotations());
        }
        return annotations;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v10, types: [java.lang.annotation.Annotation] */
    /* JADX WARN: Type inference failed for: r0v14, types: [java.lang.annotation.Annotation] */
    @Override // org.yaml.snakeyaml.introspector.Property
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        A annotation = null;
        if (isReadable()) {
            annotation = this.property.getReadMethod().getAnnotation(annotationType);
        }
        if (annotation == null && isWritable()) {
            annotation = this.property.getWriteMethod().getAnnotation(annotationType);
        }
        return annotation;
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public boolean isWritable() {
        return this.writable;
    }

    @Override // org.yaml.snakeyaml.introspector.Property
    public boolean isReadable() {
        return this.readable;
    }
}
