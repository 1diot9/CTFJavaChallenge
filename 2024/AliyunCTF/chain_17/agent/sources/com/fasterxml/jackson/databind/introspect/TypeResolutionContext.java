package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/TypeResolutionContext.class */
public interface TypeResolutionContext {
    JavaType resolveType(Type type);

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/TypeResolutionContext$Basic.class */
    public static class Basic implements TypeResolutionContext {
        private final TypeFactory _typeFactory;
        private final TypeBindings _bindings;

        public Basic(TypeFactory tf, TypeBindings b) {
            this._typeFactory = tf;
            this._bindings = b;
        }

        @Override // com.fasterxml.jackson.databind.introspect.TypeResolutionContext
        public JavaType resolveType(Type type) {
            return this._typeFactory.resolveMemberType(type, this._bindings);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/TypeResolutionContext$Empty.class */
    public static class Empty implements TypeResolutionContext {
        private final TypeFactory _typeFactory;

        public Empty(TypeFactory tf) {
            this._typeFactory = tf;
        }

        @Override // com.fasterxml.jackson.databind.introspect.TypeResolutionContext
        public JavaType resolveType(Type type) {
            return this._typeFactory.constructType(type);
        }
    }
}
