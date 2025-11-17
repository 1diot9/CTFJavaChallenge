package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.LevelToSyslogSeverity;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.net.SyslogAppenderBase;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/SyslogStartConverter.class */
public class SyslogStartConverter extends ClassicConverter {
    SimpleDateFormat simpleMonthFormat;
    SimpleDateFormat simpleTimeFormat;
    String localHostName;
    int facility;
    long lastTimestamp = -1;
    String timesmapStr = null;
    private final Calendar calendar = Calendar.getInstance(Locale.US);

    @Override // ch.qos.logback.core.pattern.DynamicConverter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        int errorCount = 0;
        String facilityStr = getFirstOption();
        if (facilityStr == null) {
            addError("was expecting a facility string as an option");
            return;
        }
        this.facility = SyslogAppenderBase.facilityStringToint(facilityStr);
        this.localHostName = getLocalHostname();
        try {
            this.simpleMonthFormat = new SimpleDateFormat("MMM", Locale.US);
            this.simpleTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        } catch (IllegalArgumentException e) {
            addError("Could not instantiate SimpleDateFormat", e);
            errorCount = 0 + 1;
        }
        if (errorCount == 0) {
            super.start();
        }
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder();
        int pri = this.facility + LevelToSyslogSeverity.convert(event);
        sb.append("<");
        sb.append(pri);
        sb.append(">");
        sb.append(computeTimeStampString(event.getTimeStamp()));
        sb.append(' ');
        sb.append(this.localHostName);
        sb.append(' ');
        return sb.toString();
    }

    public String getLocalHostname() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException uhe) {
            addError("Could not determine local host name", uhe);
            return CoreConstants.UNKNOWN_LOCALHOST;
        }
    }

    String computeTimeStampString(long now) {
        String str;
        synchronized (this) {
            if (now / 1000 != this.lastTimestamp) {
                this.lastTimestamp = now / 1000;
                Date nowDate = new Date(now);
                this.calendar.setTime(nowDate);
                this.timesmapStr = String.format("%s %2d %s", this.simpleMonthFormat.format(nowDate), Integer.valueOf(this.calendar.get(5)), this.simpleTimeFormat.format(nowDate));
            }
            str = this.timesmapStr;
        }
        return str;
    }
}
