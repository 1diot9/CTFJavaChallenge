package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.AbstractDiscriminator;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/sift/MDCBasedDiscriminator.class */
public class MDCBasedDiscriminator extends AbstractDiscriminator<ILoggingEvent> {
    private String key;
    private String defaultValue;

    @Override // ch.qos.logback.core.sift.Discriminator
    public String getDiscriminatingValue(ILoggingEvent event) {
        Map<String, String> mdcMap = event.getMDCPropertyMap();
        if (mdcMap == null) {
            return this.defaultValue;
        }
        String mdcValue = mdcMap.get(this.key);
        if (mdcValue == null) {
            return this.defaultValue;
        }
        return mdcValue;
    }

    @Override // ch.qos.logback.core.sift.AbstractDiscriminator, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        int errors = 0;
        if (OptionHelper.isNullOrEmpty(this.key)) {
            errors = 0 + 1;
            addError("The \"Key\" property must be set");
        }
        if (OptionHelper.isNullOrEmpty(this.defaultValue)) {
            errors++;
            addError("The \"DefaultValue\" property must be set");
        }
        if (errors == 0) {
            this.started = true;
        }
    }

    @Override // ch.qos.logback.core.sift.Discriminator
    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
