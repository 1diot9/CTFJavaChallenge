package org.springframework.jmx.export.metadata;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/metadata/JmxAttributeSource.class */
public interface JmxAttributeSource {
    @Nullable
    ManagedResource getManagedResource(Class<?> clazz) throws InvalidMetadataException;

    @Nullable
    ManagedAttribute getManagedAttribute(Method method) throws InvalidMetadataException;

    @Nullable
    ManagedMetric getManagedMetric(Method method) throws InvalidMetadataException;

    @Nullable
    ManagedOperation getManagedOperation(Method method) throws InvalidMetadataException;

    ManagedOperationParameter[] getManagedOperationParameters(Method method) throws InvalidMetadataException;

    ManagedNotification[] getManagedNotifications(Class<?> clazz) throws InvalidMetadataException;
}
