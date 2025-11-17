package org.springframework.boot.context;

import java.io.IOException;
import java.util.Collection;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/TypeExcludeFilter.class */
public class TypeExcludeFilter implements TypeFilter, BeanFactoryAware {
    private BeanFactory beanFactory;
    private Collection<TypeExcludeFilter> delegates;

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.core.type.filter.TypeFilter
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        if ((this.beanFactory instanceof ListableBeanFactory) && getClass() == TypeExcludeFilter.class) {
            for (TypeExcludeFilter delegate : getDelegates()) {
                if (delegate.match(metadataReader, metadataReaderFactory)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private Collection<TypeExcludeFilter> getDelegates() {
        Collection<TypeExcludeFilter> delegates = this.delegates;
        if (delegates == null) {
            delegates = ((ListableBeanFactory) this.beanFactory).getBeansOfType(TypeExcludeFilter.class).values();
            this.delegates = delegates;
        }
        return delegates;
    }

    public boolean equals(Object obj) {
        throw new IllegalStateException("TypeExcludeFilter " + getClass() + " has not implemented equals");
    }

    public int hashCode() {
        throw new IllegalStateException("TypeExcludeFilter " + getClass() + " has not implemented hashCode");
    }
}
