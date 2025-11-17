package cn.hutool.core.date.format;

import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/AbstractDateBasic.class */
public abstract class AbstractDateBasic implements DateBasic, Serializable {
    private static final long serialVersionUID = 6333136319870641818L;
    protected final String pattern;
    protected final TimeZone timeZone;
    protected final Locale locale;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractDateBasic(String pattern, TimeZone timeZone, Locale locale) {
        this.pattern = pattern;
        this.timeZone = timeZone;
        this.locale = locale;
    }

    @Override // cn.hutool.core.date.format.DateBasic
    public String getPattern() {
        return this.pattern;
    }

    @Override // cn.hutool.core.date.format.DateBasic
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    @Override // cn.hutool.core.date.format.DateBasic
    public Locale getLocale() {
        return this.locale;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastDatePrinter)) {
            return false;
        }
        AbstractDateBasic other = (AbstractDateBasic) obj;
        return this.pattern.equals(other.pattern) && this.timeZone.equals(other.timeZone) && this.locale.equals(other.locale);
    }

    public int hashCode() {
        return this.pattern.hashCode() + (13 * (this.timeZone.hashCode() + (13 * this.locale.hashCode())));
    }

    public String toString() {
        return "FastDatePrinter[" + this.pattern + "," + this.locale + "," + this.timeZone.getID() + "]";
    }
}
