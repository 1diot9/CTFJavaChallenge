package org.springframework.beans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/ExtendedBeanInfo.class */
class ExtendedBeanInfo implements BeanInfo {
    private static final Log logger = LogFactory.getLog((Class<?>) ExtendedBeanInfo.class);
    private final BeanInfo delegate;
    private final Set<PropertyDescriptor> propertyDescriptors = new TreeSet(new PropertyDescriptorComparator());

    public ExtendedBeanInfo(BeanInfo delegate) {
        PropertyDescriptor simplePropertyDescriptor;
        this.delegate = delegate;
        for (IndexedPropertyDescriptor indexedPropertyDescriptor : delegate.getPropertyDescriptors()) {
            try {
                Set<PropertyDescriptor> set = this.propertyDescriptors;
                if (indexedPropertyDescriptor instanceof IndexedPropertyDescriptor) {
                    IndexedPropertyDescriptor indexedPd = indexedPropertyDescriptor;
                    simplePropertyDescriptor = new SimpleIndexedPropertyDescriptor(indexedPd);
                } else {
                    simplePropertyDescriptor = new SimplePropertyDescriptor(indexedPropertyDescriptor);
                }
                set.add(simplePropertyDescriptor);
            } catch (IntrospectionException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Ignoring invalid bean property '" + indexedPropertyDescriptor.getName() + "': " + ex.getMessage());
                }
            }
        }
        MethodDescriptor[] methodDescriptors = delegate.getMethodDescriptors();
        if (methodDescriptors != null) {
            for (Method method : findCandidateWriteMethods(methodDescriptors)) {
                try {
                    handleCandidateWriteMethod(method);
                } catch (IntrospectionException ex2) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Ignoring candidate write method [" + method + "]: " + ex2.getMessage());
                    }
                }
            }
        }
    }

    private List<Method> findCandidateWriteMethods(MethodDescriptor[] methodDescriptors) {
        List<Method> matches = new ArrayList<>();
        for (MethodDescriptor methodDescriptor : methodDescriptors) {
            Method method = methodDescriptor.getMethod();
            if (isCandidateWriteMethod(method)) {
                matches.add(method);
            }
        }
        matches.sort(Comparator.comparing((v0) -> {
            return v0.toString();
        }).reversed());
        return matches;
    }

    public static boolean isCandidateWriteMethod(Method method) {
        String methodName = method.getName();
        int nParams = method.getParameterCount();
        return methodName.length() > 3 && methodName.startsWith("set") && Modifier.isPublic(method.getModifiers()) && (!Void.TYPE.isAssignableFrom(method.getReturnType()) || Modifier.isStatic(method.getModifiers())) && (nParams == 1 || (nParams == 2 && Integer.TYPE == method.getParameterTypes()[0]));
    }

    private void handleCandidateWriteMethod(Method method) throws IntrospectionException {
        int nParams = method.getParameterCount();
        String propertyName = propertyNameFor(method);
        Class<?> propertyType = method.getParameterTypes()[nParams - 1];
        IndexedPropertyDescriptor findExistingPropertyDescriptor = findExistingPropertyDescriptor(propertyName, propertyType);
        if (nParams == 1) {
            if (findExistingPropertyDescriptor == null) {
                this.propertyDescriptors.add(new SimplePropertyDescriptor(propertyName, null, method));
                return;
            } else {
                findExistingPropertyDescriptor.setWriteMethod(method);
                return;
            }
        }
        if (nParams == 2) {
            if (findExistingPropertyDescriptor == null) {
                this.propertyDescriptors.add(new SimpleIndexedPropertyDescriptor(propertyName, null, null, null, method));
                return;
            } else if (findExistingPropertyDescriptor instanceof IndexedPropertyDescriptor) {
                IndexedPropertyDescriptor indexedPd = findExistingPropertyDescriptor;
                indexedPd.setIndexedWriteMethod(method);
                return;
            } else {
                this.propertyDescriptors.remove(findExistingPropertyDescriptor);
                this.propertyDescriptors.add(new SimpleIndexedPropertyDescriptor(propertyName, findExistingPropertyDescriptor.getReadMethod(), findExistingPropertyDescriptor.getWriteMethod(), null, method));
                return;
            }
        }
        throw new IllegalArgumentException("Write method must have exactly 1 or 2 parameters: " + method);
    }

    @Nullable
    private PropertyDescriptor findExistingPropertyDescriptor(String propertyName, Class<?> propertyType) {
        Iterator<PropertyDescriptor> it = this.propertyDescriptors.iterator();
        while (it.hasNext()) {
            IndexedPropertyDescriptor indexedPropertyDescriptor = (PropertyDescriptor) it.next();
            String candidateName = indexedPropertyDescriptor.getName();
            if (indexedPropertyDescriptor instanceof IndexedPropertyDescriptor) {
                IndexedPropertyDescriptor indexedPd = indexedPropertyDescriptor;
                Class<?> candidateType = indexedPd.getIndexedPropertyType();
                if (candidateName.equals(propertyName) && (candidateType.equals(propertyType) || candidateType.equals(propertyType.componentType()))) {
                    return indexedPropertyDescriptor;
                }
            } else {
                Class<?> candidateType2 = indexedPropertyDescriptor.getPropertyType();
                if (candidateName.equals(propertyName) && (candidateType2.equals(propertyType) || propertyType.equals(candidateType2.componentType()))) {
                    return indexedPropertyDescriptor;
                }
            }
        }
        return null;
    }

    private String propertyNameFor(Method method) {
        return Introspector.decapitalize(method.getName().substring(3));
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return (PropertyDescriptor[]) this.propertyDescriptors.toArray(new PropertyDescriptor[0]);
    }

    public BeanInfo[] getAdditionalBeanInfo() {
        return this.delegate.getAdditionalBeanInfo();
    }

    public BeanDescriptor getBeanDescriptor() {
        return this.delegate.getBeanDescriptor();
    }

    public int getDefaultEventIndex() {
        return this.delegate.getDefaultEventIndex();
    }

    public int getDefaultPropertyIndex() {
        return this.delegate.getDefaultPropertyIndex();
    }

    public EventSetDescriptor[] getEventSetDescriptors() {
        return this.delegate.getEventSetDescriptors();
    }

    public Image getIcon(int iconKind) {
        return this.delegate.getIcon(iconKind);
    }

    public MethodDescriptor[] getMethodDescriptors() {
        return this.delegate.getMethodDescriptors();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/ExtendedBeanInfo$SimplePropertyDescriptor.class */
    public static class SimplePropertyDescriptor extends PropertyDescriptor {

        @Nullable
        private Method readMethod;

        @Nullable
        private Method writeMethod;

        @Nullable
        private Class<?> propertyType;

        @Nullable
        private Class<?> propertyEditorClass;

        public SimplePropertyDescriptor(PropertyDescriptor original) throws IntrospectionException {
            this(original.getName(), original.getReadMethod(), original.getWriteMethod());
            PropertyDescriptorUtils.copyNonMethodProperties(original, this);
        }

        public SimplePropertyDescriptor(String propertyName, @Nullable Method readMethod, Method writeMethod) throws IntrospectionException {
            super(propertyName, (Method) null, (Method) null);
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;
            this.propertyType = PropertyDescriptorUtils.findPropertyType(readMethod, writeMethod);
        }

        @Nullable
        public Method getReadMethod() {
            return this.readMethod;
        }

        public void setReadMethod(@Nullable Method readMethod) {
            this.readMethod = readMethod;
        }

        @Nullable
        public Method getWriteMethod() {
            return this.writeMethod;
        }

        public void setWriteMethod(@Nullable Method writeMethod) {
            this.writeMethod = writeMethod;
        }

        @Nullable
        public Class<?> getPropertyType() {
            if (this.propertyType == null) {
                try {
                    this.propertyType = PropertyDescriptorUtils.findPropertyType(this.readMethod, this.writeMethod);
                } catch (IntrospectionException e) {
                }
            }
            return this.propertyType;
        }

        @Nullable
        public Class<?> getPropertyEditorClass() {
            return this.propertyEditorClass;
        }

        public void setPropertyEditorClass(@Nullable Class<?> propertyEditorClass) {
            this.propertyEditorClass = propertyEditorClass;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof PropertyDescriptor) {
                    PropertyDescriptor that = (PropertyDescriptor) other;
                    if (PropertyDescriptorUtils.equals(this, that)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(getReadMethod(), getWriteMethod());
        }

        public String toString() {
            return String.format("%s[name=%s, propertyType=%s, readMethod=%s, writeMethod=%s]", getClass().getSimpleName(), getName(), getPropertyType(), this.readMethod, this.writeMethod);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/ExtendedBeanInfo$SimpleIndexedPropertyDescriptor.class */
    public static class SimpleIndexedPropertyDescriptor extends IndexedPropertyDescriptor {

        @Nullable
        private Method readMethod;

        @Nullable
        private Method writeMethod;

        @Nullable
        private Class<?> propertyType;

        @Nullable
        private Method indexedReadMethod;

        @Nullable
        private Method indexedWriteMethod;

        @Nullable
        private Class<?> indexedPropertyType;

        @Nullable
        private Class<?> propertyEditorClass;

        public SimpleIndexedPropertyDescriptor(IndexedPropertyDescriptor original) throws IntrospectionException {
            this(original.getName(), original.getReadMethod(), original.getWriteMethod(), original.getIndexedReadMethod(), original.getIndexedWriteMethod());
            PropertyDescriptorUtils.copyNonMethodProperties(original, this);
        }

        public SimpleIndexedPropertyDescriptor(String propertyName, @Nullable Method readMethod, @Nullable Method writeMethod, @Nullable Method indexedReadMethod, Method indexedWriteMethod) throws IntrospectionException {
            super(propertyName, (Method) null, (Method) null, (Method) null, (Method) null);
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;
            this.propertyType = PropertyDescriptorUtils.findPropertyType(readMethod, writeMethod);
            this.indexedReadMethod = indexedReadMethod;
            this.indexedWriteMethod = indexedWriteMethod;
            this.indexedPropertyType = PropertyDescriptorUtils.findIndexedPropertyType(propertyName, this.propertyType, indexedReadMethod, indexedWriteMethod);
        }

        @Nullable
        public Method getReadMethod() {
            return this.readMethod;
        }

        public void setReadMethod(@Nullable Method readMethod) {
            this.readMethod = readMethod;
        }

        @Nullable
        public Method getWriteMethod() {
            return this.writeMethod;
        }

        public void setWriteMethod(@Nullable Method writeMethod) {
            this.writeMethod = writeMethod;
        }

        @Nullable
        public Class<?> getPropertyType() {
            if (this.propertyType == null) {
                try {
                    this.propertyType = PropertyDescriptorUtils.findPropertyType(this.readMethod, this.writeMethod);
                } catch (IntrospectionException e) {
                }
            }
            return this.propertyType;
        }

        @Nullable
        public Method getIndexedReadMethod() {
            return this.indexedReadMethod;
        }

        public void setIndexedReadMethod(@Nullable Method indexedReadMethod) throws IntrospectionException {
            this.indexedReadMethod = indexedReadMethod;
        }

        @Nullable
        public Method getIndexedWriteMethod() {
            return this.indexedWriteMethod;
        }

        public void setIndexedWriteMethod(@Nullable Method indexedWriteMethod) throws IntrospectionException {
            this.indexedWriteMethod = indexedWriteMethod;
        }

        @Nullable
        public Class<?> getIndexedPropertyType() {
            if (this.indexedPropertyType == null) {
                try {
                    this.indexedPropertyType = PropertyDescriptorUtils.findIndexedPropertyType(getName(), getPropertyType(), this.indexedReadMethod, this.indexedWriteMethod);
                } catch (IntrospectionException e) {
                }
            }
            return this.indexedPropertyType;
        }

        @Nullable
        public Class<?> getPropertyEditorClass() {
            return this.propertyEditorClass;
        }

        public void setPropertyEditorClass(@Nullable Class<?> propertyEditorClass) {
            this.propertyEditorClass = propertyEditorClass;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof IndexedPropertyDescriptor) {
                    IndexedPropertyDescriptor that = (IndexedPropertyDescriptor) other;
                    if (!ObjectUtils.nullSafeEquals(getIndexedReadMethod(), that.getIndexedReadMethod()) || !ObjectUtils.nullSafeEquals(getIndexedWriteMethod(), that.getIndexedWriteMethod()) || !ObjectUtils.nullSafeEquals(getIndexedPropertyType(), that.getIndexedPropertyType()) || !PropertyDescriptorUtils.equals(this, that)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(getReadMethod(), getWriteMethod(), getIndexedReadMethod(), getIndexedWriteMethod());
        }

        public String toString() {
            return String.format("%s[name=%s, propertyType=%s, indexedPropertyType=%s, readMethod=%s, writeMethod=%s, indexedReadMethod=%s, indexedWriteMethod=%s]", getClass().getSimpleName(), getName(), getPropertyType(), getIndexedPropertyType(), this.readMethod, this.writeMethod, this.indexedReadMethod, this.indexedWriteMethod);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/ExtendedBeanInfo$PropertyDescriptorComparator.class */
    static class PropertyDescriptorComparator implements Comparator<PropertyDescriptor> {
        PropertyDescriptorComparator() {
        }

        @Override // java.util.Comparator
        public int compare(PropertyDescriptor desc1, PropertyDescriptor desc2) {
            return desc1.getName().compareTo(desc2.getName());
        }
    }
}
