package org.springframework.boot.autoconfigure.jdbc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/JdbcConnectionDetailsBeanPostProcessor.class */
abstract class JdbcConnectionDetailsBeanPostProcessor<T> implements BeanPostProcessor, PriorityOrdered {
    private final Class<T> dataSourceClass;
    private final ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider;

    protected abstract Object processDataSource(T dataSource, JdbcConnectionDetails connectionDetails);

    /* JADX INFO: Access modifiers changed from: package-private */
    public JdbcConnectionDetailsBeanPostProcessor(Class<T> dataSourceClass, ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
        this.dataSourceClass = dataSourceClass;
        this.connectionDetailsProvider = connectionDetailsProvider;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (this.dataSourceClass.isAssignableFrom(bean.getClass()) && "dataSource".equals(beanName)) {
            JdbcConnectionDetails connectionDetails = this.connectionDetailsProvider.getObject();
            if (!(connectionDetails instanceof PropertiesJdbcConnectionDetails)) {
                return processDataSource(bean, connectionDetails);
            }
        }
        return bean;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return -2147483646;
    }
}
