package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/sift/ContextBasedDiscriminator.class */
public class ContextBasedDiscriminator extends AbstractDiscriminator<ILoggingEvent> {
    private static final String KEY = "contextName";
    private String defaultValue;

    @Override // ch.qos.logback.core.sift.Discriminator
    public String getDiscriminatingValue(ILoggingEvent event) {
        String contextName = event.getLoggerContextVO().getName();
        if (contextName == null) {
            return this.defaultValue;
        }
        return contextName;
    }

    @Override // ch.qos.logback.core.sift.Discriminator
    public String getKey() {
        return KEY;
    }

    public void setKey(String key) {
        throw new UnsupportedOperationException("Key cannot be set. Using fixed key contextName");
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
