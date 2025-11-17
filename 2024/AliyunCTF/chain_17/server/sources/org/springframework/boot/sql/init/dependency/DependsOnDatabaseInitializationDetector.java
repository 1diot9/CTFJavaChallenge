package org.springframework.boot.sql.init.dependency;

import java.util.Set;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/sql/init/dependency/DependsOnDatabaseInitializationDetector.class */
public interface DependsOnDatabaseInitializationDetector {
    Set<String> detect(ConfigurableListableBeanFactory beanFactory);
}
