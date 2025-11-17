package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.CoreConstants;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/WhitespaceThrowableProxyConverter.class */
public class WhitespaceThrowableProxyConverter extends ThrowableProxyConverter {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.classic.pattern.ThrowableProxyConverter
    public String throwableProxyToString(IThrowableProxy tp) {
        return CoreConstants.LINE_SEPARATOR + super.throwableProxyToString(tp) + CoreConstants.LINE_SEPARATOR;
    }
}
