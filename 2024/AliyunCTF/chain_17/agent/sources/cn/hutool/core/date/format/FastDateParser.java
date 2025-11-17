package cn.hutool.core.date.format;

import cn.hutool.core.map.SafeConcurrentHashMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateParser.class */
public class FastDateParser extends AbstractDateBasic implements DateParser {
    private static final long serialVersionUID = -3199383897950947498L;
    private final int century;
    private final int startYear;
    private transient List<StrategyAndWidth> patterns;
    static final Locale JAPANESE_IMPERIAL = new Locale("ja", "JP", "JP");
    private static final Comparator<String> LONGER_FIRST_LOWERCASE = Comparator.reverseOrder();
    private static final ConcurrentMap<Locale, Strategy>[] CACHES = new ConcurrentMap[17];
    private static final Strategy ABBREVIATED_YEAR_STRATEGY = new NumberStrategy(1) { // from class: cn.hutool.core.date.format.FastDateParser.1
        @Override // cn.hutool.core.date.format.FastDateParser.NumberStrategy
        int modify(FastDateParser parser, int iValue) {
            return iValue < 100 ? parser.adjustYear(iValue) : iValue;
        }
    };
    private static final Strategy NUMBER_MONTH_STRATEGY = new NumberStrategy(2) { // from class: cn.hutool.core.date.format.FastDateParser.2
        @Override // cn.hutool.core.date.format.FastDateParser.NumberStrategy
        int modify(FastDateParser parser, int iValue) {
            return iValue - 1;
        }
    };
    private static final Strategy LITERAL_YEAR_STRATEGY = new NumberStrategy(1);
    private static final Strategy WEEK_OF_YEAR_STRATEGY = new NumberStrategy(3);
    private static final Strategy WEEK_OF_MONTH_STRATEGY = new NumberStrategy(4);
    private static final Strategy DAY_OF_YEAR_STRATEGY = new NumberStrategy(6);
    private static final Strategy DAY_OF_MONTH_STRATEGY = new NumberStrategy(5);
    private static final Strategy DAY_OF_WEEK_STRATEGY = new NumberStrategy(7) { // from class: cn.hutool.core.date.format.FastDateParser.3
        @Override // cn.hutool.core.date.format.FastDateParser.NumberStrategy
        int modify(FastDateParser parser, int iValue) {
            if (iValue != 7) {
                return iValue + 1;
            }
            return 1;
        }
    };
    private static final Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY = new NumberStrategy(8);
    private static final Strategy HOUR_OF_DAY_STRATEGY = new NumberStrategy(11);
    private static final Strategy HOUR24_OF_DAY_STRATEGY = new NumberStrategy(11) { // from class: cn.hutool.core.date.format.FastDateParser.4
        @Override // cn.hutool.core.date.format.FastDateParser.NumberStrategy
        int modify(FastDateParser parser, int iValue) {
            if (iValue == 24) {
                return 0;
            }
            return iValue;
        }
    };
    private static final Strategy HOUR12_STRATEGY = new NumberStrategy(10) { // from class: cn.hutool.core.date.format.FastDateParser.5
        @Override // cn.hutool.core.date.format.FastDateParser.NumberStrategy
        int modify(FastDateParser parser, int iValue) {
            if (iValue == 12) {
                return 0;
            }
            return iValue;
        }
    };
    private static final Strategy HOUR_STRATEGY = new NumberStrategy(10);
    private static final Strategy MINUTE_STRATEGY = new NumberStrategy(12);
    private static final Strategy SECOND_STRATEGY = new NumberStrategy(13);
    private static final Strategy MILLISECOND_STRATEGY = new NumberStrategy(14);

    public FastDateParser(String pattern, TimeZone timeZone, Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    public FastDateParser(String pattern, TimeZone timeZone, Locale locale, Date centuryStart) {
        super(pattern, timeZone, locale);
        int centuryStartYear;
        Calendar definingCalendar = Calendar.getInstance(timeZone, locale);
        if (centuryStart != null) {
            definingCalendar.setTime(centuryStart);
            centuryStartYear = definingCalendar.get(1);
        } else if (locale.equals(JAPANESE_IMPERIAL)) {
            centuryStartYear = 0;
        } else {
            definingCalendar.setTime(new Date());
            centuryStartYear = definingCalendar.get(1) - 80;
        }
        this.century = (centuryStartYear / 100) * 100;
        this.startYear = centuryStartYear - this.century;
        init(definingCalendar);
    }

    private void init(Calendar definingCalendar) {
        this.patterns = new ArrayList();
        StrategyParser fm = new StrategyParser(definingCalendar);
        while (true) {
            StrategyAndWidth field = fm.getNextStrategy();
            if (field != null) {
                this.patterns.add(field);
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateParser$StrategyAndWidth.class */
    public static class StrategyAndWidth {
        final Strategy strategy;
        final int width;

        StrategyAndWidth(Strategy strategy, int width) {
            this.strategy = strategy;
            this.width = width;
        }

        int getMaxWidth(ListIterator<StrategyAndWidth> lt) {
            if (!this.strategy.isNumber() || !lt.hasNext()) {
                return 0;
            }
            Strategy nextStrategy = lt.next().strategy;
            lt.previous();
            if (nextStrategy.isNumber()) {
                return this.width;
            }
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateParser$StrategyParser.class */
    public class StrategyParser {
        private final Calendar definingCalendar;
        private int currentIdx;

        StrategyParser(Calendar definingCalendar) {
            this.definingCalendar = definingCalendar;
        }

        StrategyAndWidth getNextStrategy() {
            if (this.currentIdx >= FastDateParser.this.pattern.length()) {
                return null;
            }
            char c = FastDateParser.this.pattern.charAt(this.currentIdx);
            if (FastDateParser.isFormatLetter(c)) {
                return letterPattern(c);
            }
            return literal();
        }

        private StrategyAndWidth letterPattern(char c) {
            int begin = this.currentIdx;
            do {
                int i = this.currentIdx + 1;
                this.currentIdx = i;
                if (i >= FastDateParser.this.pattern.length()) {
                    break;
                }
            } while (FastDateParser.this.pattern.charAt(this.currentIdx) == c);
            int width = this.currentIdx - begin;
            return new StrategyAndWidth(FastDateParser.this.getStrategy(c, width, this.definingCalendar), width);
        }

        private StrategyAndWidth literal() {
            boolean activeQuote = false;
            StringBuilder sb = new StringBuilder();
            while (this.currentIdx < FastDateParser.this.pattern.length()) {
                char c = FastDateParser.this.pattern.charAt(this.currentIdx);
                if (!activeQuote && FastDateParser.isFormatLetter(c)) {
                    break;
                }
                if (c == '\'') {
                    int i = this.currentIdx + 1;
                    this.currentIdx = i;
                    if (i == FastDateParser.this.pattern.length() || FastDateParser.this.pattern.charAt(this.currentIdx) != '\'') {
                        activeQuote = !activeQuote;
                    }
                }
                this.currentIdx++;
                sb.append(c);
            }
            if (activeQuote) {
                throw new IllegalArgumentException("Unterminated quote");
            }
            String formatField = sb.toString();
            return new StrategyAndWidth(new CopyQuotedStrategy(formatField), formatField.length());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isFormatLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Calendar definingCalendar = Calendar.getInstance(this.timeZone, this.locale);
        init(definingCalendar);
    }

    @Override // cn.hutool.core.date.format.DateParser
    public Date parse(String source) throws ParseException {
        ParsePosition pp = new ParsePosition(0);
        Date date = parse(source, pp);
        if (date == null) {
            if (this.locale.equals(JAPANESE_IMPERIAL)) {
                throw new ParseException("(The " + this.locale + " locale does not support dates before 1868 AD)\nUnparseable date: \"" + source, pp.getErrorIndex());
            }
            throw new ParseException("Unparseable date: " + source, pp.getErrorIndex());
        }
        return date;
    }

    @Override // cn.hutool.core.date.format.DateParser
    public Date parse(String source, ParsePosition pos) {
        Calendar cal = Calendar.getInstance(this.timeZone, this.locale);
        cal.clear();
        if (parse(source, pos, cal)) {
            return cal.getTime();
        }
        return null;
    }

    @Override // cn.hutool.core.date.format.DateParser
    public boolean parse(String source, ParsePosition pos, Calendar calendar) {
        ListIterator<StrategyAndWidth> lt = this.patterns.listIterator();
        while (lt.hasNext()) {
            StrategyAndWidth strategyAndWidth = lt.next();
            int maxWidth = strategyAndWidth.getMaxWidth(lt);
            if (false == strategyAndWidth.strategy.parse(this, calendar, source, pos, maxWidth)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static StringBuilder simpleQuote(StringBuilder sb, String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '$':
                case '(':
                case ')':
                case '*':
                case '+':
                case '.':
                case '?':
                case '[':
                case '\\':
                case '^':
                case '{':
                case '|':
                    sb.append('\\');
                    break;
            }
            sb.append(c);
        }
        return sb;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Map<String, Integer> appendDisplayNames(Calendar cal, Locale locale, int field, StringBuilder regex) {
        Map<String, Integer> values = new HashMap<>();
        Map<String, Integer> displayNames = cal.getDisplayNames(field, 0, locale);
        TreeSet<String> sorted = new TreeSet<>(LONGER_FIRST_LOWERCASE);
        for (Map.Entry<String, Integer> displayName : displayNames.entrySet()) {
            String key = displayName.getKey().toLowerCase(locale);
            if (sorted.add(key)) {
                values.put(key, displayName.getValue());
            }
        }
        Iterator<String> it = sorted.iterator();
        while (it.hasNext()) {
            String symbol = it.next();
            simpleQuote(regex, symbol).append('|');
        }
        return values;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int adjustYear(int twoDigitYear) {
        int trial = this.century + twoDigitYear;
        return twoDigitYear >= this.startYear ? trial : trial + 100;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateParser$Strategy.class */
    public static abstract class Strategy {
        abstract boolean parse(FastDateParser fastDateParser, Calendar calendar, String str, ParsePosition parsePosition, int i);

        private Strategy() {
        }

        boolean isNumber() {
            return false;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateParser$PatternStrategy.class */
    private static abstract class PatternStrategy extends Strategy {
        private Pattern pattern;

        abstract void setCalendar(FastDateParser fastDateParser, Calendar calendar, String str);

        private PatternStrategy() {
            super();
        }

        void createPattern(StringBuilder regex) {
            createPattern(regex.toString());
        }

        void createPattern(String regex) {
            this.pattern = Pattern.compile(regex);
        }

        @Override // cn.hutool.core.date.format.FastDateParser.Strategy
        boolean isNumber() {
            return false;
        }

        @Override // cn.hutool.core.date.format.FastDateParser.Strategy
        boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
            Matcher matcher = this.pattern.matcher(source.substring(pos.getIndex()));
            if (!matcher.lookingAt()) {
                pos.setErrorIndex(pos.getIndex());
                return false;
            }
            pos.setIndex(pos.getIndex() + matcher.end(1));
            setCalendar(parser, calendar, matcher.group(1));
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Strategy getStrategy(char f, int width, Calendar definingCalendar) {
        switch (f) {
            case 'D':
                return DAY_OF_YEAR_STRATEGY;
            case 'E':
                return getLocaleSpecificStrategy(7, definingCalendar);
            case 'F':
                return DAY_OF_WEEK_IN_MONTH_STRATEGY;
            case 'G':
                return getLocaleSpecificStrategy(0, definingCalendar);
            case 'H':
                return HOUR_OF_DAY_STRATEGY;
            case 'I':
            case 'J':
            case 'L':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'c':
            case 'e':
            case 'f':
            case 'g':
            case 'i':
            case 'j':
            case 'l':
            case Opcodes.FDIV /* 110 */:
            case Opcodes.DDIV /* 111 */:
            case 'p':
            case Opcodes.LREM /* 113 */:
            case Opcodes.FREM /* 114 */:
            case 't':
            case Opcodes.FNEG /* 118 */:
            case 'x':
            default:
                throw new IllegalArgumentException("Format '" + f + "' not supported");
            case 'K':
                return HOUR_STRATEGY;
            case 'M':
                return width >= 3 ? getLocaleSpecificStrategy(2, definingCalendar) : NUMBER_MONTH_STRATEGY;
            case 'S':
                return MILLISECOND_STRATEGY;
            case 'W':
                return WEEK_OF_MONTH_STRATEGY;
            case 'X':
                return ISO8601TimeZoneStrategy.getStrategy(width);
            case 'Y':
            case Opcodes.LSHL /* 121 */:
                return width > 2 ? LITERAL_YEAR_STRATEGY : ABBREVIATED_YEAR_STRATEGY;
            case 'Z':
                if (width == 2) {
                    return ISO8601TimeZoneStrategy.ISO_8601_3_STRATEGY;
                }
                break;
            case 'a':
                return getLocaleSpecificStrategy(9, definingCalendar);
            case 'd':
                return DAY_OF_MONTH_STRATEGY;
            case 'h':
                return HOUR12_STRATEGY;
            case Opcodes.DMUL /* 107 */:
                return HOUR24_OF_DAY_STRATEGY;
            case Opcodes.LDIV /* 109 */:
                return MINUTE_STRATEGY;
            case 's':
                return SECOND_STRATEGY;
            case Opcodes.LNEG /* 117 */:
                return DAY_OF_WEEK_STRATEGY;
            case Opcodes.DNEG /* 119 */:
                return WEEK_OF_YEAR_STRATEGY;
            case 'z':
                break;
        }
        return getLocaleSpecificStrategy(15, definingCalendar);
    }

    private static ConcurrentMap<Locale, Strategy> getCache(int field) {
        ConcurrentMap<Locale, Strategy> concurrentMap;
        synchronized (CACHES) {
            if (CACHES[field] == null) {
                CACHES[field] = new SafeConcurrentHashMap(3);
            }
            concurrentMap = CACHES[field];
        }
        return concurrentMap;
    }

    private Strategy getLocaleSpecificStrategy(int field, Calendar definingCalendar) {
        ConcurrentMap<Locale, Strategy> cache = getCache(field);
        Strategy strategy = cache.get(this.locale);
        if (strategy == null) {
            strategy = field == 15 ? new TimeZoneStrategy(this.locale) : new CaseInsensitiveTextStrategy(field, definingCalendar, this.locale);
            Strategy inCache = cache.putIfAbsent(this.locale, strategy);
            if (inCache != null) {
                return inCache;
            }
        }
        return strategy;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateParser$CopyQuotedStrategy.class */
    public static class CopyQuotedStrategy extends Strategy {
        private final String formatField;

        CopyQuotedStrategy(String formatField) {
            super();
            this.formatField = formatField;
        }

        @Override // cn.hutool.core.date.format.FastDateParser.Strategy
        boolean isNumber() {
            return false;
        }

        @Override // cn.hutool.core.date.format.FastDateParser.Strategy
        boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
            for (int idx = 0; idx < this.formatField.length(); idx++) {
                int sIdx = idx + pos.getIndex();
                if (sIdx == source.length()) {
                    pos.setErrorIndex(sIdx);
                    return false;
                }
                if (this.formatField.charAt(idx) != source.charAt(sIdx)) {
                    pos.setErrorIndex(sIdx);
                    return false;
                }
            }
            pos.setIndex(this.formatField.length() + pos.getIndex());
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateParser$CaseInsensitiveTextStrategy.class */
    public static class CaseInsensitiveTextStrategy extends PatternStrategy {
        private final int field;
        final Locale locale;
        private final Map<String, Integer> lKeyValues;

        CaseInsensitiveTextStrategy(int field, Calendar definingCalendar, Locale locale) {
            super();
            this.field = field;
            this.locale = locale;
            StringBuilder regex = new StringBuilder();
            regex.append("((?iu)");
            this.lKeyValues = FastDateParser.appendDisplayNames(definingCalendar, locale, field, regex);
            regex.setLength(regex.length() - 1);
            regex.append(")");
            createPattern(regex);
        }

        @Override // cn.hutool.core.date.format.FastDateParser.PatternStrategy
        void setCalendar(FastDateParser parser, Calendar cal, String value) {
            Integer iVal = this.lKeyValues.get(value.toLowerCase(this.locale));
            cal.set(this.field, iVal.intValue());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateParser$NumberStrategy.class */
    private static class NumberStrategy extends Strategy {
        private final int field;

        NumberStrategy(int field) {
            super();
            this.field = field;
        }

        @Override // cn.hutool.core.date.format.FastDateParser.Strategy
        boolean isNumber() {
            return true;
        }

        @Override // cn.hutool.core.date.format.FastDateParser.Strategy
        boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth) {
            int idx = pos.getIndex();
            int last = source.length();
            if (maxWidth == 0) {
                while (idx < last) {
                    char c = source.charAt(idx);
                    if (!Character.isWhitespace(c)) {
                        break;
                    }
                    idx++;
                }
                pos.setIndex(idx);
            } else {
                int end = idx + maxWidth;
                if (last > end) {
                    last = end;
                }
            }
            while (idx < last) {
                char c2 = source.charAt(idx);
                if (!Character.isDigit(c2)) {
                    break;
                }
                idx++;
            }
            if (pos.getIndex() == idx) {
                pos.setErrorIndex(idx);
                return false;
            }
            int value = Integer.parseInt(source.substring(pos.getIndex(), idx));
            pos.setIndex(idx);
            calendar.set(this.field, modify(parser, value));
            return true;
        }

        int modify(FastDateParser parser, int iValue) {
            return iValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateParser$TimeZoneStrategy.class */
    public static class TimeZoneStrategy extends PatternStrategy {
        private static final String RFC_822_TIME_ZONE = "[+-]\\d{4}";
        private static final String UTC_TIME_ZONE_WITH_OFFSET = "[+-]\\d{2}:\\d{2}";
        private static final String GMT_OPTION = "GMT[+-]\\d{1,2}:\\d{2}";
        private final Locale locale;
        private final Map<String, TzInfo> tzNames;
        private static final int ID = 0;

        /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateParser$TimeZoneStrategy$TzInfo.class */
        private static class TzInfo {
            TimeZone zone;
            int dstOffset;

            TzInfo(TimeZone tz, boolean useDst) {
                this.zone = tz;
                this.dstOffset = useDst ? tz.getDSTSavings() : 0;
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:14:0x00bc, code lost:            if (r0[r18] == null) goto L32;     */
        /* JADX WARN: Code restructure failed: missing block: B:15:0x00bf, code lost:            r0 = r0[r18].toLowerCase(r6);     */
        /* JADX WARN: Code restructure failed: missing block: B:16:0x00d2, code lost:            if (r0.add(r0) == false) goto L33;     */
        /* JADX WARN: Code restructure failed: missing block: B:17:0x00d5, code lost:            r5.tzNames.put(r0, r17);     */
        /* JADX WARN: Code restructure failed: missing block: B:19:0x00e3, code lost:            r18 = r18 + 1;     */
        /* JADX WARN: Removed duplicated region for block: B:9:0x0087  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        TimeZoneStrategy(java.util.Locale r6) {
            /*
                Method dump skipped, instructions count: 297
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: cn.hutool.core.date.format.FastDateParser.TimeZoneStrategy.<init>(java.util.Locale):void");
        }

        @Override // cn.hutool.core.date.format.FastDateParser.PatternStrategy
        void setCalendar(FastDateParser parser, Calendar cal, String value) {
            if (value.charAt(0) == '+' || value.charAt(0) == '-') {
                TimeZone tz = TimeZone.getTimeZone("GMT" + value);
                cal.setTimeZone(tz);
            } else if (value.regionMatches(true, 0, "GMT", 0, 3)) {
                TimeZone tz2 = TimeZone.getTimeZone(value.toUpperCase());
                cal.setTimeZone(tz2);
            } else {
                TzInfo tzInfo = this.tzNames.get(value.toLowerCase(this.locale));
                cal.set(16, tzInfo.dstOffset);
                cal.set(15, parser.getTimeZone().getRawOffset());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDateParser$ISO8601TimeZoneStrategy.class */
    public static class ISO8601TimeZoneStrategy extends PatternStrategy {
        private static final Strategy ISO_8601_1_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}))");
        private static final Strategy ISO_8601_2_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}\\d{2}))");
        private static final Strategy ISO_8601_3_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}(?::)\\d{2}))");

        ISO8601TimeZoneStrategy(String pattern) {
            super();
            createPattern(pattern);
        }

        @Override // cn.hutool.core.date.format.FastDateParser.PatternStrategy
        void setCalendar(FastDateParser parser, Calendar cal, String value) {
            if (Objects.equals(value, "Z")) {
                cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            } else {
                cal.setTimeZone(TimeZone.getTimeZone("GMT" + value));
            }
        }

        static Strategy getStrategy(int tokenLen) {
            switch (tokenLen) {
                case 1:
                    return ISO_8601_1_STRATEGY;
                case 2:
                    return ISO_8601_2_STRATEGY;
                case 3:
                    return ISO_8601_3_STRATEGY;
                default:
                    throw new IllegalArgumentException("invalid number of X");
            }
        }
    }
}
