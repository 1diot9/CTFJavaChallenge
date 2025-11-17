package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.Annotations;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotationCollector.class */
public abstract class AnnotationCollector {
    protected static final Annotations NO_ANNOTATIONS = new NoAnnotations();
    protected final Object _data;

    public abstract Annotations asAnnotations();

    public abstract AnnotationMap asAnnotationMap();

    public abstract boolean isPresent(Annotation annotation);

    public abstract AnnotationCollector addOrOverride(Annotation annotation);

    protected AnnotationCollector(Object d) {
        this._data = d;
    }

    public static Annotations emptyAnnotations() {
        return NO_ANNOTATIONS;
    }

    public static AnnotationCollector emptyCollector() {
        return EmptyCollector.instance;
    }

    public static AnnotationCollector emptyCollector(Object data) {
        return new EmptyCollector(data);
    }

    public Object getData() {
        return this._data;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotationCollector$EmptyCollector.class */
    static class EmptyCollector extends AnnotationCollector {
        public static final EmptyCollector instance = new EmptyCollector(null);

        EmptyCollector(Object data) {
            super(data);
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public Annotations asAnnotations() {
            return NO_ANNOTATIONS;
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public AnnotationMap asAnnotationMap() {
            return new AnnotationMap();
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public boolean isPresent(Annotation ann) {
            return false;
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public AnnotationCollector addOrOverride(Annotation ann) {
            return new OneCollector(this._data, ann.annotationType(), ann);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotationCollector$OneCollector.class */
    static class OneCollector extends AnnotationCollector {
        private Class<?> _type;
        private Annotation _value;

        public OneCollector(Object data, Class<?> type, Annotation value) {
            super(data);
            this._type = type;
            this._value = value;
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public Annotations asAnnotations() {
            return new OneAnnotation(this._type, this._value);
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public AnnotationMap asAnnotationMap() {
            return AnnotationMap.of(this._type, this._value);
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public boolean isPresent(Annotation ann) {
            return ann.annotationType() == this._type;
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public AnnotationCollector addOrOverride(Annotation ann) {
            Class<?> type = ann.annotationType();
            if (this._type == type) {
                this._value = ann;
                return this;
            }
            return new NCollector(this._data, this._type, this._value, type, ann);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotationCollector$NCollector.class */
    static class NCollector extends AnnotationCollector {
        protected final HashMap<Class<?>, Annotation> _annotations;

        public NCollector(Object data, Class<?> type1, Annotation value1, Class<?> type2, Annotation value2) {
            super(data);
            this._annotations = new HashMap<>();
            this._annotations.put(type1, value1);
            this._annotations.put(type2, value2);
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public Annotations asAnnotations() {
            if (this._annotations.size() == 2) {
                Iterator<Map.Entry<Class<?>, Annotation>> it = this._annotations.entrySet().iterator();
                Map.Entry<Class<?>, Annotation> en1 = it.next();
                Map.Entry<Class<?>, Annotation> en2 = it.next();
                return new TwoAnnotations(en1.getKey(), en1.getValue(), en2.getKey(), en2.getValue());
            }
            return new AnnotationMap(this._annotations);
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public AnnotationMap asAnnotationMap() {
            AnnotationMap result = new AnnotationMap();
            for (Annotation ann : this._annotations.values()) {
                result.add(ann);
            }
            return result;
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public boolean isPresent(Annotation ann) {
            return this._annotations.containsKey(ann.annotationType());
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotationCollector
        public AnnotationCollector addOrOverride(Annotation ann) {
            this._annotations.put(ann.annotationType(), ann);
            return this;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotationCollector$NoAnnotations.class */
    public static class NoAnnotations implements Annotations, Serializable {
        private static final long serialVersionUID = 1;

        NoAnnotations() {
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public <A extends Annotation> A get(Class<A> cls) {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public boolean has(Class<?> cls) {
            return false;
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
            return false;
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public int size() {
            return 0;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotationCollector$OneAnnotation.class */
    public static class OneAnnotation implements Annotations, Serializable {
        private static final long serialVersionUID = 1;
        private final Class<?> _type;
        private final Annotation _value;

        public OneAnnotation(Class<?> type, Annotation value) {
            this._type = type;
            this._value = value;
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public <A extends Annotation> A get(Class<A> cls) {
            if (this._type == cls) {
                return (A) this._value;
            }
            return null;
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public boolean has(Class<?> cls) {
            return this._type == cls;
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
            for (Class<? extends Annotation> cls : annoClasses) {
                if (cls == this._type) {
                    return true;
                }
            }
            return false;
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public int size() {
            return 1;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotationCollector$TwoAnnotations.class */
    public static class TwoAnnotations implements Annotations, Serializable {
        private static final long serialVersionUID = 1;
        private final Class<?> _type1;
        private final Class<?> _type2;
        private final Annotation _value1;
        private final Annotation _value2;

        public TwoAnnotations(Class<?> type1, Annotation value1, Class<?> type2, Annotation value2) {
            this._type1 = type1;
            this._value1 = value1;
            this._type2 = type2;
            this._value2 = value2;
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public <A extends Annotation> A get(Class<A> cls) {
            if (this._type1 == cls) {
                return (A) this._value1;
            }
            if (this._type2 == cls) {
                return (A) this._value2;
            }
            return null;
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public boolean has(Class<?> cls) {
            return this._type1 == cls || this._type2 == cls;
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
            for (Class<? extends Annotation> cls : annoClasses) {
                if (cls == this._type1 || cls == this._type2) {
                    return true;
                }
            }
            return false;
        }

        @Override // com.fasterxml.jackson.databind.util.Annotations
        public int size() {
            return 2;
        }
    }
}
