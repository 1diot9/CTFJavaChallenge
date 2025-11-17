package org.apache.tomcat.util.http;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/CookieProcessorBase.class */
public abstract class CookieProcessorBase implements CookieProcessor {
    private static final String COOKIE_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    protected static final ThreadLocal<DateFormat> COOKIE_DATE_FORMAT = ThreadLocal.withInitial(() -> {
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df;
    });
    protected static final String ANCIENT_DATE = COOKIE_DATE_FORMAT.get().format(new Date(AbstractComponentTracker.LINGERING_TIMEOUT));
    private SameSiteCookies sameSiteCookies = SameSiteCookies.UNSET;
    private boolean partitioned = false;

    public SameSiteCookies getSameSiteCookies() {
        return this.sameSiteCookies;
    }

    public void setSameSiteCookies(String sameSiteCookies) {
        this.sameSiteCookies = SameSiteCookies.fromString(sameSiteCookies);
    }

    public boolean getPartitioned() {
        return this.partitioned;
    }

    public void setPartitioned(boolean partitioned) {
        this.partitioned = partitioned;
    }
}
