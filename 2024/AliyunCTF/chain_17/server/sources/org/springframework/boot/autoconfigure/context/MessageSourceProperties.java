package org.springframework.boot.autoconfigure.context;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.web.servlet.tags.form.ErrorsTag;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/context/MessageSourceProperties.class */
public class MessageSourceProperties {

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration cacheDuration;
    private String basename = ErrorsTag.MESSAGES_ATTRIBUTE;
    private Charset encoding = StandardCharsets.UTF_8;
    private boolean fallbackToSystemLocale = true;
    private boolean alwaysUseMessageFormat = false;
    private boolean useCodeAsDefaultMessage = false;

    public String getBasename() {
        return this.basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public Charset getEncoding() {
        return this.encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public Duration getCacheDuration() {
        return this.cacheDuration;
    }

    public void setCacheDuration(Duration cacheDuration) {
        this.cacheDuration = cacheDuration;
    }

    public boolean isFallbackToSystemLocale() {
        return this.fallbackToSystemLocale;
    }

    public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }

    public boolean isAlwaysUseMessageFormat() {
        return this.alwaysUseMessageFormat;
    }

    public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
        this.alwaysUseMessageFormat = alwaysUseMessageFormat;
    }

    public boolean isUseCodeAsDefaultMessage() {
        return this.useCodeAsDefaultMessage;
    }

    public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
        this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
    }
}
