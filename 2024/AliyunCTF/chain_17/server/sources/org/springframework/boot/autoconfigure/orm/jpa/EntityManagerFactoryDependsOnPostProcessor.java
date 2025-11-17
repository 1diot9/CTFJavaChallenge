package org.springframework.boot.autoconfigure.orm.jpa;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/EntityManagerFactoryDependsOnPostProcessor.class */
public class EntityManagerFactoryDependsOnPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
    public EntityManagerFactoryDependsOnPostProcessor(String... dependsOn) {
        super((Class<?>) EntityManagerFactory.class, (Class<? extends FactoryBean<?>>) AbstractEntityManagerFactoryBean.class, dependsOn);
    }

    public EntityManagerFactoryDependsOnPostProcessor(Class<?>... dependsOn) {
        super((Class<?>) EntityManagerFactory.class, (Class<? extends FactoryBean<?>>) AbstractEntityManagerFactoryBean.class, dependsOn);
    }
}
