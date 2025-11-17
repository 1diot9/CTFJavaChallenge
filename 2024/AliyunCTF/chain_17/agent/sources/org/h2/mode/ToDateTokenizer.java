package org.h2.mode;

import cn.hutool.core.date.chinese.LunarInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.h2.api.ErrorCode;
import org.h2.expression.function.ToCharFunction;
import org.h2.message.DbException;
import org.h2.util.TimeZoneProvider;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/ToDateTokenizer.class */
final class ToDateTokenizer {
    static final Pattern PATTERN_INLINE = Pattern.compile("(\"[^\"]*\")");
    static final Pattern PATTERN_NUMBER = Pattern.compile("^([+-]?[0-9]+)");
    static final Pattern PATTERN_FOUR_DIGITS = Pattern.compile("^([+-]?[0-9]{4})");
    static final Pattern PATTERN_TWO_TO_FOUR_DIGITS = Pattern.compile("^([+-]?[0-9]{2,4})");
    static final Pattern PATTERN_THREE_DIGITS = Pattern.compile("^([+-]?[0-9]{3})");
    static final Pattern PATTERN_TWO_DIGITS = Pattern.compile("^([+-]?[0-9]{2})");
    static final Pattern PATTERN_TWO_DIGITS_OR_LESS = Pattern.compile("^([+-]?[0-9][0-9]?)");
    static final Pattern PATTERN_ONE_DIGIT = Pattern.compile("^([+-]?[0-9])");
    static final Pattern PATTERN_FF = Pattern.compile("^(FF[0-9]?)", 2);
    static final Pattern PATTERN_AM_PM = Pattern.compile("^(AM|A\\.M\\.|PM|P\\.M\\.)", 2);
    static final Pattern PATTERN_BC_AD = Pattern.compile("^(BC|B\\.C\\.|AD|A\\.D\\.)", 2);
    static final YearParslet PARSLET_YEAR = new YearParslet();
    static final MonthParslet PARSLET_MONTH = new MonthParslet();
    static final DayParslet PARSLET_DAY = new DayParslet();
    static final TimeParslet PARSLET_TIME = new TimeParslet();
    static final InlineParslet PARSLET_INLINE = new InlineParslet();

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/ToDateTokenizer$ToDateParslet.class */
    interface ToDateParslet {
        void parse(ToDateParser toDateParser, FormatTokenEnum formatTokenEnum, String str);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/ToDateTokenizer$YearParslet.class */
    static class YearParslet implements ToDateParslet {
        YearParslet() {
        }

        @Override // org.h2.mode.ToDateTokenizer.ToDateParslet
        public void parse(ToDateParser toDateParser, FormatTokenEnum formatTokenEnum, String str) {
            String str2 = null;
            switch (formatTokenEnum) {
                case SYYYY:
                case YYYY:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_FOUR_DIGITS, toDateParser, formatTokenEnum);
                    int parseInt = Integer.parseInt(str2);
                    if (parseInt == 0) {
                        ToDateTokenizer.throwException(toDateParser, "Year may not be zero");
                    }
                    toDateParser.setYear(parseInt >= 0 ? parseInt : parseInt + 1);
                    break;
                case YYY:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_THREE_DIGITS, toDateParser, formatTokenEnum);
                    int parseInt2 = Integer.parseInt(str2);
                    if (parseInt2 > 999) {
                        ToDateTokenizer.throwException(toDateParser, "Year may have only three digits with specified format");
                    }
                    int currentYear = parseInt2 + ((toDateParser.getCurrentYear() / 1000) * 1000);
                    toDateParser.setYear(currentYear >= 0 ? currentYear : currentYear + 1);
                    break;
                case RRRR:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_TO_FOUR_DIGITS, toDateParser, formatTokenEnum);
                    int parseInt3 = Integer.parseInt(str2);
                    if (str2.length() < 4) {
                        if (parseInt3 < 50) {
                            parseInt3 += 2000;
                        } else if (parseInt3 < 100) {
                            parseInt3 += LunarInfo.BASE_YEAR;
                        }
                    }
                    if (parseInt3 == 0) {
                        ToDateTokenizer.throwException(toDateParser, "Year may not be zero");
                    }
                    toDateParser.setYear(parseInt3);
                    break;
                case RR:
                    int currentYear2 = toDateParser.getCurrentYear() / 100;
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS, toDateParser, formatTokenEnum);
                    toDateParser.setYear(Integer.parseInt(str2) + (currentYear2 * 100));
                    break;
                case EE:
                    ToDateTokenizer.throwException(toDateParser, String.format("token '%s' not supported yet.", formatTokenEnum.name()));
                    break;
                case E:
                    ToDateTokenizer.throwException(toDateParser, String.format("token '%s' not supported yet.", formatTokenEnum.name()));
                    break;
                case YY:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS, toDateParser, formatTokenEnum);
                    int parseInt4 = Integer.parseInt(str2);
                    if (parseInt4 > 99) {
                        ToDateTokenizer.throwException(toDateParser, "Year may have only two digits with specified format");
                    }
                    int currentYear3 = parseInt4 + ((toDateParser.getCurrentYear() / 100) * 100);
                    toDateParser.setYear(currentYear3 >= 0 ? currentYear3 : currentYear3 + 1);
                    break;
                case SCC:
                case CC:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS, toDateParser, formatTokenEnum);
                    toDateParser.setYear(Integer.parseInt(str2) * 100);
                    break;
                case Y:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_ONE_DIGIT, toDateParser, formatTokenEnum);
                    int parseInt5 = Integer.parseInt(str2);
                    if (parseInt5 > 9) {
                        ToDateTokenizer.throwException(toDateParser, "Year may have only two digits with specified format");
                    }
                    int currentYear4 = parseInt5 + ((toDateParser.getCurrentYear() / 10) * 10);
                    toDateParser.setYear(currentYear4 >= 0 ? currentYear4 : currentYear4 + 1);
                    break;
                case BC_AD:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_BC_AD, toDateParser, formatTokenEnum);
                    toDateParser.setBC(str2.toUpperCase().startsWith("B"));
                    break;
                default:
                    throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", getClass().getSimpleName(), formatTokenEnum));
            }
            toDateParser.remove(str2, str);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/ToDateTokenizer$MonthParslet.class */
    static class MonthParslet implements ToDateParslet {
        private static final String[] ROMAN_MONTH = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};

        MonthParslet() {
        }

        @Override // org.h2.mode.ToDateTokenizer.ToDateParslet
        public void parse(ToDateParser toDateParser, FormatTokenEnum formatTokenEnum, String str) {
            String inputStr = toDateParser.getInputStr();
            String str2 = null;
            switch (formatTokenEnum) {
                case MONTH:
                    str2 = ToDateTokenizer.setByName(toDateParser, 0);
                    break;
                case Q:
                    ToDateTokenizer.throwException(toDateParser, String.format("token '%s' not supported yet.", formatTokenEnum.name()));
                    break;
                case MON:
                    str2 = ToDateTokenizer.setByName(toDateParser, 1);
                    break;
                case MM:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, toDateParser, formatTokenEnum);
                    toDateParser.setMonth(Integer.parseInt(str2));
                    break;
                case RM:
                    int i = 0;
                    String[] strArr = ROMAN_MONTH;
                    int length = strArr.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 < length) {
                            String str3 = strArr[i2];
                            i++;
                            int length2 = str3.length();
                            if (inputStr.length() < length2 || !str3.equalsIgnoreCase(inputStr.substring(0, length2))) {
                                i2++;
                            } else {
                                toDateParser.setMonth(i + 1);
                                str2 = str3;
                            }
                        }
                    }
                    if (str2 == null || str2.isEmpty()) {
                        ToDateTokenizer.throwException(toDateParser, String.format("Issue happened when parsing token '%s'. Expected one of: %s", formatTokenEnum.name(), Arrays.toString(ROMAN_MONTH)));
                        break;
                    }
                    break;
                default:
                    throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", getClass().getSimpleName(), formatTokenEnum));
            }
            toDateParser.remove(str2, str);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/ToDateTokenizer$DayParslet.class */
    static class DayParslet implements ToDateParslet {
        DayParslet() {
        }

        @Override // org.h2.mode.ToDateTokenizer.ToDateParslet
        public void parse(ToDateParser toDateParser, FormatTokenEnum formatTokenEnum, String str) {
            String matchStringOrThrow;
            switch (formatTokenEnum) {
                case DDD:
                    matchStringOrThrow = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, toDateParser, formatTokenEnum);
                    toDateParser.setDayOfYear(Integer.parseInt(matchStringOrThrow));
                    break;
                case DD:
                    matchStringOrThrow = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, toDateParser, formatTokenEnum);
                    toDateParser.setDay(Integer.parseInt(matchStringOrThrow));
                    break;
                case D:
                    matchStringOrThrow = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_ONE_DIGIT, toDateParser, formatTokenEnum);
                    toDateParser.setDay(Integer.parseInt(matchStringOrThrow));
                    break;
                case DAY:
                    matchStringOrThrow = ToDateTokenizer.setByName(toDateParser, 2);
                    break;
                case DY:
                    matchStringOrThrow = ToDateTokenizer.setByName(toDateParser, 3);
                    break;
                case J:
                    matchStringOrThrow = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, toDateParser, formatTokenEnum);
                    toDateParser.setAbsoluteDay(Integer.parseInt(matchStringOrThrow) + ToCharFunction.JULIAN_EPOCH);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", getClass().getSimpleName(), formatTokenEnum));
            }
            toDateParser.remove(matchStringOrThrow, str);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/ToDateTokenizer$TimeParslet.class */
    static class TimeParslet implements ToDateParslet {
        TimeParslet() {
        }

        @Override // org.h2.mode.ToDateTokenizer.ToDateParslet
        public void parse(ToDateParser toDateParser, FormatTokenEnum formatTokenEnum, String str) {
            String str2;
            switch (formatTokenEnum) {
                case HH24:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, toDateParser, formatTokenEnum);
                    toDateParser.setHour(Integer.parseInt(str2));
                    break;
                case HH12:
                case HH:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, toDateParser, formatTokenEnum);
                    toDateParser.setHour12(Integer.parseInt(str2));
                    break;
                case MI:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, toDateParser, formatTokenEnum);
                    toDateParser.setMinute(Integer.parseInt(str2));
                    break;
                case SS:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, toDateParser, formatTokenEnum);
                    toDateParser.setSecond(Integer.parseInt(str2));
                    break;
                case SSSSS:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, toDateParser, formatTokenEnum);
                    int parseInt = Integer.parseInt(str2);
                    int i = parseInt % 60;
                    int i2 = parseInt / 60;
                    toDateParser.setHour((i2 / 60) % 24);
                    toDateParser.setMinute(i2 % 60);
                    toDateParser.setSecond(i);
                    break;
                case FF:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_NUMBER, toDateParser, formatTokenEnum);
                    toDateParser.setNanos((int) Double.parseDouble(String.format("%-9s", str2).replace(' ', '0').substring(0, 9)));
                    break;
                case AM_PM:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_AM_PM, toDateParser, formatTokenEnum);
                    if (str2.toUpperCase().startsWith("A")) {
                        toDateParser.setAmPm(true);
                        break;
                    } else {
                        toDateParser.setAmPm(false);
                        break;
                    }
                case TZH:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, toDateParser, formatTokenEnum);
                    toDateParser.setTimeZoneHour(Integer.parseInt(str2));
                    break;
                case TZM:
                    str2 = ToDateTokenizer.matchStringOrThrow(ToDateTokenizer.PATTERN_TWO_DIGITS_OR_LESS, toDateParser, formatTokenEnum);
                    toDateParser.setTimeZoneMinute(Integer.parseInt(str2));
                    break;
                case TZR:
                case TZD:
                    String inputStr = toDateParser.getInputStr();
                    toDateParser.setTimeZone(TimeZoneProvider.ofId(inputStr));
                    str2 = inputStr;
                    break;
                default:
                    throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", getClass().getSimpleName(), formatTokenEnum));
            }
            toDateParser.remove(str2, str);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/ToDateTokenizer$InlineParslet.class */
    static class InlineParslet implements ToDateParslet {
        InlineParslet() {
        }

        @Override // org.h2.mode.ToDateTokenizer.ToDateParslet
        public void parse(ToDateParser toDateParser, FormatTokenEnum formatTokenEnum, String str) {
            switch (formatTokenEnum) {
                case INLINE:
                    toDateParser.remove(str.replace("\"", ""), str);
                    return;
                default:
                    throw new IllegalArgumentException(String.format("%s: Internal Error. Unhandled case: %s", getClass().getSimpleName(), formatTokenEnum));
            }
        }
    }

    static String matchStringOrThrow(Pattern pattern, ToDateParser toDateParser, Enum<?> r9) {
        Matcher matcher = pattern.matcher(toDateParser.getInputStr());
        if (!matcher.find()) {
            throwException(toDateParser, String.format("Issue happened when parsing token '%s'", r9.name()));
        }
        return matcher.group(1);
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:10:0x003e. Please report as an issue. */
    static String setByName(ToDateParser toDateParser, int i) {
        String str = null;
        String inputStr = toDateParser.getInputStr();
        String[] dateNames = ToCharFunction.getDateNames(i);
        int i2 = 0;
        while (true) {
            if (i2 >= dateNames.length) {
                break;
            }
            String str2 = dateNames[i2];
            if (str2 == null || !str2.equalsIgnoreCase(inputStr.substring(0, str2.length()))) {
                i2++;
            } else {
                switch (i) {
                    case 0:
                    case 1:
                        toDateParser.setMonth(i2 + 1);
                    case 2:
                    case 3:
                        str = str2;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }
        if (str == null || str.isEmpty()) {
            throwException(toDateParser, String.format("Tried to parse one of '%s' but failed (may be an internal error?)", Arrays.toString(dateNames)));
        }
        return str;
    }

    static void throwException(ToDateParser toDateParser, String str) {
        throw DbException.get(ErrorCode.INVALID_TO_DATE_FORMAT, toDateParser.getFunctionName(), String.format(" %s. Details: %s", str, toDateParser));
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/ToDateTokenizer$FormatTokenEnum.class */
    public enum FormatTokenEnum {
        YYYY(ToDateTokenizer.PARSLET_YEAR),
        SYYYY(ToDateTokenizer.PARSLET_YEAR),
        YYY(ToDateTokenizer.PARSLET_YEAR),
        YY(ToDateTokenizer.PARSLET_YEAR),
        SCC(ToDateTokenizer.PARSLET_YEAR),
        CC(ToDateTokenizer.PARSLET_YEAR),
        RRRR(ToDateTokenizer.PARSLET_YEAR),
        RR(ToDateTokenizer.PARSLET_YEAR),
        BC_AD(ToDateTokenizer.PARSLET_YEAR, ToDateTokenizer.PATTERN_BC_AD),
        MONTH(ToDateTokenizer.PARSLET_MONTH),
        MON(ToDateTokenizer.PARSLET_MONTH),
        MM(ToDateTokenizer.PARSLET_MONTH),
        RM(ToDateTokenizer.PARSLET_MONTH),
        DDD(ToDateTokenizer.PARSLET_DAY),
        DAY(ToDateTokenizer.PARSLET_DAY),
        DD(ToDateTokenizer.PARSLET_DAY),
        DY(ToDateTokenizer.PARSLET_DAY),
        HH24(ToDateTokenizer.PARSLET_TIME),
        HH12(ToDateTokenizer.PARSLET_TIME),
        HH(ToDateTokenizer.PARSLET_TIME),
        MI(ToDateTokenizer.PARSLET_TIME),
        SSSSS(ToDateTokenizer.PARSLET_TIME),
        SS(ToDateTokenizer.PARSLET_TIME),
        FF(ToDateTokenizer.PARSLET_TIME, ToDateTokenizer.PATTERN_FF),
        TZH(ToDateTokenizer.PARSLET_TIME),
        TZM(ToDateTokenizer.PARSLET_TIME),
        TZR(ToDateTokenizer.PARSLET_TIME),
        TZD(ToDateTokenizer.PARSLET_TIME),
        AM_PM(ToDateTokenizer.PARSLET_TIME, ToDateTokenizer.PATTERN_AM_PM),
        EE(ToDateTokenizer.PARSLET_YEAR),
        E(ToDateTokenizer.PARSLET_YEAR),
        Y(ToDateTokenizer.PARSLET_YEAR),
        Q(ToDateTokenizer.PARSLET_MONTH),
        D(ToDateTokenizer.PARSLET_DAY),
        J(ToDateTokenizer.PARSLET_DAY),
        INLINE(ToDateTokenizer.PARSLET_INLINE, ToDateTokenizer.PATTERN_INLINE);

        private static final List<FormatTokenEnum> INLINE_LIST = Collections.singletonList(INLINE);
        private static final List<FormatTokenEnum>[] TOKENS;
        private final ToDateParslet toDateParslet;
        private final Pattern patternToUse;

        static {
            List<FormatTokenEnum>[] listArr = new List[25];
            for (FormatTokenEnum formatTokenEnum : values()) {
                String name = formatTokenEnum.name();
                if (name.indexOf(95) >= 0) {
                    for (String str : name.split("_")) {
                        putToCache(listArr, formatTokenEnum, str);
                    }
                } else {
                    putToCache(listArr, formatTokenEnum, name);
                }
            }
            TOKENS = listArr;
        }

        FormatTokenEnum(ToDateParslet toDateParslet, Pattern pattern) {
            this.toDateParslet = toDateParslet;
            this.patternToUse = pattern;
        }

        FormatTokenEnum(ToDateParslet toDateParslet) {
            this.toDateParslet = toDateParslet;
            this.patternToUse = Pattern.compile(String.format("^(%s)", name()), 2);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static List<FormatTokenEnum> getTokensInQuestion(String str) {
            if (str != null && !str.isEmpty()) {
                char upperCase = Character.toUpperCase(str.charAt(0));
                if (upperCase >= 'A' && upperCase <= 'Y') {
                    return TOKENS[upperCase - 'A'];
                }
                if (upperCase == '\"') {
                    return INLINE_LIST;
                }
                return null;
            }
            return null;
        }

        private static void putToCache(List<FormatTokenEnum>[] listArr, FormatTokenEnum formatTokenEnum, String str) {
            int upperCase = Character.toUpperCase(str.charAt(0)) - 'A';
            List<FormatTokenEnum> list = listArr[upperCase];
            if (list == null) {
                list = new ArrayList(1);
                listArr[upperCase] = list;
            }
            list.add(formatTokenEnum);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean parseFormatStrWithToken(ToDateParser toDateParser) {
            Matcher matcher = this.patternToUse.matcher(toDateParser.getFormatStr());
            boolean find = matcher.find();
            if (find) {
                this.toDateParslet.parse(toDateParser, this, matcher.group(1));
            }
            return find;
        }
    }

    private ToDateTokenizer() {
    }
}
