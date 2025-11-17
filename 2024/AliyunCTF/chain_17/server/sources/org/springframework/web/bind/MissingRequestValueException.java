package org.springframework.web.bind;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/MissingRequestValueException.class */
public class MissingRequestValueException extends ServletRequestBindingException {
    private final boolean missingAfterConversion;

    public MissingRequestValueException(String msg) {
        this(msg, false);
    }

    public MissingRequestValueException(String msg, boolean missingAfterConversion) {
        super(msg);
        this.missingAfterConversion = missingAfterConversion;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MissingRequestValueException(String msg, boolean missingAfterConversion, @Nullable String messageDetailCode, @Nullable Object[] messageDetailArguments) {
        super(msg, messageDetailCode, messageDetailArguments);
        this.missingAfterConversion = missingAfterConversion;
    }

    public boolean isMissingAfterConversion() {
        return this.missingAfterConversion;
    }
}
