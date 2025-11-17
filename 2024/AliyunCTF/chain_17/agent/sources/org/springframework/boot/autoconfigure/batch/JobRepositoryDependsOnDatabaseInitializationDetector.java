package org.springframework.boot.autoconfigure.batch;

import java.util.Collections;
import java.util.Set;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/JobRepositoryDependsOnDatabaseInitializationDetector.class */
class JobRepositoryDependsOnDatabaseInitializationDetector extends AbstractBeansOfTypeDependsOnDatabaseInitializationDetector {
    JobRepositoryDependsOnDatabaseInitializationDetector() {
    }

    @Override // org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDependsOnDatabaseInitializationDetector
    protected Set<Class<?>> getDependsOnDatabaseInitializationBeanTypes() {
        return Collections.singleton(JobRepository.class);
    }
}
