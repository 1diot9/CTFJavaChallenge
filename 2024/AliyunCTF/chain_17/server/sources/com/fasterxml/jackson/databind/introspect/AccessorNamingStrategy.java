package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AccessorNamingStrategy.class */
public abstract class AccessorNamingStrategy {

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AccessorNamingStrategy$Provider.class */
    public static abstract class Provider implements Serializable {
        private static final long serialVersionUID = 1;

        public abstract AccessorNamingStrategy forPOJO(MapperConfig<?> mapperConfig, AnnotatedClass annotatedClass);

        public abstract AccessorNamingStrategy forBuilder(MapperConfig<?> mapperConfig, AnnotatedClass annotatedClass, BeanDescription beanDescription);

        public abstract AccessorNamingStrategy forRecord(MapperConfig<?> mapperConfig, AnnotatedClass annotatedClass);
    }

    public abstract String findNameForIsGetter(AnnotatedMethod annotatedMethod, String str);

    public abstract String findNameForRegularGetter(AnnotatedMethod annotatedMethod, String str);

    public abstract String findNameForMutator(AnnotatedMethod annotatedMethod, String str);

    public abstract String modifyFieldName(AnnotatedField annotatedField, String str);

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AccessorNamingStrategy$Base.class */
    public static class Base extends AccessorNamingStrategy implements Serializable {
        private static final long serialVersionUID = 1;

        @Override // com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy
        public String findNameForIsGetter(AnnotatedMethod method, String name) {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy
        public String findNameForRegularGetter(AnnotatedMethod method, String name) {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy
        public String findNameForMutator(AnnotatedMethod method, String name) {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy
        public String modifyFieldName(AnnotatedField field, String name) {
            return name;
        }
    }
}
