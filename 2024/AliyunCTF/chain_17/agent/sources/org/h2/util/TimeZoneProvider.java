package org.h2.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.util.Locale;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/TimeZoneProvider.class */
public abstract class TimeZoneProvider {
    public static final TimeZoneProvider UTC = new Simple(0);
    public static TimeZoneProvider[] CACHE;
    private static final int CACHE_SIZE = 32;

    public abstract int getTimeZoneOffsetUTC(long j);

    public abstract int getTimeZoneOffsetLocal(long j, long j2);

    public abstract long getEpochSecondsFromLocal(long j, long j2);

    public abstract String getId();

    public abstract String getShortId(long j);

    public static TimeZoneProvider ofOffset(int i) {
        if (i == 0) {
            return UTC;
        }
        if (i < -64800 || i > 64800) {
            throw new IllegalArgumentException("Time zone offset " + i + " seconds is out of range");
        }
        return new Simple(i);
    }

    public static TimeZoneProvider ofId(String str) throws RuntimeException {
        TimeZoneProvider timeZoneProvider;
        char charAt;
        char charAt2;
        char charAt3;
        char charAt4;
        char charAt5;
        int length = str.length();
        if (length == 1 && str.charAt(0) == 'Z') {
            return UTC;
        }
        int i = 0;
        if (str.startsWith("GMT") || str.startsWith("UTC")) {
            if (length == 3) {
                return UTC;
            }
            i = 3;
        }
        if (length > i) {
            boolean z = false;
            char charAt6 = str.charAt(i);
            if (length > i + 1) {
                if (charAt6 == '+') {
                    i++;
                    charAt6 = str.charAt(i);
                } else if (charAt6 == '-') {
                    z = true;
                    i++;
                    charAt6 = str.charAt(i);
                }
            }
            if (i != 3 && charAt6 >= '0' && charAt6 <= '9') {
                int i2 = charAt6 - '0';
                i++;
                if (i < length && (charAt5 = str.charAt(i)) >= '0' && charAt5 <= '9') {
                    i2 = ((i2 * 10) + charAt5) - 48;
                    i++;
                }
                if (i == length) {
                    int i3 = i2 * 3600;
                    return ofOffset(z ? -i3 : i3);
                }
                if (str.charAt(i) == ':') {
                    i++;
                    if (i < length && (charAt = str.charAt(i)) >= '0' && charAt <= '9') {
                        int i4 = charAt - '0';
                        i++;
                        if (i < length && (charAt4 = str.charAt(i)) >= '0' && charAt4 <= '9') {
                            i4 = ((i4 * 10) + charAt4) - 48;
                            i++;
                        }
                        if (i == length) {
                            int i5 = ((i2 * 60) + i4) * 60;
                            return ofOffset(z ? -i5 : i5);
                        }
                        if (str.charAt(i) == ':') {
                            i++;
                            if (i < length && (charAt2 = str.charAt(i)) >= '0' && charAt2 <= '9') {
                                int i6 = charAt2 - '0';
                                i++;
                                if (i < length && (charAt3 = str.charAt(i)) >= '0' && charAt3 <= '9') {
                                    i6 = ((i6 * 10) + charAt3) - 48;
                                    i++;
                                }
                                if (i == length) {
                                    int i7 = (((i2 * 60) + i4) * 60) + i6;
                                    return ofOffset(z ? -i7 : i7);
                                }
                            }
                        }
                    }
                }
            }
            if (i > 0) {
                throw new IllegalArgumentException(str);
            }
        }
        int hashCode = str.hashCode() & 31;
        TimeZoneProvider[] timeZoneProviderArr = CACHE;
        if (timeZoneProviderArr != null && (timeZoneProvider = timeZoneProviderArr[hashCode]) != null && timeZoneProvider.getId().equals(str)) {
            return timeZoneProvider;
        }
        WithTimeZone withTimeZone = new WithTimeZone(ZoneId.of(str, ZoneId.SHORT_IDS));
        if (timeZoneProviderArr == null) {
            TimeZoneProvider[] timeZoneProviderArr2 = new TimeZoneProvider[32];
            timeZoneProviderArr = timeZoneProviderArr2;
            CACHE = timeZoneProviderArr2;
        }
        timeZoneProviderArr[hashCode] = withTimeZone;
        return withTimeZone;
    }

    public static TimeZoneProvider getDefault() {
        ZoneOffset offset;
        ZoneId systemDefault = ZoneId.systemDefault();
        if (systemDefault instanceof ZoneOffset) {
            offset = (ZoneOffset) systemDefault;
        } else {
            ZoneRules rules = systemDefault.getRules();
            if (!rules.isFixedOffset()) {
                return new WithTimeZone(systemDefault);
            }
            offset = rules.getOffset(Instant.EPOCH);
        }
        return ofOffset(offset.getTotalSeconds());
    }

    public boolean hasFixedOffset() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/TimeZoneProvider$Simple.class */
    public static final class Simple extends TimeZoneProvider {
        private final int offset;
        private volatile String id;

        Simple(int i) {
            this.offset = i;
        }

        public int hashCode() {
            return this.offset + 129607;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return obj != null && obj.getClass() == Simple.class && this.offset == ((Simple) obj).offset;
        }

        @Override // org.h2.util.TimeZoneProvider
        public int getTimeZoneOffsetUTC(long j) {
            return this.offset;
        }

        @Override // org.h2.util.TimeZoneProvider
        public int getTimeZoneOffsetLocal(long j, long j2) {
            return this.offset;
        }

        @Override // org.h2.util.TimeZoneProvider
        public long getEpochSecondsFromLocal(long j, long j2) {
            return DateTimeUtils.getEpochSeconds(j, j2, this.offset);
        }

        @Override // org.h2.util.TimeZoneProvider
        public String getId() {
            String str = this.id;
            if (str == null) {
                String timeZoneNameFromOffsetSeconds = DateTimeUtils.timeZoneNameFromOffsetSeconds(this.offset);
                str = timeZoneNameFromOffsetSeconds;
                this.id = timeZoneNameFromOffsetSeconds;
            }
            return str;
        }

        @Override // org.h2.util.TimeZoneProvider
        public String getShortId(long j) {
            return getId();
        }

        @Override // org.h2.util.TimeZoneProvider
        public boolean hasFixedOffset() {
            return true;
        }

        public String toString() {
            return "TimeZoneProvider " + getId();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/TimeZoneProvider$WithTimeZone.class */
    public static final class WithTimeZone extends TimeZoneProvider {
        static final long SECONDS_PER_PERIOD = 12622780800L;
        static final long SECONDS_PER_YEAR = 31556952;
        private static volatile DateTimeFormatter TIME_ZONE_FORMATTER;
        private final ZoneId zoneId;

        WithTimeZone(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        public int hashCode() {
            return this.zoneId.hashCode() + 951689;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != WithTimeZone.class) {
                return false;
            }
            return this.zoneId.equals(((WithTimeZone) obj).zoneId);
        }

        @Override // org.h2.util.TimeZoneProvider
        public int getTimeZoneOffsetUTC(long j) {
            if (j > 31556889832715999L) {
                j -= SECONDS_PER_PERIOD;
            } else if (j < -31557014135532000L) {
                j += SECONDS_PER_PERIOD;
            }
            return this.zoneId.getRules().getOffset(Instant.ofEpochSecond(j)).getTotalSeconds();
        }

        @Override // org.h2.util.TimeZoneProvider
        public int getTimeZoneOffsetLocal(long j, long j2) {
            int i = (int) (j2 / DateTimeUtils.NANOS_PER_SECOND);
            int i2 = i / 60;
            int i3 = i - (i2 * 60);
            int i4 = i2 / 60;
            return ZonedDateTime.of(LocalDateTime.of(yearForCalendar(DateTimeUtils.yearFromDateValue(j)), DateTimeUtils.monthFromDateValue(j), DateTimeUtils.dayFromDateValue(j), i4, i2 - (i4 * 60), i3), this.zoneId).getOffset().getTotalSeconds();
        }

        @Override // org.h2.util.TimeZoneProvider
        public long getEpochSecondsFromLocal(long j, long j2) {
            int i = (int) (j2 / DateTimeUtils.NANOS_PER_SECOND);
            int i2 = i / 60;
            int i3 = i - (i2 * 60);
            int i4 = i2 / 60;
            int i5 = i2 - (i4 * 60);
            return ZonedDateTime.of(LocalDateTime.of(yearForCalendar(DateTimeUtils.yearFromDateValue(j)), DateTimeUtils.monthFromDateValue(j), DateTimeUtils.dayFromDateValue(j), i4, i5, i3), this.zoneId).toOffsetDateTime().toEpochSecond() + ((r0 - r0) * SECONDS_PER_YEAR);
        }

        @Override // org.h2.util.TimeZoneProvider
        public String getId() {
            return this.zoneId.getId();
        }

        @Override // org.h2.util.TimeZoneProvider
        public String getShortId(long j) {
            DateTimeFormatter dateTimeFormatter = TIME_ZONE_FORMATTER;
            if (dateTimeFormatter == null) {
                DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("z", Locale.ENGLISH);
                dateTimeFormatter = ofPattern;
                TIME_ZONE_FORMATTER = ofPattern;
            }
            return ZonedDateTime.ofInstant(Instant.ofEpochSecond(j), this.zoneId).format(dateTimeFormatter);
        }

        private static int yearForCalendar(int i) {
            if (i > 999999999) {
                i -= 400;
            } else if (i < -999999999) {
                i += 400;
            }
            return i;
        }

        public String toString() {
            return "TimeZoneProvider " + this.zoneId.getId();
        }
    }
}
