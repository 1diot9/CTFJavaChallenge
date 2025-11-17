package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/ClassEditor.class */
public class ClassEditor extends PropertyEditorSupport {

    @Nullable
    private final ClassLoader classLoader;

    public ClassEditor() {
        this(null);
    }

    public ClassEditor(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            setValue(ClassUtils.resolveClassName(text.trim(), this.classLoader));
        } else {
            setValue(null);
        }
    }

    public String getAsText() {
        Class<?> clazz = (Class) getValue();
        if (clazz != null) {
            return ClassUtils.getQualifiedName(clazz);
        }
        return "";
    }
}
