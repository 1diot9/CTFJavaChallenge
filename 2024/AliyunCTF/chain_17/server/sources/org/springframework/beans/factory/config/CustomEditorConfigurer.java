package org.springframework.beans.factory.config;

import java.beans.PropertyEditor;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/CustomEditorConfigurer.class */
public class CustomEditorConfigurer implements BeanFactoryPostProcessor, Ordered {
    protected final Log logger = LogFactory.getLog(getClass());
    private int order = Integer.MAX_VALUE;

    @Nullable
    private PropertyEditorRegistrar[] propertyEditorRegistrars;

    @Nullable
    private Map<Class<?>, Class<? extends PropertyEditor>> customEditors;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setPropertyEditorRegistrars(PropertyEditorRegistrar[] propertyEditorRegistrars) {
        this.propertyEditorRegistrars = propertyEditorRegistrars;
    }

    public void setCustomEditors(Map<Class<?>, Class<? extends PropertyEditor>> customEditors) {
        this.customEditors = customEditors;
    }

    @Override // org.springframework.beans.factory.config.BeanFactoryPostProcessor
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.propertyEditorRegistrars != null) {
            for (PropertyEditorRegistrar propertyEditorRegistrar : this.propertyEditorRegistrars) {
                beanFactory.addPropertyEditorRegistrar(propertyEditorRegistrar);
            }
        }
        if (this.customEditors != null) {
            Map<Class<?>, Class<? extends PropertyEditor>> map = this.customEditors;
            Objects.requireNonNull(beanFactory);
            map.forEach(beanFactory::registerCustomEditor);
        }
    }
}
