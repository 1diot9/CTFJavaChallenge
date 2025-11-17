package org.springframework.cglib.core;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/ClassesKey.class */
public class ClassesKey {
    private static final Key FACTORY = (Key) KeyFactory.create(Key.class);

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/ClassesKey$Key.class */
    interface Key {
        Object newInstance(Object[] array);
    }

    private ClassesKey() {
    }

    public static Object create(Object[] array) {
        return FACTORY.newInstance(classNames(array));
    }

    private static String[] classNames(Object[] objects) {
        if (objects == null) {
            return null;
        }
        String[] classNames = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != null) {
                Class<?> aClass = object.getClass();
                classNames[i] = aClass == null ? null : aClass.getName();
            }
        }
        return classNames;
    }
}
