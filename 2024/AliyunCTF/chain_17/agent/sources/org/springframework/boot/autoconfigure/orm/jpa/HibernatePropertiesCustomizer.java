package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.Map;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernatePropertiesCustomizer.class */
public interface HibernatePropertiesCustomizer {
    void customize(Map<String, Object> hibernateProperties);
}
