package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/type/IdentityEqualityType.class */
public abstract class IdentityEqualityType extends TypeBase {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    public IdentityEqualityType(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInts, int hash, Object valueHandler, Object typeHandler, boolean asStatic) {
        super(raw, bindings, superClass, superInts, hash, valueHandler, typeHandler, asStatic);
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public final boolean equals(Object o) {
        return o == this;
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public final int hashCode() {
        return System.identityHashCode(this);
    }
}
