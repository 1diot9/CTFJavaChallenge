package com.fasterxml.jackson.core.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/type/TypeReference.class */
public abstract class TypeReference<T> implements Comparable<TypeReference<T>> {
    protected final Type _type;

    /* JADX INFO: Access modifiers changed from: protected */
    public TypeReference() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }
        this._type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return this._type;
    }

    @Override // java.lang.Comparable
    public int compareTo(TypeReference<T> o) {
        return 0;
    }
}
