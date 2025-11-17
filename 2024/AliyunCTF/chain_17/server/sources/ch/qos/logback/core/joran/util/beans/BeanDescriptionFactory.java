package ch.qos.logback.core.joran.util.beans;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/util/beans/BeanDescriptionFactory.class */
public class BeanDescriptionFactory extends ContextAwareBase {
    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanDescriptionFactory(Context context) {
        setContext(context);
    }

    public BeanDescription create(Class<?> clazz) {
        Map<String, Method> propertyNameToGetter = new HashMap<>();
        Map<String, Method> propertyNameToSetter = new HashMap<>();
        Map<String, Method> propertyNameToAdder = new HashMap<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (!method.isBridge()) {
                if (BeanUtil.isGetter(method)) {
                    String propertyName = BeanUtil.getPropertyName(method);
                    Method oldGetter = propertyNameToGetter.put(propertyName, method);
                    if (oldGetter != null) {
                        if (oldGetter.getName().startsWith(BeanUtil.PREFIX_GETTER_IS)) {
                            propertyNameToGetter.put(propertyName, oldGetter);
                        }
                        String message = String.format("Class '%s' contains multiple getters for the same property '%s'.", clazz.getCanonicalName(), propertyName);
                        addWarn(message);
                    }
                } else if (BeanUtil.isSetter(method)) {
                    String propertyName2 = BeanUtil.getPropertyName(method);
                    Method oldSetter = propertyNameToSetter.put(propertyName2, method);
                    if (oldSetter != null) {
                        String message2 = String.format("Class '%s' contains multiple setters for the same property '%s'.", clazz.getCanonicalName(), propertyName2);
                        addWarn(message2);
                    }
                } else if (BeanUtil.isAdder(method)) {
                    String propertyName3 = BeanUtil.getPropertyName(method);
                    Method oldAdder = propertyNameToAdder.put(propertyName3, method);
                    if (oldAdder != null) {
                        String message3 = String.format("Class '%s' contains multiple adders for the same property '%s'.", clazz.getCanonicalName(), propertyName3);
                        addWarn(message3);
                    }
                }
            }
        }
        return new BeanDescription(clazz, propertyNameToGetter, propertyNameToSetter, propertyNameToAdder);
    }
}
