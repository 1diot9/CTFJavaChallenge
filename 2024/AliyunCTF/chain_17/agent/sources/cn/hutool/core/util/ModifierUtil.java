package cn.hutool.core.util;

import cn.hutool.core.exceptions.UtilException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/ModifierUtil.class */
public class ModifierUtil {

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/ModifierUtil$ModifierType.class */
    public enum ModifierType {
        PUBLIC(1),
        PRIVATE(2),
        PROTECTED(4),
        STATIC(8),
        FINAL(16),
        SYNCHRONIZED(32),
        VOLATILE(64),
        TRANSIENT(128),
        NATIVE(256),
        ABSTRACT(1024),
        STRICT(2048);

        private final int value;

        ModifierType(int modifier) {
            this.value = modifier;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static boolean hasModifier(Class<?> clazz, ModifierType... modifierTypes) {
        return (null == clazz || ArrayUtil.isEmpty((Object[]) modifierTypes) || 0 == (clazz.getModifiers() & modifiersToInt(modifierTypes))) ? false : true;
    }

    public static boolean hasModifier(Constructor<?> constructor, ModifierType... modifierTypes) {
        return (null == constructor || ArrayUtil.isEmpty((Object[]) modifierTypes) || 0 == (constructor.getModifiers() & modifiersToInt(modifierTypes))) ? false : true;
    }

    public static boolean hasModifier(Method method, ModifierType... modifierTypes) {
        return (null == method || ArrayUtil.isEmpty((Object[]) modifierTypes) || 0 == (method.getModifiers() & modifiersToInt(modifierTypes))) ? false : true;
    }

    public static boolean hasModifier(Field field, ModifierType... modifierTypes) {
        return (null == field || ArrayUtil.isEmpty((Object[]) modifierTypes) || 0 == (field.getModifiers() & modifiersToInt(modifierTypes))) ? false : true;
    }

    public static boolean isPublic(Field field) {
        return hasModifier(field, ModifierType.PUBLIC);
    }

    public static boolean isPublic(Method method) {
        return hasModifier(method, ModifierType.PUBLIC);
    }

    public static boolean isPublic(Class<?> clazz) {
        return hasModifier(clazz, ModifierType.PUBLIC);
    }

    public static boolean isPublic(Constructor<?> constructor) {
        return hasModifier(constructor, ModifierType.PUBLIC);
    }

    public static boolean isStatic(Field field) {
        return hasModifier(field, ModifierType.STATIC);
    }

    public static boolean isStatic(Method method) {
        return hasModifier(method, ModifierType.STATIC);
    }

    public static boolean isStatic(Class<?> clazz) {
        return hasModifier(clazz, ModifierType.STATIC);
    }

    public static boolean isSynthetic(Field field) {
        return field.isSynthetic();
    }

    public static boolean isSynthetic(Method method) {
        return method.isSynthetic();
    }

    public static boolean isSynthetic(Class<?> clazz) {
        return clazz.isSynthetic();
    }

    public static boolean isAbstract(Method method) {
        return hasModifier(method, ModifierType.ABSTRACT);
    }

    public static void removeFinalModify(Field field) {
        if (field != null && hasModifier(field, ModifierType.FINAL)) {
            if (false == field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & (-17));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new UtilException(e, "IllegalAccess for {}.{}", field.getDeclaringClass(), field.getName());
            }
        }
    }

    private static int modifiersToInt(ModifierType... modifierTypes) {
        int modifier = modifierTypes[0].getValue();
        for (int i = 1; i < modifierTypes.length; i++) {
            modifier |= modifierTypes[i].getValue();
        }
        return modifier;
    }
}
