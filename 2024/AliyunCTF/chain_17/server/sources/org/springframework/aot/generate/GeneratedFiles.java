package org.springframework.aot.generate;

import java.util.Objects;
import org.springframework.core.io.InputStreamSource;
import org.springframework.javapoet.JavaFile;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.function.ThrowingConsumer;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/GeneratedFiles.class */
public interface GeneratedFiles {

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/generate/GeneratedFiles$Kind.class */
    public enum Kind {
        SOURCE,
        RESOURCE,
        CLASS
    }

    void addFile(Kind kind, String path, InputStreamSource content);

    default void addSourceFile(JavaFile javaFile) {
        validatePackage(javaFile.packageName, javaFile.typeSpec.name);
        String className = javaFile.packageName + "." + javaFile.typeSpec.name;
        Objects.requireNonNull(javaFile);
        addSourceFile(className, javaFile::writeTo);
    }

    default void addSourceFile(String className, CharSequence content) {
        addSourceFile(className, appendable -> {
            appendable.append(content);
        });
    }

    default void addSourceFile(String className, ThrowingConsumer<Appendable> content) {
        addFile(Kind.SOURCE, getClassNamePath(className), content);
    }

    default void addSourceFile(String className, InputStreamSource content) {
        addFile(Kind.SOURCE, getClassNamePath(className), content);
    }

    default void addResourceFile(String path, CharSequence content) {
        addResourceFile(path, appendable -> {
            appendable.append(content);
        });
    }

    default void addResourceFile(String path, ThrowingConsumer<Appendable> content) {
        addFile(Kind.RESOURCE, path, content);
    }

    default void addResourceFile(String path, InputStreamSource content) {
        addFile(Kind.RESOURCE, path, content);
    }

    default void addClassFile(String path, InputStreamSource content) {
        addFile(Kind.CLASS, path, content);
    }

    default void addFile(Kind kind, String path, CharSequence content) {
        addFile(kind, path, appendable -> {
            appendable.append(content);
        });
    }

    default void addFile(Kind kind, String path, ThrowingConsumer<Appendable> content) {
        Assert.notNull(content, "'content' must not be null");
        addFile(kind, path, new AppendableConsumerInputStreamSource(content));
    }

    private static String getClassNamePath(String className) {
        Assert.hasLength(className, "'className' must not be empty");
        validatePackage(ClassUtils.getPackageName(className), className);
        Assert.isTrue(isJavaIdentifier(className), "'className' must be a valid identifier, got '" + className + "'");
        return ClassUtils.convertClassNameToResourcePath(className) + ".java";
    }

    private static void validatePackage(String packageName, String className) {
        if (!StringUtils.hasLength(packageName)) {
            throw new IllegalArgumentException("Could not add '" + className + "', processing classes in the default package is not supported. Did you forget to add a package statement?");
        }
    }

    private static boolean isJavaIdentifier(String className) {
        char[] chars = className.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i == 0 && !Character.isJavaIdentifierStart(chars[i])) {
                return false;
            }
            if (i > 0 && chars[i] != '.' && !Character.isJavaIdentifierPart(chars[i])) {
                return false;
            }
        }
        return true;
    }
}
