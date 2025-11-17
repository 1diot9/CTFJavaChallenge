package org.springframework.boot.autoconfigure.batch;

import org.springframework.core.convert.support.ConfigurableConversionService;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/batch/BatchConversionServiceCustomizer.class */
public interface BatchConversionServiceCustomizer {
    void customize(ConfigurableConversionService configurableConversionService);
}
