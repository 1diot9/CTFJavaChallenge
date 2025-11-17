package org.springframework.boot.web.context;

import java.util.Locale;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.core.annotation.Order;

@Order(0)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/context/MissingWebServerFactoryBeanFailureAnalyzer.class */
class MissingWebServerFactoryBeanFailureAnalyzer extends AbstractFailureAnalyzer<MissingWebServerFactoryBeanException> {
    MissingWebServerFactoryBeanFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, MissingWebServerFactoryBeanException cause) {
        return new FailureAnalysis("Web application could not be started as there was no " + cause.getBeanType().getName() + " bean defined in the context.", "Check your application's dependencies for a supported " + cause.getWebApplicationType().name().toLowerCase(Locale.ENGLISH) + " web server.\nCheck the configured web application type.", cause);
    }
}
