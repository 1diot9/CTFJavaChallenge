package org.h2.util;

import java.util.ArrayList;
import java.util.Arrays;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDate;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/DateTimeTemplate.class */
public final class DateTimeTemplate {
    private static final SmallLRUCache<String, DateTimeTemplate> CACHE = SmallLRUCache.newInstance(100);
    private final Part[] parts;
    private final boolean containsDate;
    private final boolean containsTime;
    private final boolean containsTimeZone;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/DateTimeTemplate$FieldType.class */
    public static final class FieldType {
        static final int YEAR = 0;
        static final int ROUNDED_YEAR = 1;
        static final int MONTH = 2;
        static final int DAY_OF_MONTH = 3;
        static final int DAY_OF_YEAR = 4;
        static final int HOUR12 = 5;
        static final int HOUR24 = 6;
        static final int MINUTE = 7;
        static final int SECOND_OF_MINUTE = 8;
        static final int SECOND_OF_DAY = 9;
        static final int FRACTION = 10;
        static final int AMPM = 11;
        static final int TIME_ZONE_HOUR = 12;
        static final int TIME_ZONE_MINUTE = 13;
        static final int TIME_ZONE_SECOND = 14;
        static final int DELIMITER = 15;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/DateTimeTemplate$Scanner.class */
    public static final class Scanner {
        final String string;
        private int offset;
        private final int length;

        Scanner(String str) {
            this.string = str;
            this.length = str.length();
        }

        int readChar() {
            if (this.offset >= this.length) {
                return -1;
            }
            String str = this.string;
            int i = this.offset;
            this.offset = i + 1;
            return str.charAt(i);
        }

        void readChar(char c) {
            if (this.offset >= this.length || this.string.charAt(this.offset) != c) {
                throw DbException.get(ErrorCode.PARSE_ERROR_1, this.string);
            }
            this.offset++;
        }

        boolean readCharIf(char c) {
            if (this.offset < this.length && this.string.charAt(this.offset) == c) {
                this.offset++;
                return true;
            }
            return false;
        }

        int readPositiveInt(int i, boolean z) {
            int i2;
            char charAt;
            int i3 = this.offset;
            if (z) {
                i2 = i3;
                while (i2 < this.length && (charAt = this.string.charAt(i2)) >= '0' && charAt <= '9') {
                    i2++;
                }
                if (i3 == i2) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, this.string);
                }
            } else {
                i2 = i3 + i;
                if (i2 > this.length) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, this.string);
                }
            }
            try {
                String str = this.string;
                int i4 = i2;
                this.offset = i4;
                return StringUtils.parseUInt31(str, i3, i4);
            } catch (NumberFormatException e) {
                throw DbException.get(ErrorCode.PARSE_ERROR_1, this.string);
            }
        }

        int readNanos(int i, boolean z) {
            int i2;
            char charAt;
            int i3 = this.offset;
            int i4 = 0;
            int i5 = 100000000;
            if (z) {
                i2 = i3;
                while (i2 < this.length && (charAt = this.string.charAt(i2)) >= '0' && charAt <= '9') {
                    i4 += i5 * (charAt - '0');
                    i5 /= 10;
                    i2++;
                }
                if (i3 == i2) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, this.string);
                }
            } else {
                i2 = i3 + i;
                if (i2 > this.length) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, this.string);
                }
                while (i3 < i2) {
                    char charAt2 = this.string.charAt(i3);
                    if (charAt2 < '0' || charAt2 > '9') {
                        throw DbException.get(ErrorCode.PARSE_ERROR_1, this.string);
                    }
                    i4 += i5 * (charAt2 - '0');
                    i5 /= 10;
                    i3++;
                }
            }
            this.offset = i2;
            return i4;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/DateTimeTemplate$Part.class */
    public static abstract class Part {
        abstract int type();

        abstract void format(StringBuilder sb, long j, long j2, int i);

        abstract void parse(int[] iArr, Scanner scanner, boolean z, int i);

        Part() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/DateTimeTemplate$Delimiter.class */
    public static final class Delimiter extends Part {
        static final Delimiter MINUS_SIGN = new Delimiter('-');
        static final Delimiter PERIOD = new Delimiter('.');
        static final Delimiter SOLIDUS = new Delimiter('/');
        static final Delimiter COMMA = new Delimiter(',');
        static final Delimiter APOSTROPHE = new Delimiter('\'');
        static final Delimiter SEMICOLON = new Delimiter(';');
        static final Delimiter COLON = new Delimiter(':');
        static final Delimiter SPACE = new Delimiter(' ');
        private final char delimiter;

        private Delimiter(char c) {
            this.delimiter = c;
        }

        @Override // org.h2.util.DateTimeTemplate.Part
        int type() {
            return 15;
        }

        @Override // org.h2.util.DateTimeTemplate.Part
        public void format(StringBuilder sb, long j, long j2, int i) {
            sb.append(this.delimiter);
        }

        @Override // org.h2.util.DateTimeTemplate.Part
        public void parse(int[] iArr, Scanner scanner, boolean z, int i) {
            scanner.readChar(this.delimiter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/DateTimeTemplate$Field.class */
    public static final class Field extends Part {
        private static final Field[] FF;
        private final int type;
        private final int digits;
        static final Field Y = new Field(0, 1);
        static final Field YY = new Field(0, 2);
        static final Field YYY = new Field(0, 3);
        static final Field YYYY = new Field(0, 4);
        static final Field RR = new Field(1, 2);
        static final Field RRRR = new Field(1, 4);
        static final Field MM = new Field(2, 2);
        static final Field DD = new Field(3, 2);
        static final Field DDD = new Field(4, 3);
        static final Field HH12 = new Field(5, 2);
        static final Field HH24 = new Field(6, 2);
        static final Field MI = new Field(7, 2);
        static final Field SS = new Field(8, 2);
        static final Field SSSSS = new Field(9, 5);
        static final Field AM_PM = new Field(11, 4);
        static final Field TZH = new Field(12, 2);
        static final Field TZM = new Field(13, 2);
        static final Field TZS = new Field(14, 2);

        static {
            Field[] fieldArr = new Field[9];
            int i = 0;
            while (i < 9) {
                int i2 = i;
                i++;
                fieldArr[i2] = new Field(10, i);
            }
            FF = fieldArr;
        }

        static Field ff(int i) {
            return FF[i - 1];
        }

        Field(int i, int i2) {
            this.type = i;
            this.digits = i2;
        }

        @Override // org.h2.util.DateTimeTemplate.Part
        int type() {
            return this.type;
        }

        @Override // org.h2.util.DateTimeTemplate.Part
        void format(StringBuilder sb, long j, long j2, int i) {
            switch (this.type) {
                case 0:
                case 1:
                    int yearFromDateValue = DateTimeUtils.yearFromDateValue(j);
                    if (yearFromDateValue < 0) {
                        sb.append('-');
                        yearFromDateValue = -yearFromDateValue;
                    }
                    switch (this.digits) {
                        case 1:
                            yearFromDateValue %= 10;
                            break;
                        case 2:
                            yearFromDateValue %= 100;
                            break;
                        case 3:
                            yearFromDateValue %= 1000;
                            break;
                    }
                    formatLast(sb, yearFromDateValue, this.digits);
                    return;
                case 2:
                    StringUtils.appendTwoDigits(sb, DateTimeUtils.monthFromDateValue(j));
                    return;
                case 3:
                    StringUtils.appendTwoDigits(sb, DateTimeUtils.dayFromDateValue(j));
                    return;
                case 4:
                    StringUtils.appendZeroPadded(sb, 3, DateTimeUtils.getDayOfYear(j));
                    return;
                case 5:
                    int i2 = (int) (j2 / DateTimeUtils.NANOS_PER_HOUR);
                    if (i2 == 0) {
                        i2 = 12;
                    } else if (i2 > 12) {
                        i2 -= 12;
                    }
                    StringUtils.appendTwoDigits(sb, i2);
                    return;
                case 6:
                    StringUtils.appendTwoDigits(sb, (int) (j2 / DateTimeUtils.NANOS_PER_HOUR));
                    return;
                case 7:
                    StringUtils.appendTwoDigits(sb, (int) ((j2 / DateTimeUtils.NANOS_PER_MINUTE) % 60));
                    return;
                case 8:
                    StringUtils.appendTwoDigits(sb, (int) ((j2 / DateTimeUtils.NANOS_PER_SECOND) % 60));
                    return;
                case 9:
                    StringUtils.appendZeroPadded(sb, 5, (int) (j2 / DateTimeUtils.NANOS_PER_SECOND));
                    return;
                case 10:
                    formatLast(sb, ((int) (j2 % DateTimeUtils.NANOS_PER_SECOND)) / DateTimeUtils.FRACTIONAL_SECONDS_TABLE[this.digits], this.digits);
                    return;
                case 11:
                    sb.append(((int) (j2 / DateTimeUtils.NANOS_PER_HOUR)) < 12 ? "A.M." : "P.M.");
                    return;
                case 12:
                    int i3 = i / 3600;
                    if (i >= 0) {
                        sb.append('+');
                    } else {
                        i3 = -i3;
                        sb.append('-');
                    }
                    StringUtils.appendTwoDigits(sb, i3);
                    return;
                case 13:
                    StringUtils.appendTwoDigits(sb, Math.abs((i % 3600) / 60));
                    return;
                case 14:
                    StringUtils.appendTwoDigits(sb, Math.abs(i % 60));
                    return;
                default:
                    return;
            }
        }

        private static void formatLast(StringBuilder sb, int i, int i2) {
            if (i2 == 2) {
                StringUtils.appendTwoDigits(sb, i);
            } else {
                StringUtils.appendZeroPadded(sb, i2, i);
            }
        }

        @Override // org.h2.util.DateTimeTemplate.Part
        void parse(int[] iArr, Scanner scanner, boolean z, int i) {
            int i2;
            switch (this.type) {
                case 0:
                case 1:
                    boolean readCharIf = scanner.readCharIf('-');
                    if (!readCharIf) {
                        scanner.readCharIf('+');
                    }
                    int readPositiveInt = scanner.readPositiveInt(this.digits, z);
                    if (readCharIf) {
                        if (this.digits < 4 || this.type == 1) {
                            throw DbException.get(ErrorCode.PARSE_ERROR_1, scanner.string);
                        }
                        readPositiveInt = -readPositiveInt;
                    } else if (this.digits < 4) {
                        if (this.digits == 1) {
                            if (readPositiveInt > 9) {
                                throw DbException.get(ErrorCode.PARSE_ERROR_1, scanner.string);
                            }
                            readPositiveInt += (i / 10) * 10;
                        } else if (this.digits == 2) {
                            if (readPositiveInt > 99) {
                                throw DbException.get(ErrorCode.PARSE_ERROR_1, scanner.string);
                            }
                            readPositiveInt += (i / 100) * 100;
                            if (this.type == 1) {
                                if (readPositiveInt > i + 50) {
                                    readPositiveInt -= 100;
                                } else if (readPositiveInt < i - 49) {
                                    int i3 = i + 100;
                                }
                            }
                        } else if (this.digits == 3) {
                            if (readPositiveInt > 999) {
                                throw DbException.get(ErrorCode.PARSE_ERROR_1, scanner.string);
                            }
                            readPositiveInt += (i / 1000) * 1000;
                        }
                    }
                    iArr[this.type] = readPositiveInt;
                    return;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 13:
                case 14:
                    iArr[this.type] = scanner.readPositiveInt(this.digits, z);
                    return;
                case 10:
                    iArr[10] = scanner.readNanos(this.digits, z);
                    return;
                case 11:
                    if (scanner.readCharIf('A')) {
                        i2 = 0;
                    } else {
                        scanner.readChar('P');
                        i2 = 1;
                    }
                    scanner.readChar('.');
                    scanner.readChar('M');
                    scanner.readChar('.');
                    iArr[11] = i2;
                    return;
                case 12:
                    boolean readCharIf2 = scanner.readCharIf('-');
                    if (!readCharIf2 && !scanner.readCharIf('+')) {
                        scanner.readChar(' ');
                    }
                    int readPositiveInt2 = scanner.readPositiveInt(this.digits, z);
                    if (readPositiveInt2 > 18) {
                        throw DbException.get(ErrorCode.PARSE_ERROR_1, scanner.string);
                    }
                    iArr[12] = readCharIf2 ? readPositiveInt2 == 0 ? -100 : -readPositiveInt2 : readPositiveInt2;
                    return;
                default:
                    return;
            }
        }
    }

    public static DateTimeTemplate of(String str) {
        DateTimeTemplate dateTimeTemplate;
        synchronized (CACHE) {
            DateTimeTemplate dateTimeTemplate2 = CACHE.get(str);
            if (dateTimeTemplate2 != null) {
                return dateTimeTemplate2;
            }
            DateTimeTemplate parseTemplate = parseTemplate(str);
            synchronized (CACHE) {
                dateTimeTemplate = (DateTimeTemplate) CACHE.putIfAbsent(str, parseTemplate);
            }
            return dateTimeTemplate != null ? dateTimeTemplate : parseTemplate;
        }
    }

    private static DateTimeTemplate parseTemplate(String str) {
        Part part;
        ArrayList arrayList = new ArrayList();
        Scanner scanner = new Scanner(str);
        int i = 0;
        while (true) {
            int readChar = scanner.readChar();
            if (readChar >= 0) {
                switch (readChar) {
                    case 32:
                        part = Delimiter.SPACE;
                        break;
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 48:
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case 56:
                    case 57:
                    case 60:
                    case 61:
                    case 62:
                    case 63:
                    case 64:
                    case 66:
                    case 67:
                    case 69:
                    case 71:
                    case 73:
                    case 74:
                    case 75:
                    case 76:
                    case 78:
                    case 79:
                    case 81:
                    case 85:
                    case 86:
                    case 87:
                    case 88:
                    default:
                        throw DbException.get(ErrorCode.PARSE_ERROR_1, str);
                    case 39:
                        part = Delimiter.APOSTROPHE;
                        break;
                    case 44:
                        part = Delimiter.COMMA;
                        break;
                    case 45:
                        part = Delimiter.MINUS_SIGN;
                        break;
                    case 46:
                        part = Delimiter.PERIOD;
                        break;
                    case 47:
                        part = Delimiter.SOLIDUS;
                        break;
                    case 58:
                        part = Delimiter.COLON;
                        break;
                    case 59:
                        part = Delimiter.SEMICOLON;
                        break;
                    case 65:
                    case 80:
                        scanner.readChar('.');
                        scanner.readChar('M');
                        scanner.readChar('.');
                        i = checkUsed(i, 11, str);
                        part = Field.AM_PM;
                        break;
                    case 68:
                        scanner.readChar('D');
                        if (scanner.readCharIf('D')) {
                            i = checkUsed(i, 4, str);
                            part = Field.DDD;
                            break;
                        } else {
                            i = checkUsed(i, 3, str);
                            part = Field.DD;
                            break;
                        }
                    case 70:
                        scanner.readChar('F');
                        int readChar2 = scanner.readChar();
                        if (readChar2 >= 49 && readChar2 <= 57) {
                            i = checkUsed(i, 10, str);
                            part = Field.ff(readChar2 - 48);
                            break;
                        }
                        break;
                    case 72:
                        scanner.readChar('H');
                        if (scanner.readCharIf('2')) {
                            scanner.readChar('4');
                            i = checkUsed(i, 6, str);
                            part = Field.HH24;
                            break;
                        } else {
                            if (scanner.readCharIf('1')) {
                                scanner.readChar('2');
                            }
                            i = checkUsed(i, 5, str);
                            part = Field.HH12;
                            break;
                        }
                    case 77:
                        if (scanner.readCharIf('I')) {
                            i = checkUsed(i, 7, str);
                            part = Field.MI;
                            break;
                        } else {
                            scanner.readChar('M');
                            i = checkUsed(i, 2, str);
                            part = Field.MM;
                            break;
                        }
                    case 82:
                        i = checkUsed(i, 0, str);
                        scanner.readChar('R');
                        if (scanner.readCharIf('R')) {
                            scanner.readChar('R');
                            part = Field.RRRR;
                            break;
                        } else {
                            part = Field.RR;
                            break;
                        }
                    case 83:
                        scanner.readChar('S');
                        if (scanner.readCharIf('S')) {
                            scanner.readChar('S');
                            scanner.readChar('S');
                            i = checkUsed(i, 9, str);
                            part = Field.SSSSS;
                            break;
                        } else {
                            i = checkUsed(i, 8, str);
                            part = Field.SS;
                            break;
                        }
                    case 84:
                        scanner.readChar('Z');
                        if (scanner.readCharIf('H')) {
                            i = checkUsed(i, 12, str);
                            part = Field.TZH;
                            break;
                        } else if (scanner.readCharIf('M')) {
                            i = checkUsed(i, 13, str);
                            part = Field.TZM;
                            break;
                        } else {
                            scanner.readChar('S');
                            i = checkUsed(i, 14, str);
                            part = Field.TZS;
                            break;
                        }
                    case 89:
                        i = checkUsed(i, 0, str);
                        if (scanner.readCharIf('Y')) {
                            if (scanner.readCharIf('Y')) {
                                if (!scanner.readCharIf('Y')) {
                                    part = Field.YYY;
                                    break;
                                } else {
                                    part = Field.YYYY;
                                    break;
                                }
                            } else {
                                part = Field.YY;
                                break;
                            }
                        } else {
                            part = Field.Y;
                            break;
                        }
                }
                arrayList.add(part);
            } else {
                if ((i & 16) == 0 || (i & 12) == 0) {
                    if (((i & 32) != 0) == ((i & 2048) != 0) && (((i & 64) == 0 || (i & 32) == 0) && (((i & 512) == 0 || (i & 480) == 0) && (((i & 16384) == 0 || (i & 8192) != 0) && ((i & 8192) == 0 || (i & 4096) != 0))))) {
                        return new DateTimeTemplate((Part[]) arrayList.toArray(new Part[0]), (i & 29) != 0, (i & 3040) != 0, (i & 28672) != 0);
                    }
                }
                throw DbException.get(ErrorCode.PARSE_ERROR_1, str);
            }
        }
        throw DbException.get(ErrorCode.PARSE_ERROR_1, str);
    }

    private static int checkUsed(int i, int i2, String str) {
        int i3 = i | (1 << i2);
        if (i == i3) {
            throw DbException.get(ErrorCode.PARSE_ERROR_1, str);
        }
        return i3;
    }

    private DateTimeTemplate(Part[] partArr, boolean z, boolean z2, boolean z3) {
        this.parts = partArr;
        this.containsDate = z;
        this.containsTime = z2;
        this.containsTimeZone = z3;
    }

    public String format(Value value) {
        long dateValue;
        long timeNanos;
        int timeZoneOffsetSeconds;
        switch (value.getValueType()) {
            case 0:
                return null;
            case 17:
                if (this.containsTime || this.containsTimeZone) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, "time or time zone fields with DATE");
                }
                dateValue = ((ValueDate) value).getDateValue();
                timeNanos = 0;
                timeZoneOffsetSeconds = 0;
                break;
                break;
            case 18:
                if (this.containsDate || this.containsTimeZone) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, "date or time zone fields with TIME");
                }
                dateValue = 0;
                timeNanos = ((ValueTime) value).getNanos();
                timeZoneOffsetSeconds = 0;
                break;
                break;
            case 19:
                if (this.containsDate) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, "date fields with TIME WITH TIME ZONE");
                }
                ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone) value;
                dateValue = 0;
                timeNanos = valueTimeTimeZone.getNanos();
                timeZoneOffsetSeconds = valueTimeTimeZone.getTimeZoneOffsetSeconds();
                break;
            case 20:
                if (this.containsTimeZone) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, "time zone fields with TIMESTAMP");
                }
                ValueTimestamp valueTimestamp = (ValueTimestamp) value;
                dateValue = valueTimestamp.getDateValue();
                timeNanos = valueTimestamp.getTimeNanos();
                timeZoneOffsetSeconds = 0;
                break;
            case 21:
                ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone) value;
                dateValue = valueTimestampTimeZone.getDateValue();
                timeNanos = valueTimestampTimeZone.getTimeNanos();
                timeZoneOffsetSeconds = valueTimestampTimeZone.getTimeZoneOffsetSeconds();
                break;
            default:
                throw DbException.getUnsupportedException(value.getType().getTraceSQL());
        }
        StringBuilder sb = new StringBuilder();
        for (Part part : this.parts) {
            part.format(sb, dateValue, timeNanos, timeZoneOffsetSeconds);
        }
        return sb.toString();
    }

    public Value parse(String str, TypeInfo typeInfo, CastDataProvider castDataProvider) {
        switch (typeInfo.getValueType()) {
            case 17:
                if (this.containsTime || this.containsTimeZone) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, "time or time zone fields with DATE");
                }
                int[] yearMonth = yearMonth(castDataProvider);
                return ValueDate.fromDateValue(constructDate(parse(str, yearMonth[0]), yearMonth));
            case 18:
                if (this.containsDate || this.containsTimeZone) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, "date or time zone fields with TIME");
                }
                return ValueTime.fromNanos(constructTime(parse(str, 0)));
            case 19:
                if (this.containsDate) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, "date fields with TIME WITH TIME ZONE");
                }
                int[] parse = parse(str, 0);
                return ValueTimeTimeZone.fromNanos(constructTime(parse), constructOffset(parse));
            case 20:
                if (this.containsTimeZone) {
                    throw DbException.get(ErrorCode.PARSE_ERROR_1, "time zone fields with TIMESTAMP");
                }
                int[] yearMonth2 = yearMonth(castDataProvider);
                int[] parse2 = parse(str, yearMonth2[0]);
                return ValueTimestamp.fromDateValueAndNanos(constructDate(parse2, yearMonth2), constructTime(parse2));
            case 21:
                int[] yearMonth3 = yearMonth(castDataProvider);
                int[] parse3 = parse(str, yearMonth3[0]);
                return ValueTimestampTimeZone.fromDateValueAndNanos(constructDate(parse3, yearMonth3), constructTime(parse3), constructOffset(parse3));
            default:
                throw DbException.getUnsupportedException(typeInfo.getTraceSQL());
        }
    }

    private static int[] yearMonth(CastDataProvider castDataProvider) {
        long dateValue = castDataProvider.currentTimestamp().getDateValue();
        return new int[]{DateTimeUtils.yearFromDateValue(dateValue), DateTimeUtils.monthFromDateValue(dateValue)};
    }

    private int[] parse(String str, int i) {
        int[] iArr = new int[15];
        Arrays.fill(iArr, Integer.MIN_VALUE);
        Scanner scanner = new Scanner(str);
        int i2 = 0;
        int length = this.parts.length - 1;
        while (i2 <= length) {
            Part part = this.parts[i2];
            part.parse(iArr, scanner, !(i2 != 0 && ((1 << part.type()) & 6144) == 0 && ((1 << this.parts[i2 - 1].type()) & 34816) == 0) && (i2 == length || part.type() == 11 || ((1 << this.parts[i2 + 1].type()) & 38912) != 0), i);
            i2++;
        }
        return iArr;
    }

    private static long constructDate(int[] iArr, int[] iArr2) {
        int i = iArr[0];
        if (i == Integer.MIN_VALUE) {
            i = iArr[1];
        }
        if (i == Integer.MIN_VALUE) {
            i = iArr2[0];
        }
        int i2 = iArr[4];
        if (i2 != Integer.MIN_VALUE) {
            if (i2 >= 1) {
                if (i2 <= (DateTimeUtils.isLeapYear(i) ? 366 : 365)) {
                    return DateTimeUtils.dateValueFromAbsoluteDay((DateTimeUtils.absoluteDayFromYear(i) + i2) - 1);
                }
            }
            throw DbException.get(ErrorCode.PARSE_ERROR_1, "Day of year " + i2);
        }
        int i3 = iArr[2];
        if (i3 == Integer.MIN_VALUE) {
            i3 = iArr2[1];
        }
        int i4 = iArr[3];
        if (i4 == Integer.MIN_VALUE) {
            i4 = 1;
        }
        if (!DateTimeUtils.isValidDate(i, i3, i4)) {
            throw DbException.get(ErrorCode.PARSE_ERROR_1, "Invalid date, year=" + i + ", month=" + i3 + ", day=" + i4);
        }
        return DateTimeUtils.dateValue(i, i3, i4);
    }

    private static long constructTime(int[] iArr) {
        int i = iArr[9];
        if (i == Integer.MIN_VALUE) {
            int i2 = iArr[6];
            if (i2 == Integer.MIN_VALUE) {
                int i3 = iArr[5];
                if (i3 == Integer.MIN_VALUE) {
                    i2 = 0;
                } else {
                    if (i3 < 1 || i3 > 12) {
                        throw DbException.get(ErrorCode.PARSE_ERROR_1, "Hour(12) " + i3);
                    }
                    if (i3 == 12) {
                        i3 = 0;
                    }
                    i2 = i3 + (iArr[11] * 12);
                }
            } else if (i2 < 0 || i2 > 23) {
                throw DbException.get(ErrorCode.PARSE_ERROR_1, "Hour(24) " + i2);
            }
            int i4 = iArr[7];
            if (i4 == Integer.MIN_VALUE) {
                i4 = 0;
            } else if (i4 < 0 || i4 > 59) {
                throw DbException.get(ErrorCode.PARSE_ERROR_1, "Minute " + i4);
            }
            int i5 = iArr[8];
            if (i5 == Integer.MIN_VALUE) {
                i5 = 0;
            } else if (i5 < 0 || i5 > 59) {
                throw DbException.get(ErrorCode.PARSE_ERROR_1, "Second of minute " + i5);
            }
            i = (((i2 * 60) + i4) * 60) + i5;
        } else if (i < 0 || i >= DateTimeUtils.SECONDS_PER_DAY) {
            throw DbException.get(ErrorCode.PARSE_ERROR_1, "Second of day " + i);
        }
        int i6 = iArr[10];
        if (i6 == Integer.MIN_VALUE) {
            i6 = 0;
        }
        return (i * DateTimeUtils.NANOS_PER_SECOND) + i6;
    }

    private static int constructOffset(int[] iArr) {
        int i = iArr[12];
        if (i == Integer.MIN_VALUE) {
            return 0;
        }
        boolean z = i < 0;
        if (z) {
            if (i == -100) {
                i = 0;
            } else {
                i = -i;
            }
        }
        int i2 = iArr[13];
        if (i2 == Integer.MIN_VALUE) {
            i2 = 0;
        } else if (i2 > 59) {
            throw DbException.get(ErrorCode.PARSE_ERROR_1, "Time zone minute " + i2);
        }
        int i3 = iArr[14];
        if (i3 == Integer.MIN_VALUE) {
            i3 = 0;
        } else if (i3 > 59) {
            throw DbException.get(ErrorCode.PARSE_ERROR_1, "Time zone second " + i3);
        }
        int i4 = (((i * 60) + i2) * 60) + i3;
        if (i4 > 64800) {
            throw DbException.get(ErrorCode.PARSE_ERROR_1, "Time zone offset is too large");
        }
        return z ? -i4 : i4;
    }
}
