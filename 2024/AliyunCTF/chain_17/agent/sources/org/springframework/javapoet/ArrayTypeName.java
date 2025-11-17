package org.springframework.javapoet;

import cn.hutool.core.text.CharSequenceUtil;
import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/ArrayTypeName.class */
public final class ArrayTypeName extends TypeName {
    public final TypeName componentType;

    @Override // org.springframework.javapoet.TypeName
    public /* bridge */ /* synthetic */ TypeName annotated(List list) {
        return annotated((List<AnnotationSpec>) list);
    }

    private ArrayTypeName(TypeName componentType) {
        this(componentType, new ArrayList());
    }

    private ArrayTypeName(TypeName componentType, List<AnnotationSpec> annotations) {
        super(annotations);
        this.componentType = (TypeName) Util.checkNotNull(componentType, "rawType == null", new Object[0]);
    }

    @Override // org.springframework.javapoet.TypeName
    public ArrayTypeName annotated(List<AnnotationSpec> annotations) {
        return new ArrayTypeName(this.componentType, concatAnnotations(annotations));
    }

    @Override // org.springframework.javapoet.TypeName
    public TypeName withoutAnnotations() {
        return new ArrayTypeName(this.componentType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.springframework.javapoet.TypeName
    public CodeWriter emit(CodeWriter out) throws IOException {
        return emit(out, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CodeWriter emit(CodeWriter out, boolean varargs) throws IOException {
        emitLeafType(out);
        return emitBrackets(out, varargs);
    }

    private CodeWriter emitLeafType(CodeWriter out) throws IOException {
        if (TypeName.asArray(this.componentType) != null) {
            return TypeName.asArray(this.componentType).emitLeafType(out);
        }
        return this.componentType.emit(out);
    }

    private CodeWriter emitBrackets(CodeWriter out, boolean varargs) throws IOException {
        if (isAnnotated()) {
            out.emit(CharSequenceUtil.SPACE);
            emitAnnotations(out);
        }
        if (TypeName.asArray(this.componentType) == null) {
            return out.emit(varargs ? "..." : ClassUtils.ARRAY_SUFFIX);
        }
        out.emit(ClassUtils.ARRAY_SUFFIX);
        return TypeName.asArray(this.componentType).emitBrackets(out, varargs);
    }

    public static ArrayTypeName of(TypeName componentType) {
        return new ArrayTypeName(componentType);
    }

    public static ArrayTypeName of(Type componentType) {
        return of(TypeName.get(componentType));
    }

    public static ArrayTypeName get(ArrayType mirror) {
        return get(mirror, new LinkedHashMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ArrayTypeName get(ArrayType mirror, Map<TypeParameterElement, TypeVariableName> typeVariables) {
        return new ArrayTypeName(get(mirror.getComponentType(), typeVariables));
    }

    public static ArrayTypeName get(GenericArrayType type) {
        return get(type, (Map<Type, TypeVariableName>) new LinkedHashMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ArrayTypeName get(GenericArrayType type, Map<Type, TypeVariableName> map) {
        return of(get(type.getGenericComponentType(), map));
    }
}
