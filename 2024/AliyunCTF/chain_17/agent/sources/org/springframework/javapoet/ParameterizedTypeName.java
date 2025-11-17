package org.springframework.javapoet;

import cn.hutool.core.text.CharSequenceUtil;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/ParameterizedTypeName.class */
public final class ParameterizedTypeName extends TypeName {
    private final ParameterizedTypeName enclosingType;
    public final ClassName rawType;
    public final List<TypeName> typeArguments;

    @Override // org.springframework.javapoet.TypeName
    public /* bridge */ /* synthetic */ TypeName annotated(List list) {
        return annotated((List<AnnotationSpec>) list);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParameterizedTypeName(ParameterizedTypeName enclosingType, ClassName rawType, List<TypeName> typeArguments) {
        this(enclosingType, rawType, typeArguments, new ArrayList());
    }

    private ParameterizedTypeName(ParameterizedTypeName enclosingType, ClassName rawType, List<TypeName> typeArguments, List<AnnotationSpec> annotations) {
        super(annotations);
        this.rawType = ((ClassName) Util.checkNotNull(rawType, "rawType == null", new Object[0])).annotated(annotations);
        this.enclosingType = enclosingType;
        this.typeArguments = Util.immutableList(typeArguments);
        Util.checkArgument((this.typeArguments.isEmpty() && enclosingType == null) ? false : true, "no type arguments: %s", rawType);
        Iterator<TypeName> it = this.typeArguments.iterator();
        while (it.hasNext()) {
            TypeName typeArgument = it.next();
            Util.checkArgument((typeArgument.isPrimitive() || typeArgument == VOID) ? false : true, "invalid type parameter: %s", typeArgument);
        }
    }

    @Override // org.springframework.javapoet.TypeName
    public ParameterizedTypeName annotated(List<AnnotationSpec> annotations) {
        return new ParameterizedTypeName(this.enclosingType, this.rawType, this.typeArguments, concatAnnotations(annotations));
    }

    @Override // org.springframework.javapoet.TypeName
    public TypeName withoutAnnotations() {
        return new ParameterizedTypeName(this.enclosingType, this.rawType.withoutAnnotations(), this.typeArguments, new ArrayList());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.springframework.javapoet.TypeName
    public CodeWriter emit(CodeWriter out) throws IOException {
        if (this.enclosingType != null) {
            this.enclosingType.emit(out);
            out.emit(".");
            if (isAnnotated()) {
                out.emit(CharSequenceUtil.SPACE);
                emitAnnotations(out);
            }
            out.emit(this.rawType.simpleName());
        } else {
            this.rawType.emit(out);
        }
        if (!this.typeArguments.isEmpty()) {
            out.emitAndIndent("<");
            boolean firstParameter = true;
            for (TypeName parameter : this.typeArguments) {
                if (!firstParameter) {
                    out.emitAndIndent(", ");
                }
                parameter.emit(out);
                firstParameter = false;
            }
            out.emitAndIndent(">");
        }
        return out;
    }

    public ParameterizedTypeName nestedClass(String name) {
        Util.checkNotNull(name, "name == null", new Object[0]);
        return new ParameterizedTypeName(this, this.rawType.nestedClass(name), new ArrayList(), new ArrayList());
    }

    public ParameterizedTypeName nestedClass(String name, List<TypeName> typeArguments) {
        Util.checkNotNull(name, "name == null", new Object[0]);
        return new ParameterizedTypeName(this, this.rawType.nestedClass(name), typeArguments, new ArrayList());
    }

    public static ParameterizedTypeName get(ClassName rawType, TypeName... typeArguments) {
        return new ParameterizedTypeName(null, rawType, Arrays.asList(typeArguments));
    }

    public static ParameterizedTypeName get(Class<?> rawType, Type... typeArguments) {
        return new ParameterizedTypeName(null, ClassName.get(rawType), list(typeArguments));
    }

    public static ParameterizedTypeName get(ParameterizedType type) {
        return get(type, (Map<Type, TypeVariableName>) new LinkedHashMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ParameterizedTypeName get(ParameterizedType type, Map<Type, TypeVariableName> map) {
        ClassName rawType = ClassName.get((Class<?>) type.getRawType());
        ParameterizedType ownerType = (!(type.getOwnerType() instanceof ParameterizedType) || Modifier.isStatic(((Class) type.getRawType()).getModifiers())) ? null : (ParameterizedType) type.getOwnerType();
        List<TypeName> typeArguments = TypeName.list(type.getActualTypeArguments(), map);
        if (ownerType != null) {
            return get(ownerType, map).nestedClass(rawType.simpleName(), typeArguments);
        }
        return new ParameterizedTypeName(null, rawType, typeArguments);
    }
}
