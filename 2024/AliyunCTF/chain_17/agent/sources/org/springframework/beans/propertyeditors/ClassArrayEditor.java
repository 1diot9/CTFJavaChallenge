package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.StringJoiner;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/ClassArrayEditor.class */
public class ClassArrayEditor extends PropertyEditorSupport {

    @Nullable
    private final ClassLoader classLoader;

    public ClassArrayEditor() {
        this(null);
    }

    public ClassArrayEditor(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            String[] classNames = StringUtils.commaDelimitedListToStringArray(text);
            Class<?>[] classes = new Class[classNames.length];
            for (int i = 0; i < classNames.length; i++) {
                String className = classNames[i].trim();
                classes[i] = ClassUtils.resolveClassName(className, this.classLoader);
            }
            setValue(classes);
            return;
        }
        setValue(null);
    }

    public String getAsText() {
        Class<?>[] classes = (Class[]) getValue();
        if (ObjectUtils.isEmpty((Object[]) classes)) {
            return "";
        }
        StringJoiner sj = new StringJoiner(",");
        for (Class<?> klass : classes) {
            sj.add(ClassUtils.getQualifiedName(klass));
        }
        return sj.toString();
    }
}
