package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/Annotated.class */
public abstract class Annotated {
    public abstract <A extends Annotation> A getAnnotation(Class<A> cls);

    public abstract boolean hasAnnotation(Class<?> cls);

    public abstract boolean hasOneOf(Class<? extends Annotation>[] clsArr);

    public abstract AnnotatedElement getAnnotated();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract int getModifiers();

    public abstract String getName();

    public abstract JavaType getType();

    public abstract Class<?> getRawType();

    @Deprecated
    public abstract Iterable<Annotation> annotations();

    public abstract boolean equals(Object obj);

    public abstract int hashCode();

    public abstract String toString();

    public boolean isPublic() {
        return Modifier.isPublic(getModifiers());
    }
}
