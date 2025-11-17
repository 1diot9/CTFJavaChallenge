package org.springframework.beans;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyDescriptorUtils.class */
abstract class PropertyDescriptorUtils {
    public static final PropertyDescriptor[] EMPTY_PROPERTY_DESCRIPTOR_ARRAY = new PropertyDescriptor[0];

    PropertyDescriptorUtils() {
    }

    public static Collection<? extends PropertyDescriptor> determineBasicProperties(Class<?> beanClass) throws IntrospectionException {
        boolean setter;
        int nameIndex;
        Map<String, BasicPropertyDescriptor> pdMap = new TreeMap<>();
        for (Method method : beanClass.getMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith("set") && method.getParameterCount() == 1) {
                setter = true;
                nameIndex = 3;
            } else if (methodName.startsWith(BeanUtil.PREFIX_GETTER_GET) && method.getParameterCount() == 0 && method.getReturnType() != Void.TYPE) {
                setter = false;
                nameIndex = 3;
            } else {
                if (methodName.startsWith(BeanUtil.PREFIX_GETTER_IS) && method.getParameterCount() == 0 && method.getReturnType() == Boolean.TYPE) {
                    setter = false;
                    nameIndex = 2;
                }
            }
            String propertyName = StringUtils.uncapitalizeAsProperty(methodName.substring(nameIndex));
            if (!propertyName.isEmpty()) {
                BasicPropertyDescriptor pd = pdMap.get(propertyName);
                if (pd != null) {
                    if (setter) {
                        Method writeMethod = pd.getWriteMethod();
                        if (writeMethod == null || writeMethod.getParameterTypes()[0].isAssignableFrom(method.getParameterTypes()[0])) {
                            pd.setWriteMethod(method);
                        } else {
                            pd.addWriteMethod(method);
                        }
                    } else {
                        Method readMethod = pd.getReadMethod();
                        if (readMethod == null || (readMethod.getReturnType() == method.getReturnType() && method.getName().startsWith(BeanUtil.PREFIX_GETTER_IS))) {
                            pd.setReadMethod(method);
                        }
                    }
                } else {
                    pdMap.put(propertyName, new BasicPropertyDescriptor(propertyName, !setter ? method : null, setter ? method : null));
                }
            }
        }
        return pdMap.values();
    }

    public static void copyNonMethodProperties(PropertyDescriptor source, PropertyDescriptor target) {
        target.setExpert(source.isExpert());
        target.setHidden(source.isHidden());
        target.setPreferred(source.isPreferred());
        target.setName(source.getName());
        target.setShortDescription(source.getShortDescription());
        target.setDisplayName(source.getDisplayName());
        Enumeration<String> keys = source.attributeNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            target.setValue(key, source.getValue(key));
        }
        target.setPropertyEditorClass(source.getPropertyEditorClass());
        target.setBound(source.isBound());
        target.setConstrained(source.isConstrained());
    }

    @Nullable
    public static Class<?> findPropertyType(@Nullable Method readMethod, @Nullable Method writeMethod) throws IntrospectionException {
        Class<?> propertyType = null;
        if (readMethod != null) {
            if (readMethod.getParameterCount() != 0) {
                throw new IntrospectionException("Bad read method arg count: " + readMethod);
            }
            propertyType = readMethod.getReturnType();
            if (propertyType == Void.TYPE) {
                throw new IntrospectionException("Read method returns void: " + readMethod);
            }
        }
        if (writeMethod != null) {
            Class<?>[] params = writeMethod.getParameterTypes();
            if (params.length != 1) {
                throw new IntrospectionException("Bad write method arg count: " + writeMethod);
            }
            if (propertyType != null) {
                if (propertyType.isAssignableFrom(params[0])) {
                    propertyType = params[0];
                } else if (!params[0].isAssignableFrom(propertyType)) {
                    throw new IntrospectionException("Type mismatch between read and write methods: " + readMethod + " - " + writeMethod);
                }
            } else {
                propertyType = params[0];
            }
        }
        return propertyType;
    }

    @Nullable
    public static Class<?> findIndexedPropertyType(String name, @Nullable Class<?> propertyType, @Nullable Method indexedReadMethod, @Nullable Method indexedWriteMethod) throws IntrospectionException {
        Class<?> indexedPropertyType = null;
        if (indexedReadMethod != null) {
            Class<?>[] params = indexedReadMethod.getParameterTypes();
            if (params.length != 1) {
                throw new IntrospectionException("Bad indexed read method arg count: " + indexedReadMethod);
            }
            if (params[0] != Integer.TYPE) {
                throw new IntrospectionException("Non int index to indexed read method: " + indexedReadMethod);
            }
            indexedPropertyType = indexedReadMethod.getReturnType();
            if (indexedPropertyType == Void.TYPE) {
                throw new IntrospectionException("Indexed read method returns void: " + indexedReadMethod);
            }
        }
        if (indexedWriteMethod != null) {
            Class<?>[] params2 = indexedWriteMethod.getParameterTypes();
            if (params2.length != 2) {
                throw new IntrospectionException("Bad indexed write method arg count: " + indexedWriteMethod);
            }
            if (params2[0] != Integer.TYPE) {
                throw new IntrospectionException("Non int index to indexed write method: " + indexedWriteMethod);
            }
            if (indexedPropertyType != null) {
                if (indexedPropertyType.isAssignableFrom(params2[1])) {
                    indexedPropertyType = params2[1];
                } else if (!params2[1].isAssignableFrom(indexedPropertyType)) {
                    throw new IntrospectionException("Type mismatch between indexed read and write methods: " + indexedReadMethod + " - " + indexedWriteMethod);
                }
            } else {
                indexedPropertyType = params2[1];
            }
        }
        if (propertyType != null && (!propertyType.isArray() || propertyType.componentType() != indexedPropertyType)) {
            throw new IntrospectionException("Type mismatch between indexed and non-indexed methods: " + indexedReadMethod + " - " + indexedWriteMethod);
        }
        return indexedPropertyType;
    }

    public static boolean equals(PropertyDescriptor pd, PropertyDescriptor otherPd) {
        return ObjectUtils.nullSafeEquals(pd.getReadMethod(), otherPd.getReadMethod()) && ObjectUtils.nullSafeEquals(pd.getWriteMethod(), otherPd.getWriteMethod()) && ObjectUtils.nullSafeEquals(pd.getPropertyType(), otherPd.getPropertyType()) && ObjectUtils.nullSafeEquals(pd.getPropertyEditorClass(), otherPd.getPropertyEditorClass()) && pd.isBound() == otherPd.isBound() && pd.isConstrained() == otherPd.isConstrained();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyDescriptorUtils$BasicPropertyDescriptor.class */
    private static class BasicPropertyDescriptor extends PropertyDescriptor {

        @Nullable
        private Method readMethod;

        @Nullable
        private Method writeMethod;
        private final List<Method> alternativeWriteMethods;

        public BasicPropertyDescriptor(String propertyName, @Nullable Method readMethod, @Nullable Method writeMethod) throws IntrospectionException {
            super(propertyName, readMethod, writeMethod);
            this.alternativeWriteMethods = new ArrayList();
        }

        public void setReadMethod(@Nullable Method readMethod) {
            this.readMethod = readMethod;
        }

        @Nullable
        public Method getReadMethod() {
            return this.readMethod;
        }

        public void setWriteMethod(@Nullable Method writeMethod) {
            this.writeMethod = writeMethod;
        }

        public void addWriteMethod(Method writeMethod) {
            if (this.writeMethod != null) {
                this.alternativeWriteMethods.add(this.writeMethod);
                this.writeMethod = null;
            }
            this.alternativeWriteMethods.add(writeMethod);
        }

        @Nullable
        public Method getWriteMethod() {
            if (this.writeMethod == null && !this.alternativeWriteMethods.isEmpty()) {
                if (this.readMethod == null) {
                    return this.alternativeWriteMethods.get(0);
                }
                Iterator<Method> it = this.alternativeWriteMethods.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Method method = it.next();
                    if (this.readMethod.getReturnType().isAssignableFrom(method.getParameterTypes()[0])) {
                        this.writeMethod = method;
                        break;
                    }
                }
            }
            return this.writeMethod;
        }
    }
}
