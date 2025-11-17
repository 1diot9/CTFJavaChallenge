package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.util.Collection;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/SubtypeResolver.class */
public abstract class SubtypeResolver {
    public abstract void registerSubtypes(NamedType... namedTypeArr);

    public abstract void registerSubtypes(Class<?>... clsArr);

    public abstract void registerSubtypes(Collection<Class<?>> collection);

    public SubtypeResolver copy() {
        return this;
    }

    public Collection<NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config, AnnotatedMember property, JavaType baseType) {
        return collectAndResolveSubtypes(property, config, config.getAnnotationIntrospector(), baseType);
    }

    public Collection<NamedType> collectAndResolveSubtypesByClass(MapperConfig<?> config, AnnotatedClass baseType) {
        return collectAndResolveSubtypes(baseType, config, config.getAnnotationIntrospector());
    }

    public Collection<NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config, AnnotatedMember property, JavaType baseType) {
        return collectAndResolveSubtypes(property, config, config.getAnnotationIntrospector(), baseType);
    }

    public Collection<NamedType> collectAndResolveSubtypesByTypeId(MapperConfig<?> config, AnnotatedClass baseType) {
        return collectAndResolveSubtypes(baseType, config, config.getAnnotationIntrospector());
    }

    @Deprecated
    public Collection<NamedType> collectAndResolveSubtypes(AnnotatedMember property, MapperConfig<?> config, AnnotationIntrospector ai, JavaType baseType) {
        return collectAndResolveSubtypesByClass(config, property, baseType);
    }

    @Deprecated
    public Collection<NamedType> collectAndResolveSubtypes(AnnotatedClass baseType, MapperConfig<?> config, AnnotationIntrospector ai) {
        return collectAndResolveSubtypesByClass(config, baseType);
    }
}
