package cn.hutool.core.date.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateFormat.class */
public class FastDateFormat extends Format implements DateParser, DatePrinter {
    private static final long serialVersionUID = 8097890768636183236L;
    public static final int FULL = 0;
    public static final int LONG = 1;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static final FormatCache<FastDateFormat> CACHE = new FormatCache<FastDateFormat>() { // from class: cn.hutool.core.date.format.FastDateFormat.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // cn.hutool.core.date.format.FormatCache
        public FastDateFormat createInstance(String pattern, TimeZone timeZone, Locale locale) {
            return new FastDateFormat(pattern, timeZone, locale);
        }
    };
    private final FastDatePrinter printer;
    private final FastDateParser parser;

    public static FastDateFormat getInstance() {
        return CACHE.getInstance();
    }

    public static FastDateFormat getInstance(String pattern) {
        return CACHE.getInstance(pattern, null, null);
    }

    public static FastDateFormat getInstance(String pattern, TimeZone timeZone) {
        return CACHE.getInstance(pattern, timeZone, null);
    }

    public static FastDateFormat getInstance(String pattern, Locale locale) {
        return CACHE.getInstance(pattern, null, locale);
    }

    public static FastDateFormat getInstance(String pattern, TimeZone timeZone, Locale locale) {
        return CACHE.getInstance(pattern, timeZone, locale);
    }

    public static FastDateFormat getDateInstance(int style) {
        return CACHE.getDateInstance(style, null, null);
    }

    public static FastDateFormat getDateInstance(int style, Locale locale) {
        return CACHE.getDateInstance(style, null, locale);
    }

    public static FastDateFormat getDateInstance(int style, TimeZone timeZone) {
        return CACHE.getDateInstance(style, timeZone, null);
    }

    public static FastDateFormat getDateInstance(int style, TimeZone timeZone, Locale locale) {
        return CACHE.getDateInstance(style, timeZone, locale);
    }

    public static FastDateFormat getTimeInstance(int style) {
        return CACHE.getTimeInstance(style, null, null);
    }

    public static FastDateFormat getTimeInstance(int style, Locale locale) {
        return CACHE.getTimeInstance(style, null, locale);
    }

    public static FastDateFormat getTimeInstance(int style, TimeZone timeZone) {
        return CACHE.getTimeInstance(style, timeZone, null);
    }

    public static FastDateFormat getTimeInstance(int style, TimeZone timeZone, Locale locale) {
        return CACHE.getTimeInstance(style, timeZone, locale);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle) {
        return CACHE.getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), null, null);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, Locale locale) {
        return CACHE.getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), null, locale);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone) {
        return getDateTimeInstance(dateStyle, timeStyle, timeZone, null);
    }

    public static FastDateFormat getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
        return CACHE.getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), timeZone, locale);
    }

    protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    protected FastDateFormat(String pattern, TimeZone timeZone, Locale locale, Date centuryStart) {
        this.printer = new FastDatePrinter(pattern, timeZone, locale);
        this.parser = new FastDateParser(pattern, timeZone, locale, centuryStart);
    }

    @Override // java.text.Format
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        return toAppendTo.append(this.printer.format(obj));
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public String format(long millis) {
        return this.printer.format(millis);
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public String format(Date date) {
        return this.printer.format(date);
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public String format(Calendar calendar) {
        return this.printer.format(calendar);
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public <B extends Appendable> B format(long j, B b) {
        return (B) this.printer.format(j, (long) b);
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public <B extends Appendable> B format(Date date, B b) {
        return (B) this.printer.format(date, (Date) b);
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public <B extends Appendable> B format(Calendar calendar, B b) {
        return (B) this.printer.format(calendar, (Calendar) b);
    }

    @Override // cn.hutool.core.date.format.DateParser
    public Date parse(String source) throws ParseException {
        return this.parser.parse(source);
    }

    @Override // cn.hutool.core.date.format.DateParser
    public Date parse(String source, ParsePosition pos) {
        return this.parser.parse(source, pos);
    }

    @Override // cn.hutool.core.date.format.DateParser
    public boolean parse(String source, ParsePosition pos, Calendar calendar) {
        return this.parser.parse(source, pos, calendar);
    }

    @Override // java.text.Format, cn.hutool.core.date.format.DateParser
    public Object parseObject(String source, ParsePosition pos) {
        return this.parser.parseObject(source, pos);
    }

    @Override // cn.hutool.core.date.format.DateBasic
    public String getPattern() {
        return this.printer.getPattern();
    }

    @Override // cn.hutool.core.date.format.DateBasic
    public TimeZone getTimeZone() {
        return this.printer.getTimeZone();
    }

    @Override // cn.hutool.core.date.format.DateBasic
    public Locale getLocale() {
        return this.printer.getLocale();
    }

    public int getMaxLengthEstimate() {
        return this.printer.getMaxLengthEstimate();
    }

    public DateTimeFormatter getDateTimeFormatter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getPattern());
        if (getLocale() != null) {
            formatter = formatter.withLocale(getLocale());
        }
        if (getTimeZone() != null) {
            formatter = formatter.withZone(getTimeZone().toZoneId());
        }
        return formatter;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FastDateFormat)) {
            return false;
        }
        FastDateFormat other = (FastDateFormat) obj;
        return this.printer.equals(other.printer);
    }

    public int hashCode() {
        return this.printer.hashCode();
    }

    public String toString() {
        return "FastDateFormat[" + this.printer.getPattern() + "," + this.printer.getLocale() + "," + this.printer.getTimeZone().getID() + "]";
    }
}
