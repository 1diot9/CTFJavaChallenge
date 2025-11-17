package org.springframework.scheduling.annotation;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.lang.NonNull;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/annotation/AsyncConfigurationSelector.class */
public class AsyncConfigurationSelector extends AdviceModeImportSelector<EnableAsync> {
    private static final String ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME = "org.springframework.scheduling.aspectj.AspectJAsyncConfiguration";

    @Override // org.springframework.context.annotation.AdviceModeImportSelector
    @NonNull
    public String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                return new String[]{ProxyAsyncConfiguration.class.getName()};
            case ASPECTJ:
                return new String[]{ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME};
            default:
                throw new IncompatibleClassChangeError();
        }
    }
}
