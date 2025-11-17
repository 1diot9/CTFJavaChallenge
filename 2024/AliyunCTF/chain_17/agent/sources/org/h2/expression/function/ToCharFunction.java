package org.h2.expression.function;

import cn.hutool.core.text.CharSequenceUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import org.apache.juli.JdkLoggerFormatter;
import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.DateTimeUtils;
import org.h2.util.StringUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarchar;
import org.slf4j.Marker;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/ToCharFunction.class */
public final class ToCharFunction extends FunctionN {
    public static final int JULIAN_EPOCH = -2440588;
    private static final int[] ROMAN_VALUES = {1000, JdkLoggerFormatter.LOG_LEVEL_WARN, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] ROMAN_NUMERALS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
    public static final int MONTHS = 0;
    public static final int SHORT_MONTHS = 1;
    public static final int WEEKDAYS = 2;
    public static final int SHORT_WEEKDAYS = 3;
    static final int AM_PM = 4;
    private static volatile String[][] NAMES;

    public static String toChar(BigDecimal bigDecimal, String str, String str2) {
        Integer num;
        String upperEnglish = str != null ? StringUtils.toUpperEnglish(str) : null;
        if (upperEnglish == null || upperEnglish.equals("TM") || upperEnglish.equals("TM9")) {
            String plainString = bigDecimal.toPlainString();
            return plainString.startsWith("0.") ? plainString.substring(1) : plainString;
        }
        if (upperEnglish.equals("TME")) {
            int precision = (bigDecimal.precision() - bigDecimal.scale()) - 1;
            return bigDecimal.movePointLeft(precision).toPlainString() + "E" + (precision < 0 ? '-' : '+') + (Math.abs(precision) < 10 ? CustomBooleanEditor.VALUE_0 : "") + Math.abs(precision);
        }
        if (upperEnglish.equals("RN")) {
            boolean startsWith = str.startsWith("r");
            String pad = StringUtils.pad(toRomanNumeral(bigDecimal.intValue()), 15, CharSequenceUtil.SPACE, false);
            return startsWith ? pad.toLowerCase() : pad;
        }
        if (upperEnglish.equals("FMRN")) {
            boolean z = str.charAt(2) == 'r';
            String romanNumeral = toRomanNumeral(bigDecimal.intValue());
            return z ? romanNumeral.toLowerCase() : romanNumeral;
        }
        if (upperEnglish.endsWith("X")) {
            return toHex(bigDecimal, str);
        }
        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        char groupingSeparator = decimalFormatSymbols.getGroupingSeparator();
        char decimalSeparator = decimalFormatSymbols.getDecimalSeparator();
        boolean startsWith2 = upperEnglish.startsWith("S");
        if (startsWith2) {
            str = str.substring(1);
        }
        boolean endsWith = upperEnglish.endsWith("S");
        if (endsWith) {
            str = str.substring(0, str.length() - 1);
        }
        boolean endsWith2 = upperEnglish.endsWith("MI");
        if (endsWith2) {
            str = str.substring(0, str.length() - 2);
        }
        boolean endsWith3 = upperEnglish.endsWith("PR");
        if (endsWith3) {
            str = str.substring(0, str.length() - 2);
        }
        int indexOf = upperEnglish.indexOf(86);
        if (indexOf >= 0) {
            int i = 0;
            for (int i2 = indexOf + 1; i2 < str.length(); i2++) {
                char charAt = str.charAt(i2);
                if (charAt == '0' || charAt == '9') {
                    i++;
                }
            }
            bigDecimal = bigDecimal.movePointRight(i);
            str = str.substring(0, indexOf) + str.substring(indexOf + 1);
        }
        if (str.endsWith("EEEE")) {
            num = Integer.valueOf((bigDecimal.precision() - bigDecimal.scale()) - 1);
            bigDecimal = bigDecimal.movePointLeft(num.intValue());
            str = str.substring(0, str.length() - 4);
        } else {
            num = null;
        }
        int i3 = 1;
        boolean z2 = !upperEnglish.startsWith("FM");
        if (!z2) {
            str = str.substring(2);
        }
        String replaceAll = str.replaceAll("[Bb]", "");
        int findDecimalSeparator = findDecimalSeparator(replaceAll);
        int calculateScale = calculateScale(replaceAll, findDecimalSeparator);
        int scale = bigDecimal.scale();
        if (calculateScale < scale) {
            bigDecimal = bigDecimal.setScale(calculateScale, RoundingMode.HALF_UP);
        } else if (scale < 0) {
            bigDecimal = bigDecimal.setScale(0);
        }
        for (int indexOf2 = replaceAll.indexOf(48); indexOf2 >= 0 && indexOf2 < findDecimalSeparator; indexOf2++) {
            if (replaceAll.charAt(indexOf2) == '9') {
                replaceAll = replaceAll.substring(0, indexOf2) + CustomBooleanEditor.VALUE_0 + replaceAll.substring(indexOf2 + 1);
            }
        }
        StringBuilder sb = new StringBuilder();
        String str3 = (bigDecimal.abs().compareTo(BigDecimal.ONE) < 0 ? zeroesAfterDecimalSeparator(bigDecimal) : "") + bigDecimal.unscaledValue().abs().toString();
        int length = (str3.length() - bigDecimal.scale()) - 1;
        for (int i4 = findDecimalSeparator - 1; i4 >= 0; i4--) {
            char charAt2 = replaceAll.charAt(i4);
            i3++;
            if (charAt2 == '9' || charAt2 == '0') {
                if (length >= 0) {
                    sb.insert(0, str3.charAt(length));
                    length--;
                } else if (charAt2 == '0' && num == null) {
                    sb.insert(0, '0');
                }
            } else if (charAt2 == ',') {
                if (length >= 0 || (i4 > 0 && replaceAll.charAt(i4 - 1) == '0')) {
                    sb.insert(0, charAt2);
                }
            } else if (charAt2 == 'G' || charAt2 == 'g') {
                if (length >= 0 || (i4 > 0 && replaceAll.charAt(i4 - 1) == '0')) {
                    sb.insert(0, groupingSeparator);
                }
            } else if (charAt2 == 'C' || charAt2 == 'c') {
                sb.insert(0, getCurrency().getCurrencyCode());
                i3 += 6;
            } else if (charAt2 == 'L' || charAt2 == 'l' || charAt2 == 'U' || charAt2 == 'u') {
                sb.insert(0, getCurrency().getSymbol());
                i3 += 9;
            } else {
                if (charAt2 != '$') {
                    throw DbException.get(ErrorCode.INVALID_TO_CHAR_FORMAT, str);
                }
                sb.insert(0, getCurrency().getSymbol());
            }
        }
        if (length >= 0) {
            return StringUtils.pad("", replaceAll.length() + 1, "#", true);
        }
        if (findDecimalSeparator < replaceAll.length()) {
            i3++;
            char charAt3 = replaceAll.charAt(findDecimalSeparator);
            if (charAt3 == 'd' || charAt3 == 'D') {
                sb.append(decimalSeparator);
            } else {
                sb.append(charAt3);
            }
            int length2 = str3.length() - bigDecimal.scale();
            for (int i5 = findDecimalSeparator + 1; i5 < replaceAll.length(); i5++) {
                char charAt4 = replaceAll.charAt(i5);
                i3++;
                if (charAt4 != '9' && charAt4 != '0') {
                    throw DbException.get(ErrorCode.INVALID_TO_CHAR_FORMAT, str);
                }
                if (length2 < str3.length()) {
                    sb.append(str3.charAt(length2));
                    length2++;
                } else if (charAt4 == '0' || z2) {
                    sb.append('0');
                }
            }
        }
        addSign(sb, bigDecimal.signum(), startsWith2, endsWith, endsWith2, endsWith3, z2);
        if (num != null) {
            sb.append('E');
            sb.append(num.intValue() < 0 ? '-' : '+');
            sb.append(Math.abs(num.intValue()) < 10 ? CustomBooleanEditor.VALUE_0 : "");
            sb.append(Math.abs(num.intValue()));
        }
        if (z2) {
            if (num != null) {
                sb.insert(0, ' ');
            } else {
                while (sb.length() < i3) {
                    sb.insert(0, ' ');
                }
            }
        }
        return sb.toString();
    }

    private static Currency getCurrency() {
        Locale locale = Locale.getDefault();
        return Currency.getInstance(locale.getCountry().length() == 2 ? locale : Locale.US);
    }

    private static String zeroesAfterDecimalSeparator(BigDecimal bigDecimal) {
        String plainString = bigDecimal.toPlainString();
        int indexOf = plainString.indexOf(46);
        if (indexOf < 0) {
            return "";
        }
        int i = indexOf + 1;
        boolean z = true;
        int length = plainString.length();
        while (true) {
            if (i >= length) {
                break;
            }
            if (plainString.charAt(i) == '0') {
                i++;
            } else {
                z = false;
                break;
            }
        }
        char[] cArr = new char[z ? (length - indexOf) - 1 : (i - 1) - indexOf];
        Arrays.fill(cArr, '0');
        return String.valueOf(cArr);
    }

    private static void addSign(StringBuilder sb, int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        String str;
        if (z4) {
            if (i < 0) {
                sb.insert(0, '<');
                sb.append('>');
                return;
            } else {
                if (z5) {
                    sb.insert(0, ' ');
                    sb.append(' ');
                    return;
                }
                return;
            }
        }
        if (i == 0) {
            str = "";
        } else if (i < 0) {
            str = "-";
        } else if (z || z2) {
            str = Marker.ANY_NON_NULL_MARKER;
        } else if (z5) {
            str = CharSequenceUtil.SPACE;
        } else {
            str = "";
        }
        if (z3 || z2) {
            sb.append(str);
        } else {
            sb.insert(0, str);
        }
    }

    private static int findDecimalSeparator(String str) {
        int indexOf = str.indexOf(46);
        if (indexOf == -1) {
            indexOf = str.indexOf(68);
            if (indexOf == -1) {
                indexOf = str.indexOf(100);
                if (indexOf == -1) {
                    indexOf = str.length();
                }
            }
        }
        return indexOf;
    }

    private static int calculateScale(String str, int i) {
        int i2 = 0;
        for (int i3 = i; i3 < str.length(); i3++) {
            char charAt = str.charAt(i3);
            if (charAt == '0' || charAt == '9') {
                i2++;
            }
        }
        return i2;
    }

    private static String toRomanNumeral(int i) {
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < ROMAN_VALUES.length; i2++) {
            int i3 = ROMAN_VALUES[i2];
            String str = ROMAN_NUMERALS[i2];
            while (i >= i3) {
                sb.append(str);
                i -= i3;
            }
        }
        return sb.toString();
    }

    private static String toHex(BigDecimal bigDecimal, String str) {
        boolean z = !StringUtils.toUpperEnglish(str).startsWith("FM");
        boolean z2 = !str.contains("x");
        boolean startsWith = str.startsWith(CustomBooleanEditor.VALUE_0);
        int i = 0;
        for (int i2 = 0; i2 < str.length(); i2++) {
            char charAt = str.charAt(i2);
            if (charAt == '0' || charAt == 'X' || charAt == 'x') {
                i++;
            }
        }
        String hexString = Integer.toHexString(bigDecimal.setScale(0, RoundingMode.HALF_UP).intValue());
        if (i < hexString.length()) {
            hexString = StringUtils.pad("", i + 1, "#", true);
        } else {
            if (z2) {
                hexString = StringUtils.toUpperEnglish(hexString);
            }
            if (startsWith) {
                hexString = StringUtils.pad(hexString, i, CustomBooleanEditor.VALUE_0, false);
            }
            if (z) {
                hexString = StringUtils.pad(hexString, str.length() + 1, CharSequenceUtil.SPACE, false);
            }
        }
        return hexString;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v5, types: [java.lang.String[]] */
    public static String[] getDateNames(int i) {
        String[][] strArr = NAMES;
        if (strArr == null) {
            strArr = new String[5];
            DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance();
            strArr[0] = dateFormatSymbols.getMonths();
            String[] shortMonths = dateFormatSymbols.getShortMonths();
            for (int i2 = 0; i2 < 12; i2++) {
                String str = shortMonths[i2];
                if (str.endsWith(".")) {
                    shortMonths[i2] = str.substring(0, str.length() - 1);
                }
            }
            strArr[1] = shortMonths;
            strArr[2] = dateFormatSymbols.getWeekdays();
            strArr[3] = dateFormatSymbols.getShortWeekdays();
            strArr[4] = dateFormatSymbols.getAmPmStrings();
            NAMES = strArr;
        }
        return strArr[i];
    }

    public static void clearNames() {
        NAMES = (String[][]) null;
    }

    private static String getTimeZone(SessionLocal sessionLocal, Value value, boolean z) {
        if (value instanceof ValueTimestampTimeZone) {
            return DateTimeUtils.timeZoneNameFromOffsetSeconds(((ValueTimestampTimeZone) value).getTimeZoneOffsetSeconds());
        }
        if (value instanceof ValueTimeTimeZone) {
            return DateTimeUtils.timeZoneNameFromOffsetSeconds(((ValueTimeTimeZone) value).getTimeZoneOffsetSeconds());
        }
        TimeZoneProvider currentTimeZone = sessionLocal.currentTimeZone();
        if (z) {
            ValueTimestamp valueTimestamp = (ValueTimestamp) value.convertTo(TypeInfo.TYPE_TIMESTAMP, sessionLocal);
            return currentTimeZone.getShortId(currentTimeZone.getEpochSecondsFromLocal(valueTimestamp.getDateValue(), valueTimestamp.getTimeNanos()));
        }
        return currentTimeZone.getId();
    }

    public static String toCharDateTime(SessionLocal sessionLocal, Value value, String str, String str2) {
        long[] dateAndTimeFromValue = DateTimeUtils.dateAndTimeFromValue(value, sessionLocal);
        long j = dateAndTimeFromValue[0];
        long j2 = dateAndTimeFromValue[1];
        int yearFromDateValue = DateTimeUtils.yearFromDateValue(j);
        int monthFromDateValue = DateTimeUtils.monthFromDateValue(j);
        int dayFromDateValue = DateTimeUtils.dayFromDateValue(j);
        int abs = Math.abs(yearFromDateValue);
        int i = (int) (j2 / DateTimeUtils.NANOS_PER_SECOND);
        int i2 = (int) (j2 - (i * Constants.MAX_STRING_LENGTH));
        int i3 = i / 60;
        int i4 = i - (i3 * 60);
        int i5 = i3 / 60;
        int i6 = i3 - (i5 * 60);
        int i7 = ((i5 + 11) % 12) + 1;
        boolean z = i5 < 12;
        if (str == null) {
            str = "DD-MON-YY HH.MI.SS.FF PM";
        }
        StringBuilder sb = new StringBuilder();
        boolean z2 = true;
        int i8 = 0;
        int length = str.length();
        while (i8 < length) {
            Capitalization containsAt = containsAt(str, i8, "A.D.", "B.C.");
            if (containsAt != null) {
                sb.append(containsAt.apply(yearFromDateValue > 0 ? "A.D." : "B.C."));
                i8 += 4;
            } else {
                Capitalization containsAt2 = containsAt(str, i8, "AD", "BC");
                if (containsAt2 != null) {
                    sb.append(containsAt2.apply(yearFromDateValue > 0 ? "AD" : "BC"));
                    i8 += 2;
                } else {
                    Capitalization containsAt3 = containsAt(str, i8, "A.M.", "P.M.");
                    if (containsAt3 != null) {
                        sb.append(containsAt3.apply(z ? "A.M." : "P.M."));
                        i8 += 4;
                    } else {
                        Capitalization containsAt4 = containsAt(str, i8, "AM", "PM");
                        if (containsAt4 != null) {
                            sb.append(containsAt4.apply(z ? "AM" : "PM"));
                            i8 += 2;
                        } else if (containsAt(str, i8, "DL") != null) {
                            sb.append(getDateNames(2)[DateTimeUtils.getSundayDayOfWeek(j)]).append(", ").append(getDateNames(0)[monthFromDateValue - 1]).append(' ').append(dayFromDateValue).append(", ");
                            StringUtils.appendZeroPadded(sb, 4, abs);
                            i8 += 2;
                        } else if (containsAt(str, i8, "DS") != null) {
                            StringUtils.appendTwoDigits(sb, monthFromDateValue).append('/');
                            StringUtils.appendTwoDigits(sb, dayFromDateValue).append('/');
                            StringUtils.appendZeroPadded(sb, 4, abs);
                            i8 += 2;
                        } else if (containsAt(str, i8, "TS") != null) {
                            sb.append(i7).append(':');
                            StringUtils.appendTwoDigits(sb, i6).append(':');
                            StringUtils.appendTwoDigits(sb, i4).append(' ').append(getDateNames(4)[z ? (char) 0 : (char) 1]);
                            i8 += 2;
                        } else if (containsAt(str, i8, "DDD") != null) {
                            sb.append(DateTimeUtils.getDayOfYear(j));
                            i8 += 3;
                        } else if (containsAt(str, i8, "DD") != null) {
                            StringUtils.appendTwoDigits(sb, dayFromDateValue);
                            i8 += 2;
                        } else {
                            Capitalization containsAt5 = containsAt(str, i8, "DY");
                            if (containsAt5 != null) {
                                sb.append(containsAt5.apply(getDateNames(3)[DateTimeUtils.getSundayDayOfWeek(j)]));
                                i8 += 2;
                            } else {
                                Capitalization containsAt6 = containsAt(str, i8, "DAY");
                                if (containsAt6 != null) {
                                    String str3 = getDateNames(2)[DateTimeUtils.getSundayDayOfWeek(j)];
                                    if (z2) {
                                        str3 = StringUtils.pad(str3, "Wednesday".length(), CharSequenceUtil.SPACE, true);
                                    }
                                    sb.append(containsAt6.apply(str3));
                                    i8 += 3;
                                } else if (containsAt(str, i8, "D") != null) {
                                    sb.append(DateTimeUtils.getSundayDayOfWeek(j));
                                    i8++;
                                } else if (containsAt(str, i8, "J") != null) {
                                    sb.append(DateTimeUtils.absoluteDayFromDateValue(j) - (-2440588));
                                    i8++;
                                } else if (containsAt(str, i8, "HH24") != null) {
                                    StringUtils.appendTwoDigits(sb, i5);
                                    i8 += 4;
                                } else if (containsAt(str, i8, "HH12") != null) {
                                    StringUtils.appendTwoDigits(sb, i7);
                                    i8 += 4;
                                } else if (containsAt(str, i8, "HH") != null) {
                                    StringUtils.appendTwoDigits(sb, i7);
                                    i8 += 2;
                                } else if (containsAt(str, i8, "MI") != null) {
                                    StringUtils.appendTwoDigits(sb, i6);
                                    i8 += 2;
                                } else if (containsAt(str, i8, "SSSSS") != null) {
                                    sb.append((int) (j2 / DateTimeUtils.NANOS_PER_SECOND));
                                    i8 += 5;
                                } else if (containsAt(str, i8, "SS") != null) {
                                    StringUtils.appendTwoDigits(sb, i4);
                                    i8 += 2;
                                } else if (containsAt(str, i8, "FF1", "FF2", "FF3", "FF4", "FF5", "FF6", "FF7", "FF8", "FF9") != null) {
                                    StringUtils.appendZeroPadded(sb, str.charAt(i8 + 2) - '0', (int) (i2 * Math.pow(10.0d, r0 - 9)));
                                    i8 += 3;
                                } else if (containsAt(str, i8, "FF") != null) {
                                    StringUtils.appendZeroPadded(sb, 9, i2);
                                    i8 += 2;
                                } else if (containsAt(str, i8, "TZR") != null) {
                                    sb.append(getTimeZone(sessionLocal, value, false));
                                    i8 += 3;
                                } else if (containsAt(str, i8, "TZD") != null) {
                                    sb.append(getTimeZone(sessionLocal, value, true));
                                    i8 += 3;
                                } else if (containsAt(str, i8, "TZH") != null) {
                                    int extractDateTime = DateTimeFunction.extractDateTime(sessionLocal, value, 6);
                                    sb.append(extractDateTime < 0 ? '-' : '+');
                                    StringUtils.appendTwoDigits(sb, Math.abs(extractDateTime));
                                    i8 += 3;
                                } else if (containsAt(str, i8, "TZM") != null) {
                                    StringUtils.appendTwoDigits(sb, Math.abs(DateTimeFunction.extractDateTime(sessionLocal, value, 7)));
                                    i8 += 3;
                                } else if (containsAt(str, i8, "WW") != null) {
                                    StringUtils.appendTwoDigits(sb, ((DateTimeUtils.getDayOfYear(j) - 1) / 7) + 1);
                                    i8 += 2;
                                } else if (containsAt(str, i8, "IW") != null) {
                                    StringUtils.appendTwoDigits(sb, DateTimeUtils.getIsoWeekOfYear(j));
                                    i8 += 2;
                                } else if (containsAt(str, i8, "W") != null) {
                                    sb.append(((dayFromDateValue - 1) / 7) + 1);
                                    i8++;
                                } else if (containsAt(str, i8, "Y,YYY") != null) {
                                    sb.append(new DecimalFormat("#,###").format(abs));
                                    i8 += 5;
                                } else if (containsAt(str, i8, "SYYYY") != null) {
                                    if (yearFromDateValue < 0) {
                                        sb.append('-');
                                    }
                                    StringUtils.appendZeroPadded(sb, 4, abs);
                                    i8 += 5;
                                } else if (containsAt(str, i8, "YYYY", "RRRR") != null) {
                                    StringUtils.appendZeroPadded(sb, 4, abs);
                                    i8 += 4;
                                } else if (containsAt(str, i8, "IYYY") != null) {
                                    StringUtils.appendZeroPadded(sb, 4, Math.abs(DateTimeUtils.getIsoWeekYear(j)));
                                    i8 += 4;
                                } else if (containsAt(str, i8, "YYY") != null) {
                                    StringUtils.appendZeroPadded(sb, 3, abs % 1000);
                                    i8 += 3;
                                } else if (containsAt(str, i8, "IYY") != null) {
                                    StringUtils.appendZeroPadded(sb, 3, Math.abs(DateTimeUtils.getIsoWeekYear(j)) % 1000);
                                    i8 += 3;
                                } else if (containsAt(str, i8, "YY", "RR") != null) {
                                    StringUtils.appendTwoDigits(sb, abs % 100);
                                    i8 += 2;
                                } else if (containsAt(str, i8, "IY") != null) {
                                    StringUtils.appendTwoDigits(sb, Math.abs(DateTimeUtils.getIsoWeekYear(j)) % 100);
                                    i8 += 2;
                                } else if (containsAt(str, i8, "Y") != null) {
                                    sb.append(abs % 10);
                                    i8++;
                                } else if (containsAt(str, i8, "I") != null) {
                                    sb.append(Math.abs(DateTimeUtils.getIsoWeekYear(j)) % 10);
                                    i8++;
                                } else {
                                    Capitalization containsAt7 = containsAt(str, i8, "MONTH");
                                    if (containsAt7 != null) {
                                        String str4 = getDateNames(0)[monthFromDateValue - 1];
                                        if (z2) {
                                            str4 = StringUtils.pad(str4, "September".length(), CharSequenceUtil.SPACE, true);
                                        }
                                        sb.append(containsAt7.apply(str4));
                                        i8 += 5;
                                    } else {
                                        Capitalization containsAt8 = containsAt(str, i8, "MON");
                                        if (containsAt8 != null) {
                                            sb.append(containsAt8.apply(getDateNames(1)[monthFromDateValue - 1]));
                                            i8 += 3;
                                        } else if (containsAt(str, i8, "MM") != null) {
                                            StringUtils.appendTwoDigits(sb, monthFromDateValue);
                                            i8 += 2;
                                        } else {
                                            Capitalization containsAt9 = containsAt(str, i8, "RM");
                                            if (containsAt9 != null) {
                                                sb.append(containsAt9.apply(toRomanNumeral(monthFromDateValue)));
                                                i8 += 2;
                                            } else if (containsAt(str, i8, "Q") != null) {
                                                sb.append(1 + ((monthFromDateValue - 1) / 3));
                                                i8++;
                                            } else if (containsAt(str, i8, "X") != null) {
                                                sb.append(DecimalFormatSymbols.getInstance().getDecimalSeparator());
                                                i8++;
                                            } else if (containsAt(str, i8, "FM") != null) {
                                                z2 = !z2;
                                                i8 += 2;
                                            } else if (containsAt(str, i8, "FX") != null) {
                                                i8 += 2;
                                            } else if (containsAt(str, i8, "\"") != null) {
                                                i8++;
                                                while (true) {
                                                    if (i8 < str.length()) {
                                                        char charAt = str.charAt(i8);
                                                        if (charAt != '\"') {
                                                            sb.append(charAt);
                                                            i8++;
                                                        } else {
                                                            i8++;
                                                            break;
                                                        }
                                                    } else {
                                                        break;
                                                    }
                                                }
                                            } else if (str.charAt(i8) == '-' || str.charAt(i8) == '/' || str.charAt(i8) == ',' || str.charAt(i8) == '.' || str.charAt(i8) == ';' || str.charAt(i8) == ':' || str.charAt(i8) == ' ') {
                                                sb.append(str.charAt(i8));
                                                i8++;
                                            } else {
                                                throw DbException.get(ErrorCode.INVALID_TO_CHAR_FORMAT, str);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    private static Capitalization containsAt(String str, int i, String... strArr) {
        for (String str2 : strArr) {
            if (i + str2.length() <= str.length()) {
                boolean z = true;
                Boolean bool = null;
                Boolean bool2 = null;
                int i2 = 0;
                while (true) {
                    if (i2 >= str2.length()) {
                        break;
                    }
                    char charAt = str.charAt(i + i2);
                    char charAt2 = str2.charAt(i2);
                    if (charAt != charAt2 && Character.toUpperCase(charAt) != Character.toUpperCase(charAt2)) {
                        z = false;
                        break;
                    }
                    if (Character.isLetter(charAt)) {
                        if (bool == null) {
                            bool = Boolean.valueOf(Character.isUpperCase(charAt));
                        } else if (bool2 == null) {
                            bool2 = Boolean.valueOf(Character.isUpperCase(charAt));
                        }
                    }
                    i2++;
                }
                if (z) {
                    return Capitalization.toCapitalization(bool, bool2);
                }
            }
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/ToCharFunction$Capitalization.class */
    public enum Capitalization {
        UPPERCASE,
        LOWERCASE,
        CAPITALIZE;

        static Capitalization toCapitalization(Boolean bool, Boolean bool2) {
            if (bool == null) {
                return CAPITALIZE;
            }
            if (bool2 == null) {
                return bool.booleanValue() ? UPPERCASE : LOWERCASE;
            }
            if (bool.booleanValue()) {
                return bool2.booleanValue() ? UPPERCASE : CAPITALIZE;
            }
            return LOWERCASE;
        }

        public String apply(String str) {
            if (str == null || str.isEmpty()) {
                return str;
            }
            switch (this) {
                case UPPERCASE:
                    return StringUtils.toUpperEnglish(str);
                case LOWERCASE:
                    return StringUtils.toLowerEnglish(str);
                case CAPITALIZE:
                    return Character.toUpperCase(str.charAt(0)) + (str.length() > 1 ? StringUtils.toLowerEnglish(str).substring(1) : "");
                default:
                    throw new IllegalArgumentException("Unknown capitalization strategy: " + this);
            }
        }
    }

    public ToCharFunction(Expression expression, Expression expression2, Expression expression3) {
        super(expression2 == null ? new Expression[]{expression} : expression3 == null ? new Expression[]{expression, expression2} : new Expression[]{expression, expression2, expression3});
    }

    @Override // org.h2.expression.function.FunctionN
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2, Value value3) {
        Value value4;
        switch (value.getValueType()) {
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                value4 = ValueVarchar.get(toChar(value.getBigDecimal(), value2 == null ? null : value2.getString(), value3 == null ? null : value3.getString()), sessionLocal);
                break;
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
                value4 = ValueVarchar.get(toCharDateTime(sessionLocal, value, value2 == null ? null : value2.getString(), value3 == null ? null : value3.getString()), sessionLocal);
                break;
            default:
                value4 = ValueVarchar.get(value.getString(), sessionLocal);
                break;
        }
        return value4;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        this.type = TypeInfo.TYPE_VARCHAR;
        if (optimizeArguments) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return "TO_CHAR";
    }
}
