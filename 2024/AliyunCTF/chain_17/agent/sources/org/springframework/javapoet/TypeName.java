package org.springframework.javapoet;

import cn.hutool.core.text.CharSequenceUtil;
import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleTypeVisitor8;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/TypeName.class */
public class TypeName {
    public static final TypeName VOID = new TypeName("void");
    public static final TypeName BOOLEAN = new TypeName("boolean");
    public static final TypeName BYTE = new TypeName("byte");
    public static final TypeName SHORT = new TypeName("short");
    public static final TypeName INT = new TypeName("int");
    public static final TypeName LONG = new TypeName("long");
    public static final TypeName CHAR = new TypeName("char");
    public static final TypeName FLOAT = new TypeName("float");
    public static final TypeName DOUBLE = new TypeName("double");
    public static final ClassName OBJECT = ClassName.get("java.lang", "Object", new String[0]);
    private static final ClassName BOXED_VOID = ClassName.get("java.lang", "Void", new String[0]);
    private static final ClassName BOXED_BOOLEAN = ClassName.get("java.lang", "Boolean", new String[0]);
    private static final ClassName BOXED_BYTE = ClassName.get("java.lang", "Byte", new String[0]);
    private static final ClassName BOXED_SHORT = ClassName.get("java.lang", "Short", new String[0]);
    private static final ClassName BOXED_INT = ClassName.get("java.lang", "Integer", new String[0]);
    private static final ClassName BOXED_LONG = ClassName.get("java.lang", "Long", new String[0]);
    private static final ClassName BOXED_CHAR = ClassName.get("java.lang", "Character", new String[0]);
    private static final ClassName BOXED_FLOAT = ClassName.get("java.lang", "Float", new String[0]);
    private static final ClassName BOXED_DOUBLE = ClassName.get("java.lang", "Double", new String[0]);
    private final String keyword;
    public final List<AnnotationSpec> annotations;
    private String cachedString;

    private TypeName(String keyword) {
        this(keyword, new ArrayList());
    }

    private TypeName(String keyword, List<AnnotationSpec> annotations) {
        this.keyword = keyword;
        this.annotations = Util.immutableList(annotations);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TypeName(List<AnnotationSpec> annotations) {
        this(null, annotations);
    }

    public final TypeName annotated(AnnotationSpec... annotations) {
        return annotated(Arrays.asList(annotations));
    }

    public TypeName annotated(List<AnnotationSpec> annotations) {
        Util.checkNotNull(annotations, "annotations == null", new Object[0]);
        return new TypeName(this.keyword, concatAnnotations(annotations));
    }

    public TypeName withoutAnnotations() {
        return new TypeName(this.keyword);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final List<AnnotationSpec> concatAnnotations(List<AnnotationSpec> annotations) {
        List<AnnotationSpec> allAnnotations = new ArrayList<>(this.annotations);
        allAnnotations.addAll(annotations);
        return allAnnotations;
    }

    public boolean isAnnotated() {
        return !this.annotations.isEmpty();
    }

    public boolean isPrimitive() {
        return (this.keyword == null || this == VOID) ? false : true;
    }

    public boolean isBoxedPrimitive() {
        return equals(BOXED_BOOLEAN) || equals(BOXED_BYTE) || equals(BOXED_SHORT) || equals(BOXED_INT) || equals(BOXED_LONG) || equals(BOXED_CHAR) || equals(BOXED_FLOAT) || equals(BOXED_DOUBLE);
    }

    public TypeName box() {
        if (this.keyword == null) {
            return this;
        }
        if (this == VOID) {
            return BOXED_VOID;
        }
        if (this == BOOLEAN) {
            return BOXED_BOOLEAN;
        }
        if (this == BYTE) {
            return BOXED_BYTE;
        }
        if (this == SHORT) {
            return BOXED_SHORT;
        }
        if (this == INT) {
            return BOXED_INT;
        }
        if (this == LONG) {
            return BOXED_LONG;
        }
        if (this == CHAR) {
            return BOXED_CHAR;
        }
        if (this == FLOAT) {
            return BOXED_FLOAT;
        }
        if (this == DOUBLE) {
            return BOXED_DOUBLE;
        }
        throw new AssertionError(this.keyword);
    }

    public TypeName unbox() {
        if (this.keyword != null) {
            return this;
        }
        if (equals(BOXED_VOID)) {
            return VOID;
        }
        if (equals(BOXED_BOOLEAN)) {
            return BOOLEAN;
        }
        if (equals(BOXED_BYTE)) {
            return BYTE;
        }
        if (equals(BOXED_SHORT)) {
            return SHORT;
        }
        if (equals(BOXED_INT)) {
            return INT;
        }
        if (equals(BOXED_LONG)) {
            return LONG;
        }
        if (equals(BOXED_CHAR)) {
            return CHAR;
        }
        if (equals(BOXED_FLOAT)) {
            return FLOAT;
        }
        if (equals(BOXED_DOUBLE)) {
            return DOUBLE;
        }
        throw new UnsupportedOperationException("cannot unbox " + this);
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && getClass() == o.getClass()) {
            return toString().equals(o.toString());
        }
        return false;
    }

    public final int hashCode() {
        return toString().hashCode();
    }

    public final String toString() {
        String result = this.cachedString;
        if (result == null) {
            try {
                StringBuilder resultBuilder = new StringBuilder();
                CodeWriter codeWriter = new CodeWriter(resultBuilder);
                emit(codeWriter);
                result = resultBuilder.toString();
                this.cachedString = result;
            } catch (IOException e) {
                throw new AssertionError();
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CodeWriter emit(CodeWriter out) throws IOException {
        if (this.keyword == null) {
            throw new AssertionError();
        }
        if (isAnnotated()) {
            out.emit("");
            emitAnnotations(out);
        }
        return out.emitAndIndent(this.keyword);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CodeWriter emitAnnotations(CodeWriter out) throws IOException {
        for (AnnotationSpec annotation : this.annotations) {
            annotation.emit(out, true);
            out.emit(CharSequenceUtil.SPACE);
        }
        return out;
    }

    public static TypeName get(TypeMirror mirror) {
        return get(mirror, new LinkedHashMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TypeName get(TypeMirror mirror, final Map<TypeParameterElement, TypeVariableName> typeVariables) {
        return (TypeName) mirror.accept(new SimpleTypeVisitor8<TypeName, Void>() { // from class: org.springframework.javapoet.TypeName.1
            public TypeName visitPrimitive(PrimitiveType t, Void p) {
                switch (AnonymousClass2.$SwitchMap$javax$lang$model$type$TypeKind[t.getKind().ordinal()]) {
                    case 1:
                        return TypeName.BOOLEAN;
                    case 2:
                        return TypeName.BYTE;
                    case 3:
                        return TypeName.SHORT;
                    case 4:
                        return TypeName.INT;
                    case 5:
                        return TypeName.LONG;
                    case 6:
                        return TypeName.CHAR;
                    case 7:
                        return TypeName.FLOAT;
                    case 8:
                        return TypeName.DOUBLE;
                    default:
                        throw new AssertionError();
                }
            }

            public TypeName visitDeclared(DeclaredType t, Void p) {
                TypeName typeName;
                ClassName rawType = ClassName.get(t.asElement());
                TypeMirror enclosingType = t.getEnclosingType();
                if (enclosingType.getKind() != TypeKind.NONE && !t.asElement().getModifiers().contains(Modifier.STATIC)) {
                    typeName = (TypeName) enclosingType.accept(this, (Object) null);
                } else {
                    typeName = null;
                }
                TypeName enclosing = typeName;
                if (t.getTypeArguments().isEmpty() && !(enclosing instanceof ParameterizedTypeName)) {
                    return rawType;
                }
                List<TypeName> typeArgumentNames = new ArrayList<>();
                for (TypeMirror mirror2 : t.getTypeArguments()) {
                    typeArgumentNames.add(TypeName.get(mirror2, (Map<TypeParameterElement, TypeVariableName>) typeVariables));
                }
                if (enclosing instanceof ParameterizedTypeName) {
                    return ((ParameterizedTypeName) enclosing).nestedClass(rawType.simpleName(), typeArgumentNames);
                }
                return new ParameterizedTypeName(null, rawType, typeArgumentNames);
            }

            public TypeName visitError(ErrorType t, Void p) {
                return visitDeclared((DeclaredType) t, p);
            }

            public ArrayTypeName visitArray(ArrayType t, Void p) {
                return ArrayTypeName.get(t, (Map<TypeParameterElement, TypeVariableName>) typeVariables);
            }

            public TypeName visitTypeVariable(TypeVariable t, Void p) {
                return TypeVariableName.get(t, (Map<TypeParameterElement, TypeVariableName>) typeVariables);
            }

            public TypeName visitWildcard(WildcardType t, Void p) {
                return WildcardTypeName.get(t, (Map<TypeParameterElement, TypeVariableName>) typeVariables);
            }

            public TypeName visitNoType(NoType t, Void p) {
                return t.getKind() == TypeKind.VOID ? TypeName.VOID : (TypeName) super.visitUnknown(t, p);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            public TypeName defaultAction(TypeMirror e, Void p) {
                throw new IllegalArgumentException("Unexpected type mirror: " + e);
            }
        }, (Object) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.springframework.javapoet.TypeName$2, reason: invalid class name */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/TypeName$2.class */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$javax$lang$model$type$TypeKind = new int[TypeKind.values().length];

        static {
            try {
                $SwitchMap$javax$lang$model$type$TypeKind[TypeKind.BOOLEAN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$javax$lang$model$type$TypeKind[TypeKind.BYTE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$javax$lang$model$type$TypeKind[TypeKind.SHORT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$javax$lang$model$type$TypeKind[TypeKind.INT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$javax$lang$model$type$TypeKind[TypeKind.LONG.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$javax$lang$model$type$TypeKind[TypeKind.CHAR.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$javax$lang$model$type$TypeKind[TypeKind.FLOAT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$javax$lang$model$type$TypeKind[TypeKind.DOUBLE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    public static TypeName get(Type type) {
        return get(type, new LinkedHashMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TypeName get(Type type, Map<Type, TypeVariableName> map) {
        if (type instanceof Class) {
            Class<?> classType = (Class) type;
            return type == Void.TYPE ? VOID : type == Boolean.TYPE ? BOOLEAN : type == Byte.TYPE ? BYTE : type == Short.TYPE ? SHORT : type == Integer.TYPE ? INT : type == Long.TYPE ? LONG : type == Character.TYPE ? CHAR : type == Float.TYPE ? FLOAT : type == Double.TYPE ? DOUBLE : classType.isArray() ? ArrayTypeName.of(get(classType.getComponentType(), map)) : ClassName.get(classType);
        }
        if (type instanceof ParameterizedType) {
            return ParameterizedTypeName.get((ParameterizedType) type, map);
        }
        if (type instanceof java.lang.reflect.WildcardType) {
            return WildcardTypeName.get((java.lang.reflect.WildcardType) type, map);
        }
        if (type instanceof java.lang.reflect.TypeVariable) {
            return TypeVariableName.get((java.lang.reflect.TypeVariable<?>) type, map);
        }
        if (type instanceof GenericArrayType) {
            return ArrayTypeName.get((GenericArrayType) type, map);
        }
        throw new IllegalArgumentException("unexpected type: " + type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<TypeName> list(Type[] types) {
        return list(types, new LinkedHashMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<TypeName> list(Type[] types, Map<Type, TypeVariableName> map) {
        List<TypeName> result = new ArrayList<>(types.length);
        for (Type type : types) {
            result.add(get(type, map));
        }
        return result;
    }

    static TypeName arrayComponent(TypeName type) {
        if (type instanceof ArrayTypeName) {
            return ((ArrayTypeName) type).componentType;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ArrayTypeName asArray(TypeName type) {
        if (type instanceof ArrayTypeName) {
            return (ArrayTypeName) type;
        }
        return null;
    }
}
