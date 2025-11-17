package org.h2.mode;

import java.util.Iterator;
import java.util.List;
import org.h2.engine.SessionLocal;
import org.h2.mode.ToDateTokenizer;
import org.h2.util.DateTimeUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/ToDateParser.class */
public final class ToDateParser {
    private final SessionLocal session;
    private final String unmodifiedInputStr;
    private final String unmodifiedFormatStr;
    private final ConfigParam functionName;
    private String inputStr;
    private String formatStr;
    private boolean bc;
    private long absoluteDay;
    private int year;
    private int month;
    private int dayOfYear;
    private int hour;
    private int minute;
    private int second;
    private int nanos;
    private int hour12;
    private TimeZoneProvider timeZone;
    private int timeZoneHour;
    private int timeZoneMinute;
    private int currentYear;
    private int currentMonth;
    private boolean doyValid = false;
    private boolean absoluteDayValid = false;
    private boolean hour12Valid = false;
    private boolean timeZoneHMValid = false;
    private int day = 1;
    private boolean isAM = true;

    private ToDateParser(SessionLocal sessionLocal, ConfigParam configParam, String str, String str2) {
        this.session = sessionLocal;
        this.functionName = configParam;
        this.inputStr = str.trim();
        this.unmodifiedInputStr = this.inputStr;
        if (str2 == null || str2.isEmpty()) {
            this.formatStr = configParam.getDefaultFormatStr();
        } else {
            this.formatStr = str2.trim();
        }
        this.unmodifiedFormatStr = this.formatStr;
    }

    private static ToDateParser getTimestampParser(SessionLocal sessionLocal, ConfigParam configParam, String str, String str2) {
        ToDateParser toDateParser = new ToDateParser(sessionLocal, configParam, str, str2);
        parse(toDateParser);
        return toDateParser;
    }

    private ValueTimestamp getResultingValue() {
        long dateValue;
        int i;
        if (this.absoluteDayValid) {
            dateValue = DateTimeUtils.dateValueFromAbsoluteDay(this.absoluteDay);
        } else {
            int i2 = this.year;
            if (i2 == 0) {
                i2 = getCurrentYear();
            }
            if (this.bc) {
                i2 = 1 - i2;
            }
            if (this.doyValid) {
                dateValue = DateTimeUtils.dateValueFromAbsoluteDay((DateTimeUtils.absoluteDayFromYear(i2) + this.dayOfYear) - 1);
            } else {
                int i3 = this.month;
                if (i3 == 0) {
                    i3 = getCurrentMonth();
                }
                dateValue = DateTimeUtils.dateValue(i2, i3, this.day);
            }
        }
        if (this.hour12Valid) {
            i = this.hour12 % 12;
            if (!this.isAM) {
                i += 12;
            }
        } else {
            i = this.hour;
        }
        return ValueTimestamp.fromDateValueAndNanos(dateValue, (((((i * 60) + this.minute) * 60) + this.second) * DateTimeUtils.NANOS_PER_SECOND) + this.nanos);
    }

    private ValueTimestampTimeZone getResultingValueWithTimeZone() {
        int timeZoneOffsetLocal;
        ValueTimestamp resultingValue = getResultingValue();
        long dateValue = resultingValue.getDateValue();
        long timeNanos = resultingValue.getTimeNanos();
        if (this.timeZoneHMValid) {
            timeZoneOffsetLocal = ((this.timeZoneHour * 60) + (this.timeZoneHour >= 0 ? this.timeZoneMinute : -this.timeZoneMinute)) * 60;
        } else {
            timeZoneOffsetLocal = (this.timeZone != null ? this.timeZone : this.session.currentTimeZone()).getTimeZoneOffsetLocal(dateValue, timeNanos);
        }
        return ValueTimestampTimeZone.fromDateValueAndNanos(dateValue, resultingValue.getTimeNanos(), timeZoneOffsetLocal);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getInputStr() {
        return this.inputStr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getFormatStr() {
        return this.formatStr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getFunctionName() {
        return this.functionName.name();
    }

    private void queryCurrentYearAndMonth() {
        long dateValue = this.session.currentTimestamp().getDateValue();
        this.currentYear = DateTimeUtils.yearFromDateValue(dateValue);
        this.currentMonth = DateTimeUtils.monthFromDateValue(dateValue);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getCurrentYear() {
        if (this.currentYear == 0) {
            queryCurrentYearAndMonth();
        }
        return this.currentYear;
    }

    int getCurrentMonth() {
        if (this.currentMonth == 0) {
            queryCurrentYearAndMonth();
        }
        return this.currentMonth;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAbsoluteDay(int i) {
        this.doyValid = false;
        this.absoluteDayValid = true;
        this.absoluteDay = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBC(boolean z) {
        this.doyValid = false;
        this.absoluteDayValid = false;
        this.bc = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setYear(int i) {
        this.doyValid = false;
        this.absoluteDayValid = false;
        this.year = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMonth(int i) {
        this.doyValid = false;
        this.absoluteDayValid = false;
        this.month = i;
        if (this.year == 0) {
            this.year = 1970;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDay(int i) {
        this.doyValid = false;
        this.absoluteDayValid = false;
        this.day = i;
        if (this.year == 0) {
            this.year = 1970;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDayOfYear(int i) {
        this.doyValid = true;
        this.absoluteDayValid = false;
        this.dayOfYear = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHour(int i) {
        this.hour12Valid = false;
        this.hour = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMinute(int i) {
        this.minute = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSecond(int i) {
        this.second = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNanos(int i) {
        this.nanos = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAmPm(boolean z) {
        this.hour12Valid = true;
        this.isAM = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHour12(int i) {
        this.hour12Valid = true;
        this.hour12 = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTimeZone(TimeZoneProvider timeZoneProvider) {
        this.timeZoneHMValid = false;
        this.timeZone = timeZoneProvider;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTimeZoneHour(int i) {
        this.timeZoneHMValid = true;
        this.timeZoneHour = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTimeZoneMinute(int i) {
        this.timeZoneHMValid = true;
        this.timeZoneMinute = i;
    }

    private boolean hasToParseData() {
        return !this.formatStr.isEmpty();
    }

    private void removeFirstChar() {
        if (!this.formatStr.isEmpty()) {
            this.formatStr = this.formatStr.substring(1);
        }
        if (!this.inputStr.isEmpty()) {
            this.inputStr = this.inputStr.substring(1);
        }
    }

    private static ToDateParser parse(ToDateParser toDateParser) {
        while (toDateParser.hasToParseData()) {
            List<ToDateTokenizer.FormatTokenEnum> tokensInQuestion = ToDateTokenizer.FormatTokenEnum.getTokensInQuestion(toDateParser.getFormatStr());
            if (tokensInQuestion == null) {
                toDateParser.removeFirstChar();
            } else {
                boolean z = false;
                Iterator<ToDateTokenizer.FormatTokenEnum> it = tokensInQuestion.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    if (it.next().parseFormatStrWithToken(toDateParser)) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    toDateParser.removeFirstChar();
                }
            }
        }
        return toDateParser;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void remove(String str, String str2) {
        if (str != null && this.inputStr.length() >= str.length()) {
            this.inputStr = this.inputStr.substring(str.length());
        }
        if (str2 != null && this.formatStr.length() >= str2.length()) {
            this.formatStr = this.formatStr.substring(str2.length());
        }
    }

    public String toString() {
        int length = this.inputStr.length();
        int length2 = this.unmodifiedInputStr.length() - length;
        int i = length <= 0 ? length : length - 1;
        int length3 = this.unmodifiedFormatStr.length() - this.formatStr.length();
        StringBuilder append = new StringBuilder().append(String.format("\n    %s('%s', '%s')", this.functionName, this.unmodifiedInputStr, this.unmodifiedFormatStr));
        Object[] objArr = new Object[3];
        objArr[0] = String.format(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL + (this.functionName.name().length() + length2) + "s", "");
        objArr[1] = i <= 0 ? "" : String.format(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL + i + "s", "");
        objArr[2] = length3 <= 0 ? "" : String.format(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL + length3 + "s", "");
        return append.append(String.format("\n      %s^%s ,  %s^ <-- Parsing failed at this point", objArr)).toString();
    }

    public static ValueTimestamp toTimestamp(SessionLocal sessionLocal, String str, String str2) {
        return getTimestampParser(sessionLocal, ConfigParam.TO_TIMESTAMP, str, str2).getResultingValue();
    }

    public static ValueTimestampTimeZone toTimestampTz(SessionLocal sessionLocal, String str, String str2) {
        return getTimestampParser(sessionLocal, ConfigParam.TO_TIMESTAMP_TZ, str, str2).getResultingValueWithTimeZone();
    }

    public static ValueTimestamp toDate(SessionLocal sessionLocal, String str, String str2) {
        return getTimestampParser(sessionLocal, ConfigParam.TO_DATE, str, str2).getResultingValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/ToDateParser$ConfigParam.class */
    public enum ConfigParam {
        TO_DATE("DD MON YYYY"),
        TO_TIMESTAMP("DD MON YYYY HH:MI:SS"),
        TO_TIMESTAMP_TZ("DD MON YYYY HH:MI:SS TZR");

        private final String defaultFormatStr;

        ConfigParam(String str) {
            this.defaultFormatStr = str;
        }

        String getDefaultFormatStr() {
            return this.defaultFormatStr;
        }
    }
}
