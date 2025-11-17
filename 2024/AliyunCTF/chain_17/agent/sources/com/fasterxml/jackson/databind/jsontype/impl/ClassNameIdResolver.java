package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/impl/ClassNameIdResolver.class */
public class ClassNameIdResolver extends TypeIdResolverBase {
    private static final String JAVA_UTIL_PKG = "java.util.";
    protected final PolymorphicTypeValidator _subTypeValidator;

    @Deprecated
    protected ClassNameIdResolver(JavaType baseType, TypeFactory typeFactory) {
        this(baseType, typeFactory, LaissezFaireSubTypeValidator.instance);
    }

    public ClassNameIdResolver(JavaType baseType, TypeFactory typeFactory, PolymorphicTypeValidator ptv) {
        super(baseType, typeFactory);
        this._subTypeValidator = ptv;
    }

    public static ClassNameIdResolver construct(JavaType baseType, MapperConfig<?> config, PolymorphicTypeValidator ptv) {
        return new ClassNameIdResolver(baseType, config.getTypeFactory(), ptv);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeIdResolver
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CLASS;
    }

    public void registerSubtype(Class<?> type, String name) {
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeIdResolver
    public String idFromValue(Object value) {
        return _idFrom(value, value.getClass(), this._typeFactory);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.TypeIdResolver
    public String idFromValueAndType(Object value, Class<?> type) {
        return _idFrom(value, type, this._typeFactory);
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase, com.fasterxml.jackson.databind.jsontype.TypeIdResolver
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        return _typeFromId(id, context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JavaType _typeFromId(String id, DatabindContext ctxt) throws IOException {
        JavaType t = ctxt.resolveAndValidateSubType(this._baseType, id, this._subTypeValidator);
        if (t == null && (ctxt instanceof DeserializationContext)) {
            return ((DeserializationContext) ctxt).handleUnknownTypeId(this._baseType, id, this, "no such class found");
        }
        return t;
    }

    protected String _idFrom(Object value, Class<?> cls, TypeFactory typeFactory) {
        if (ClassUtil.isEnumType(cls) && !cls.isEnum()) {
            cls = cls.getSuperclass();
        }
        String str = cls.getName();
        if (str.startsWith(JAVA_UTIL_PKG)) {
            if (value instanceof EnumSet) {
                Class<?> enumClass = ClassUtil.findEnumType((EnumSet<?>) value);
                str = typeFactory.constructCollectionType(EnumSet.class, enumClass).toCanonical();
            } else if (value instanceof EnumMap) {
                Class<?> enumClass2 = ClassUtil.findEnumType((EnumMap<?, ?>) value);
                str = typeFactory.constructMapType(EnumMap.class, enumClass2, Object.class).toCanonical();
            }
        } else if (str.indexOf(36) >= 0) {
            Class<?> outer = ClassUtil.getOuterClass(cls);
            if (outer != null) {
                Class<?> staticType = this._baseType.getRawClass();
                if (ClassUtil.getOuterClass(staticType) == null) {
                    str = this._baseType.getRawClass().getName();
                }
            }
        }
        return str;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase, com.fasterxml.jackson.databind.jsontype.TypeIdResolver
    public String getDescForKnownTypeIds() {
        return "class name used as type id";
    }
}
