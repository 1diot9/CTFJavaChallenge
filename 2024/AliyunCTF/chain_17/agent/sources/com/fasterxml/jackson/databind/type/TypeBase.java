package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/type/TypeBase.class */
public abstract class TypeBase extends JavaType implements JsonSerializable {
    private static final long serialVersionUID = 1;
    private static final TypeBindings NO_BINDINGS = TypeBindings.emptyBindings();
    private static final JavaType[] NO_TYPES = new JavaType[0];
    protected final JavaType _superClass;
    protected final JavaType[] _superInterfaces;
    protected final TypeBindings _bindings;
    volatile transient String _canonicalName;

    @Override // com.fasterxml.jackson.databind.JavaType
    public abstract StringBuilder getGenericSignature(StringBuilder sb);

    @Override // com.fasterxml.jackson.databind.JavaType
    public abstract StringBuilder getErasedSignature(StringBuilder sb);

    /* JADX INFO: Access modifiers changed from: protected */
    public TypeBase(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInts, int hash, Object valueHandler, Object typeHandler, boolean asStatic) {
        super(raw, hash, valueHandler, typeHandler, asStatic);
        this._bindings = bindings == null ? NO_BINDINGS : bindings;
        this._superClass = superClass;
        this._superInterfaces = superInts;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TypeBase(TypeBase base) {
        super(base);
        this._superClass = base._superClass;
        this._superInterfaces = base._superInterfaces;
        this._bindings = base._bindings;
    }

    @Override // com.fasterxml.jackson.core.type.ResolvedType
    public String toCanonical() {
        String str = this._canonicalName;
        if (str == null) {
            str = buildCanonicalName();
        }
        return str;
    }

    protected String buildCanonicalName() {
        return this._class.getName();
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public TypeBindings getBindings() {
        return this._bindings;
    }

    @Override // com.fasterxml.jackson.databind.JavaType, com.fasterxml.jackson.core.type.ResolvedType
    public int containedTypeCount() {
        return this._bindings.size();
    }

    @Override // com.fasterxml.jackson.databind.JavaType, com.fasterxml.jackson.core.type.ResolvedType
    public JavaType containedType(int index) {
        return this._bindings.getBoundType(index);
    }

    @Override // com.fasterxml.jackson.databind.JavaType, com.fasterxml.jackson.core.type.ResolvedType
    @Deprecated
    public String containedTypeName(int index) {
        return this._bindings.getBoundName(index);
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public JavaType getSuperClass() {
        return this._superClass;
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public List<JavaType> getInterfaces() {
        if (this._superInterfaces == null) {
            return Collections.emptyList();
        }
        switch (this._superInterfaces.length) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(this._superInterfaces[0]);
            default:
                return Arrays.asList(this._superInterfaces);
        }
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public final JavaType findSuperType(Class<?> rawTarget) {
        JavaType type;
        if (rawTarget == this._class) {
            return this;
        }
        if (rawTarget.isInterface() && this._superInterfaces != null) {
            int count = this._superInterfaces.length;
            for (int i = 0; i < count; i++) {
                JavaType type2 = this._superInterfaces[i].findSuperType(rawTarget);
                if (type2 != null) {
                    return type2;
                }
            }
        }
        if (this._superClass != null && (type = this._superClass.findSuperType(rawTarget)) != null) {
            return type;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.JavaType
    public JavaType[] findTypeParameters(Class<?> expType) {
        JavaType match = findSuperType(expType);
        if (match == null) {
            return NO_TYPES;
        }
        return match.getBindings().typeParameterArray();
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializable
    public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = new WritableTypeId(this, JsonToken.VALUE_STRING);
        typeSer.writeTypePrefix(g, typeIdDef);
        serialize(g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializable
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(toCanonical());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static StringBuilder _classSignature(Class<?> cls, StringBuilder sb, boolean trailingSemicolon) {
        if (cls.isPrimitive()) {
            if (cls == Boolean.TYPE) {
                sb.append('Z');
            } else if (cls == Byte.TYPE) {
                sb.append('B');
            } else if (cls == Short.TYPE) {
                sb.append('S');
            } else if (cls == Character.TYPE) {
                sb.append('C');
            } else if (cls == Integer.TYPE) {
                sb.append('I');
            } else if (cls == Long.TYPE) {
                sb.append('J');
            } else if (cls == Float.TYPE) {
                sb.append('F');
            } else if (cls == Double.TYPE) {
                sb.append('D');
            } else if (cls == Void.TYPE) {
                sb.append('V');
            } else {
                throw new IllegalStateException("Unrecognized primitive type: " + cls.getName());
            }
        } else {
            sb.append('L');
            String name = cls.getName();
            int len = name.length();
            for (int i = 0; i < len; i++) {
                char c = name.charAt(i);
                if (c == '.') {
                    c = '/';
                }
                sb.append(c);
            }
            if (trailingSemicolon) {
                sb.append(';');
            }
        }
        return sb;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static JavaType _bogusSuperClass(Class<?> cls) {
        Class<?> parent = cls.getSuperclass();
        if (parent == null) {
            return null;
        }
        return TypeFactory.unknownType();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean _hasNTypeParameters(int count) {
        TypeVariable<?>[] params = this._class.getTypeParameters();
        return params.length == count;
    }
}
