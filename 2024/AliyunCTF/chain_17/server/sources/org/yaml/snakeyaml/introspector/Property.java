package org.yaml.snakeyaml.introspector;

import java.lang.annotation.Annotation;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/introspector/Property.class */
public abstract class Property implements Comparable<Property> {
    private final String name;
    private final Class<?> type;

    public abstract Class<?>[] getActualTypeArguments();

    public abstract void set(Object obj, Object obj2) throws Exception;

    public abstract Object get(Object obj);

    public abstract List<Annotation> getAnnotations();

    public abstract <A extends Annotation> A getAnnotation(Class<A> cls);

    public Property(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public Class<?> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return getName() + " of " + getType();
    }

    @Override // java.lang.Comparable
    public int compareTo(Property o) {
        return getName().compareTo(o.getName());
    }

    public boolean isWritable() {
        return true;
    }

    public boolean isReadable() {
        return true;
    }

    public int hashCode() {
        return getName().hashCode() + getType().hashCode();
    }

    public boolean equals(Object other) {
        if (other instanceof Property) {
            Property p = (Property) other;
            return getName().equals(p.getName()) && getType().equals(p.getType());
        }
        return false;
    }
}
