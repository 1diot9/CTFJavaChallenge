package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/JavaType.class */
public abstract class JavaType extends ResolvedType implements Serializable, Type {
    private static final long serialVersionUID = 1;
    protected final Class<?> _class;
    protected final int _hash;
    protected final Object _valueHandler;
    protected final Object _typeHandler;
    protected final boolean _asStatic;

    public abstract JavaType withContentType(JavaType javaType);

    public abstract JavaType withStaticTyping();

    public abstract JavaType withTypeHandler(Object obj);

    public abstract JavaType withContentTypeHandler(Object obj);

    public abstract JavaType withValueHandler(Object obj);

    public abstract JavaType withContentValueHandler(Object obj);

    public abstract JavaType refine(Class<?> cls, TypeBindings typeBindings, JavaType javaType, JavaType[] javaTypeArr);

    @Deprecated
    protected abstract JavaType _narrow(Class<?> cls);

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public abstract boolean isContainerType();

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public abstract int containedTypeCount();

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public abstract JavaType containedType(int i);

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    @Deprecated
    public abstract String containedTypeName(int i);

    public abstract TypeBindings getBindings();

    public abstract JavaType findSuperType(Class<?> cls);

    public abstract JavaType getSuperClass();

    public abstract List<JavaType> getInterfaces();

    public abstract JavaType[] findTypeParameters(Class<?> cls);

    public abstract StringBuilder getGenericSignature(StringBuilder sb);

    public abstract StringBuilder getErasedSignature(StringBuilder sb);

    public abstract String toString();

    public abstract boolean equals(Object obj);

    /* JADX INFO: Access modifiers changed from: protected */
    public JavaType(Class<?> raw, int additionalHash, Object valueHandler, Object typeHandler, boolean asStatic) {
        this._class = raw;
        this._hash = (31 * additionalHash) + raw.hashCode();
        this._valueHandler = valueHandler;
        this._typeHandler = typeHandler;
        this._asStatic = asStatic;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JavaType(JavaType base) {
        this._class = base._class;
        this._hash = base._hash;
        this._valueHandler = base._valueHandler;
        this._typeHandler = base._typeHandler;
        this._asStatic = base._asStatic;
    }

    public JavaType withHandlersFrom(JavaType src) {
        JavaType type = this;
        Object h = src.getTypeHandler();
        if (h != this._typeHandler) {
            type = type.withTypeHandler(h);
        }
        Object h2 = src.getValueHandler();
        if (h2 != this._valueHandler) {
            type = type.withValueHandler(h2);
        }
        return type;
    }

    @Deprecated
    public JavaType forcedNarrowBy(Class<?> subclass) {
        if (subclass == this._class) {
            return this;
        }
        return _narrow(subclass);
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public final Class<?> getRawClass() {
        return this._class;
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public final boolean hasRawClass(Class<?> clz) {
        return this._class == clz;
    }

    public boolean hasContentType() {
        return true;
    }

    public final boolean isTypeOrSubTypeOf(Class<?> clz) {
        return this._class == clz || clz.isAssignableFrom(this._class);
    }

    public final boolean isTypeOrSuperTypeOf(Class<?> clz) {
        return this._class == clz || this._class.isAssignableFrom(clz);
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public boolean isAbstract() {
        return Modifier.isAbstract(this._class.getModifiers());
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public boolean isConcrete() {
        int mod = this._class.getModifiers();
        if ((mod & 1536) == 0) {
            return true;
        }
        return this._class.isPrimitive();
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public boolean isThrowable() {
        return Throwable.class.isAssignableFrom(this._class);
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public boolean isArrayType() {
        return false;
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public final boolean isEnumType() {
        return ClassUtil.isEnumType(this._class);
    }

    public final boolean isEnumImplType() {
        return ClassUtil.isEnumType(this._class) && this._class != Enum.class;
    }

    public final boolean isRecordType() {
        return ClassUtil.isRecordType(this._class);
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public final boolean isInterface() {
        return this._class.isInterface();
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public final boolean isPrimitive() {
        return this._class.isPrimitive();
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public final boolean isFinal() {
        return Modifier.isFinal(this._class.getModifiers());
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public boolean isCollectionLikeType() {
        return false;
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public boolean isMapLikeType() {
        return false;
    }

    public final boolean isJavaLangObject() {
        return this._class == Object.class;
    }

    public final boolean useStaticType() {
        return this._asStatic;
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public boolean hasGenericTypes() {
        return containedTypeCount() > 0;
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public JavaType getKeyType() {
        return null;
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public JavaType getContentType() {
        return null;
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public JavaType getReferencedType() {
        return null;
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    @Deprecated
    public Class<?> getParameterSource() {
        return null;
    }

    public JavaType containedTypeOrUnknown(int index) {
        JavaType t = containedType(index);
        return t == null ? TypeFactory.unknownType() : t;
    }

    public <T> T getValueHandler() {
        return (T) this._valueHandler;
    }

    public <T> T getTypeHandler() {
        return (T) this._typeHandler;
    }

    public Object getContentValueHandler() {
        return null;
    }

    public Object getContentTypeHandler() {
        return null;
    }

    public boolean hasValueHandler() {
        return this._valueHandler != null;
    }

    public boolean hasHandlers() {
        return (this._typeHandler == null && this._valueHandler == null) ? false : true;
    }

    public String getGenericSignature() {
        StringBuilder sb = new StringBuilder(40);
        getGenericSignature(sb);
        return sb.toString();
    }

    public String getErasedSignature() {
        StringBuilder sb = new StringBuilder(40);
        getErasedSignature(sb);
        return sb.toString();
    }

    public int hashCode() {
        return this._hash;
    }
}
