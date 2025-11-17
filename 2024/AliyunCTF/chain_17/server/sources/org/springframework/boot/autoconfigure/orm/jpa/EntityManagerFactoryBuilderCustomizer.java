package org.springframework.boot.autoconfigure.orm.jpa;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/EntityManagerFactoryBuilderCustomizer.class */
public interface EntityManagerFactoryBuilderCustomizer {
    void customize(EntityManagerFactoryBuilder builder);
}
