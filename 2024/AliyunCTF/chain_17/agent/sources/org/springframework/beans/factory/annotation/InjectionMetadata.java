package org.springframework.beans.factory.annotation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/InjectionMetadata.class */
public class InjectionMetadata {
    public static final InjectionMetadata EMPTY = new InjectionMetadata(Object.class, Collections.emptyList()) { // from class: org.springframework.beans.factory.annotation.InjectionMetadata.1
        @Override // org.springframework.beans.factory.annotation.InjectionMetadata
        protected boolean needsRefresh(Class<?> clazz) {
            return false;
        }

        @Override // org.springframework.beans.factory.annotation.InjectionMetadata
        public void checkConfigMembers(RootBeanDefinition beanDefinition) {
        }

        @Override // org.springframework.beans.factory.annotation.InjectionMetadata
        public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) {
        }

        @Override // org.springframework.beans.factory.annotation.InjectionMetadata
        public void clear(@Nullable PropertyValues pvs) {
        }
    };
    private final Class<?> targetClass;
    private final Collection<InjectedElement> injectedElements;

    @Nullable
    private volatile Set<InjectedElement> checkedElements;

    public InjectionMetadata(Class<?> targetClass, Collection<InjectedElement> elements) {
        this.targetClass = targetClass;
        this.injectedElements = elements;
    }

    public Collection<InjectedElement> getInjectedElements() {
        return Collections.unmodifiableCollection(this.injectedElements);
    }

    public Collection<InjectedElement> getInjectedElements(@Nullable PropertyValues pvs) {
        return this.injectedElements.stream().filter(candidate -> {
            return candidate.shouldInject(pvs);
        }).toList();
    }

    protected boolean needsRefresh(Class<?> clazz) {
        return this.targetClass != clazz;
    }

    public void checkConfigMembers(RootBeanDefinition beanDefinition) {
        if (this.injectedElements.isEmpty()) {
            this.checkedElements = Collections.emptySet();
            return;
        }
        Set<InjectedElement> checkedElements = new LinkedHashSet<>(((this.injectedElements.size() * 4) / 3) + 1);
        for (InjectedElement element : this.injectedElements) {
            Member member = element.getMember();
            if (!beanDefinition.isExternallyManagedConfigMember(member)) {
                beanDefinition.registerExternallyManagedConfigMember(member);
                checkedElements.add(element);
            }
        }
        this.checkedElements = checkedElements;
    }

    public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
        Collection<InjectedElement> checkedElements = this.checkedElements;
        Collection<InjectedElement> elementsToIterate = checkedElements != null ? checkedElements : this.injectedElements;
        if (!elementsToIterate.isEmpty()) {
            for (InjectedElement element : elementsToIterate) {
                element.inject(target, beanName, pvs);
            }
        }
    }

    public void clear(@Nullable PropertyValues pvs) {
        Collection<InjectedElement> checkedElements = this.checkedElements;
        Collection<InjectedElement> elementsToIterate = checkedElements != null ? checkedElements : this.injectedElements;
        if (!elementsToIterate.isEmpty()) {
            for (InjectedElement element : elementsToIterate) {
                element.clearPropertySkipping(pvs);
            }
        }
    }

    public static InjectionMetadata forElements(Collection<InjectedElement> elements, Class<?> clazz) {
        return elements.isEmpty() ? new InjectionMetadata(clazz, Collections.emptyList()) : new InjectionMetadata(clazz, elements);
    }

    public static boolean needsRefresh(@Nullable InjectionMetadata metadata, Class<?> clazz) {
        return metadata == null || metadata.needsRefresh(clazz);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/InjectionMetadata$InjectedElement.class */
    public static abstract class InjectedElement {
        protected final Member member;
        protected final boolean isField;

        @Nullable
        protected final PropertyDescriptor pd;

        @Nullable
        protected volatile Boolean skip;

        /* JADX INFO: Access modifiers changed from: protected */
        public InjectedElement(Member member, @Nullable PropertyDescriptor pd) {
            this.member = member;
            this.isField = member instanceof Field;
            this.pd = pd;
        }

        public final Member getMember() {
            return this.member;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public final Class<?> getResourceType() {
            if (this.isField) {
                return ((Field) this.member).getType();
            }
            if (this.pd != null) {
                return this.pd.getPropertyType();
            }
            return ((Method) this.member).getParameterTypes()[0];
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public final void checkResourceType(Class<?> resourceType) {
            if (this.isField) {
                Class<?> fieldType = ((Field) this.member).getType();
                if (!resourceType.isAssignableFrom(fieldType) && !fieldType.isAssignableFrom(resourceType)) {
                    throw new IllegalStateException("Specified field type [" + fieldType + "] is incompatible with resource type [" + resourceType.getName() + "]");
                }
                return;
            }
            Class<?> paramType = this.pd != null ? this.pd.getPropertyType() : ((Method) this.member).getParameterTypes()[0];
            if (!resourceType.isAssignableFrom(paramType) && !paramType.isAssignableFrom(resourceType)) {
                throw new IllegalStateException("Specified parameter type [" + paramType + "] is incompatible with resource type [" + resourceType.getName() + "]");
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public boolean shouldInject(@Nullable PropertyValues pvs) {
            return this.isField || !checkPropertySkipping(pvs);
        }

        protected void inject(Object target, @Nullable String requestingBeanName, @Nullable PropertyValues pvs) throws Throwable {
            if (!shouldInject(pvs)) {
                return;
            }
            if (this.isField) {
                Field field = (Field) this.member;
                ReflectionUtils.makeAccessible(field);
                field.set(target, getResourceToInject(target, requestingBeanName));
            } else {
                try {
                    Method method = (Method) this.member;
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(target, getResourceToInject(target, requestingBeanName));
                } catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        }

        protected boolean checkPropertySkipping(@Nullable PropertyValues pvs) {
            Boolean skip = this.skip;
            if (skip != null) {
                return skip.booleanValue();
            }
            if (pvs == null) {
                this.skip = false;
                return false;
            }
            synchronized (pvs) {
                Boolean skip2 = this.skip;
                if (skip2 != null) {
                    return skip2.booleanValue();
                }
                if (this.pd != null) {
                    if (pvs.contains(this.pd.getName())) {
                        this.skip = true;
                        return true;
                    }
                    if (pvs instanceof MutablePropertyValues) {
                        MutablePropertyValues mpvs = (MutablePropertyValues) pvs;
                        mpvs.registerProcessedProperty(this.pd.getName());
                    }
                }
                this.skip = false;
                return false;
            }
        }

        protected void clearPropertySkipping(@Nullable PropertyValues pvs) {
            if (pvs == null) {
                return;
            }
            synchronized (pvs) {
                if (Boolean.FALSE.equals(this.skip) && this.pd != null && (pvs instanceof MutablePropertyValues)) {
                    MutablePropertyValues mpvs = (MutablePropertyValues) pvs;
                    mpvs.clearProcessedProperty(this.pd.getName());
                }
            }
        }

        @Nullable
        protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
            return null;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof InjectedElement) {
                    InjectedElement that = (InjectedElement) other;
                    if (this.member.equals(that.member)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.member.getClass().hashCode() * 29) + this.member.getName().hashCode();
        }

        public String toString() {
            return getClass().getSimpleName() + " for " + this.member;
        }
    }
}
