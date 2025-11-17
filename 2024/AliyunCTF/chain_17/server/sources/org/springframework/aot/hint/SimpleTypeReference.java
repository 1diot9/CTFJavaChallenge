package org.springframework.aot.hint;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.lang.model.SourceVersion;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/SimpleTypeReference.class */
public final class SimpleTypeReference extends AbstractTypeReference {
    private static final List<String> PRIMITIVE_NAMES = List.of("boolean", "byte", "short", "int", "long", "char", "float", "double", "void");

    @Nullable
    private String canonicalName;

    SimpleTypeReference(String packageName, String simpleName, @Nullable TypeReference enclosingType) {
        super(packageName, simpleName, enclosingType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SimpleTypeReference of(String className) {
        Assert.notNull(className, "'className' must not be null");
        if (!isValidClassName(className)) {
            throw new IllegalStateException("Invalid class name '" + className + "'");
        }
        if (!className.contains(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX)) {
            return createTypeReference(className);
        }
        String[] elements = className.split("(?<!\\$)\\$(?!\\$)");
        SimpleTypeReference typeReference = createTypeReference(elements[0]);
        for (int i = 1; i < elements.length; i++) {
            typeReference = new SimpleTypeReference(typeReference.getPackageName(), elements[i], typeReference);
        }
        return typeReference;
    }

    private static boolean isValidClassName(String className) {
        for (String s : className.split("\\.", -1)) {
            String candidate = s.replace(PropertyAccessor.PROPERTY_KEY_PREFIX, "").replace("]", "");
            if (!SourceVersion.isIdentifier(candidate)) {
                return false;
            }
        }
        return true;
    }

    private static SimpleTypeReference createTypeReference(String className) {
        int i = className.lastIndexOf(46);
        if (i != -1) {
            return new SimpleTypeReference(className.substring(0, i), className.substring(i + 1), null);
        }
        String packageName = isPrimitive(className) ? "java.lang" : "";
        return new SimpleTypeReference(packageName, className, null);
    }

    @Override // org.springframework.aot.hint.TypeReference
    public String getCanonicalName() {
        if (this.canonicalName == null) {
            StringBuilder names = new StringBuilder();
            buildName(this, names);
            this.canonicalName = addPackageIfNecessary(names.toString());
        }
        return this.canonicalName;
    }

    @Override // org.springframework.aot.hint.AbstractTypeReference
    protected boolean isPrimitive() {
        return isPrimitive(getSimpleName());
    }

    private static boolean isPrimitive(String name) {
        Stream<String> stream = PRIMITIVE_NAMES.stream();
        Objects.requireNonNull(name);
        return stream.anyMatch(name::startsWith);
    }

    private static void buildName(@Nullable TypeReference type, StringBuilder sb) {
        if (type == null) {
            return;
        }
        String typeName = type.getEnclosingType() != null ? "." + type.getSimpleName() : type.getSimpleName();
        sb.insert(0, typeName);
        buildName(type.getEnclosingType(), sb);
    }
}
