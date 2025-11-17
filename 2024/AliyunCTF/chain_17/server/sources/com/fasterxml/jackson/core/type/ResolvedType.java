package com.fasterxml.jackson.core.type;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/type/ResolvedType.class */
public abstract class ResolvedType {
    public abstract Class<?> getRawClass();

    public abstract boolean hasRawClass(Class<?> cls);

    public abstract boolean isAbstract();

    public abstract boolean isConcrete();

    public abstract boolean isThrowable();

    public abstract boolean isArrayType();

    public abstract boolean isEnumType();

    public abstract boolean isInterface();

    public abstract boolean isPrimitive();

    public abstract boolean isFinal();

    public abstract boolean isContainerType();

    public abstract boolean isCollectionLikeType();

    public abstract boolean isMapLikeType();

    public abstract boolean hasGenericTypes();

    public abstract ResolvedType getKeyType();

    public abstract ResolvedType getContentType();

    public abstract ResolvedType getReferencedType();

    public abstract int containedTypeCount();

    public abstract ResolvedType containedType(int i);

    public abstract String containedTypeName(int i);

    public abstract String toCanonical();

    public boolean isReferenceType() {
        return getReferencedType() != null;
    }

    @Deprecated
    public Class<?> getParameterSource() {
        return null;
    }
}
