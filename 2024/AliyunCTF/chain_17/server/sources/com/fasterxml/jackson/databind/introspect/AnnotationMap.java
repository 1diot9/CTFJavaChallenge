package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.Annotations;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotationMap.class */
public final class AnnotationMap implements Annotations {
    protected HashMap<Class<?>, Annotation> _annotations;

    public AnnotationMap() {
    }

    public static AnnotationMap of(Class<?> type, Annotation value) {
        HashMap<Class<?>, Annotation> ann = new HashMap<>(4);
        ann.put(type, value);
        return new AnnotationMap(ann);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationMap(HashMap<Class<?>, Annotation> a) {
        this._annotations = a;
    }

    @Override // com.fasterxml.jackson.databind.util.Annotations
    public <A extends Annotation> A get(Class<A> cls) {
        if (this._annotations == null) {
            return null;
        }
        return (A) this._annotations.get(cls);
    }

    @Override // com.fasterxml.jackson.databind.util.Annotations
    public boolean has(Class<?> cls) {
        if (this._annotations == null) {
            return false;
        }
        return this._annotations.containsKey(cls);
    }

    @Override // com.fasterxml.jackson.databind.util.Annotations
    public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
        if (this._annotations != null) {
            for (Class<? extends Annotation> cls : annoClasses) {
                if (this._annotations.containsKey(cls)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public Iterable<Annotation> annotations() {
        if (this._annotations == null || this._annotations.size() == 0) {
            return Collections.emptyList();
        }
        return this._annotations.values();
    }

    public static AnnotationMap merge(AnnotationMap primary, AnnotationMap secondary) {
        if (primary == null || primary._annotations == null || primary._annotations.isEmpty()) {
            return secondary;
        }
        if (secondary == null || secondary._annotations == null || secondary._annotations.isEmpty()) {
            return primary;
        }
        HashMap<Class<?>, Annotation> annotations = new HashMap<>();
        for (Annotation ann : secondary._annotations.values()) {
            annotations.put(ann.annotationType(), ann);
        }
        for (Annotation ann2 : primary._annotations.values()) {
            annotations.put(ann2.annotationType(), ann2);
        }
        return new AnnotationMap(annotations);
    }

    @Override // com.fasterxml.jackson.databind.util.Annotations
    public int size() {
        if (this._annotations == null) {
            return 0;
        }
        return this._annotations.size();
    }

    public boolean addIfNotPresent(Annotation ann) {
        if (this._annotations == null || !this._annotations.containsKey(ann.annotationType())) {
            _add(ann);
            return true;
        }
        return false;
    }

    public boolean add(Annotation ann) {
        return _add(ann);
    }

    public String toString() {
        if (this._annotations == null) {
            return "[null]";
        }
        return this._annotations.toString();
    }

    protected final boolean _add(Annotation ann) {
        if (this._annotations == null) {
            this._annotations = new HashMap<>();
        }
        Annotation previous = this._annotations.put(ann.annotationType(), ann);
        return previous == null || !previous.equals(ann);
    }
}
