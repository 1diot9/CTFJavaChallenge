package cn.hutool.core.date.format;

import cn.hutool.core.date.DateException;
import cn.hutool.core.map.SafeConcurrentHashMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;
import org.apache.coyote.http11.Constants;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter.class */
public class FastDatePrinter extends AbstractDateBasic implements DatePrinter {
    private static final long serialVersionUID = -6305750172255764887L;
    private transient Rule[] rules;
    private transient int mMaxLengthEstimate;
    private static final int MAX_DIGITS = 10;
    private static final ConcurrentMap<TimeZoneDisplayKey, String> C_TIME_ZONE_DISPLAY_CACHE = new SafeConcurrentHashMap(7);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$NumberRule.class */
    public interface NumberRule extends Rule {
        void appendTo(Appendable appendable, int i) throws IOException;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$Rule.class */
    public interface Rule {
        int estimateLength();

        void appendTo(Appendable appendable, Calendar calendar) throws IOException;
    }

    public FastDatePrinter(String pattern, TimeZone timeZone, Locale locale) {
        super(pattern, timeZone, locale);
        init();
    }

    private void init() {
        List<Rule> rulesList = parsePattern();
        this.rules = (Rule[]) rulesList.toArray(new Rule[0]);
        int len = 0;
        int i = this.rules.length;
        while (true) {
            i--;
            if (i >= 0) {
                len += this.rules[i].estimateLength();
            } else {
                this.mMaxLengthEstimate = len;
                return;
            }
        }
    }

    protected List<Rule> parsePattern() {
        Rule rule;
        DateFormatSymbols symbols = new DateFormatSymbols(this.locale);
        List<Rule> rules = new ArrayList<>();
        String[] ERAs = symbols.getEras();
        String[] months = symbols.getMonths();
        String[] shortMonths = symbols.getShortMonths();
        String[] weekdays = symbols.getWeekdays();
        String[] shortWeekdays = symbols.getShortWeekdays();
        String[] AmPmStrings = symbols.getAmPmStrings();
        int length = this.pattern.length();
        int[] indexRef = new int[1];
        int i = 0;
        while (i < length) {
            indexRef[0] = i;
            String token = parseToken(this.pattern, indexRef);
            int i2 = indexRef[0];
            int tokenLen = token.length();
            if (tokenLen != 0) {
                char c = token.charAt(0);
                switch (c) {
                    case '\'':
                        String sub = token.substring(1);
                        if (sub.length() == 1) {
                            rule = new CharacterLiteral(sub.charAt(0));
                            break;
                        } else {
                            rule = new StringLiteral(sub);
                            break;
                        }
                    case '(':
                    case ')':
                    case '*':
                    case '+':
                    case ',':
                    case '-':
                    case '.':
                    case '/':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    case ':':
                    case ';':
                    case '<':
                    case '=':
                    case '>':
                    case '?':
                    case '@':
                    case 'A':
                    case 'B':
                    case 'C':
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
                        throw new IllegalArgumentException("Illegal pattern component: " + token);
                    case 'D':
                        rule = selectNumberRule(6, tokenLen);
                        break;
                    case 'E':
                        rule = new TextField(7, tokenLen < 4 ? shortWeekdays : weekdays);
                        break;
                    case 'F':
                        rule = selectNumberRule(8, tokenLen);
                        break;
                    case 'G':
                        rule = new TextField(0, ERAs);
                        break;
                    case 'H':
                        rule = selectNumberRule(11, tokenLen);
                        break;
                    case 'K':
                        rule = selectNumberRule(10, tokenLen);
                        break;
                    case 'M':
                        if (tokenLen >= 4) {
                            rule = new TextField(2, months);
                            break;
                        } else if (tokenLen == 3) {
                            rule = new TextField(2, shortMonths);
                            break;
                        } else if (tokenLen == 2) {
                            rule = TwoDigitMonthField.INSTANCE;
                            break;
                        } else {
                            rule = UnpaddedMonthField.INSTANCE;
                            break;
                        }
                    case 'S':
                        rule = selectNumberRule(14, tokenLen);
                        break;
                    case 'W':
                        rule = selectNumberRule(4, tokenLen);
                        break;
                    case 'X':
                        rule = Iso8601_Rule.getRule(tokenLen);
                        break;
                    case 'Y':
                    case Opcodes.LSHL /* 121 */:
                        if (tokenLen == 2) {
                            rule = TwoDigitYearField.INSTANCE;
                        } else {
                            rule = selectNumberRule(1, Math.max(tokenLen, 4));
                        }
                        if (c != 'Y') {
                            break;
                        } else {
                            rule = new WeekYear((NumberRule) rule);
                            break;
                        }
                    case 'Z':
                        if (tokenLen == 1) {
                            rule = TimeZoneNumberRule.INSTANCE_NO_COLON;
                            break;
                        } else if (tokenLen == 2) {
                            rule = Iso8601_Rule.ISO8601_HOURS_COLON_MINUTES;
                            break;
                        } else {
                            rule = TimeZoneNumberRule.INSTANCE_COLON;
                            break;
                        }
                    case 'a':
                        rule = new TextField(9, AmPmStrings);
                        break;
                    case 'd':
                        rule = selectNumberRule(5, tokenLen);
                        break;
                    case 'h':
                        rule = new TwelveHourField(selectNumberRule(10, tokenLen));
                        break;
                    case Opcodes.DMUL /* 107 */:
                        rule = new TwentyFourHourField(selectNumberRule(11, tokenLen));
                        break;
                    case Opcodes.LDIV /* 109 */:
                        rule = selectNumberRule(12, tokenLen);
                        break;
                    case 's':
                        rule = selectNumberRule(13, tokenLen);
                        break;
                    case Opcodes.LNEG /* 117 */:
                        rule = new DayInWeekField(selectNumberRule(7, tokenLen));
                        break;
                    case Opcodes.DNEG /* 119 */:
                        rule = selectNumberRule(3, tokenLen);
                        break;
                    case 'z':
                        if (tokenLen >= 4) {
                            rule = new TimeZoneNameRule(this.timeZone, this.locale, 1);
                            break;
                        } else {
                            rule = new TimeZoneNameRule(this.timeZone, this.locale, 0);
                            break;
                        }
                }
                rules.add(rule);
                i = i2 + 1;
            } else {
                return rules;
            }
        }
        return rules;
    }

    protected String parseToken(String pattern, int[] indexRef) {
        StringBuilder buf = new StringBuilder();
        int i = indexRef[0];
        int length = pattern.length();
        char c = pattern.charAt(i);
        if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
            buf.append(c);
            while (i + 1 < length) {
                char peek = pattern.charAt(i + 1);
                if (peek != c) {
                    break;
                }
                buf.append(c);
                i++;
            }
        } else {
            buf.append('\'');
            boolean inLiteral = false;
            while (i < length) {
                char c2 = pattern.charAt(i);
                if (c2 == '\'') {
                    if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
                        i++;
                        buf.append(c2);
                    } else {
                        inLiteral = !inLiteral;
                    }
                } else {
                    if (!inLiteral && ((c2 >= 'A' && c2 <= 'Z') || (c2 >= 'a' && c2 <= 'z'))) {
                        i--;
                        break;
                    }
                    buf.append(c2);
                }
                i++;
            }
        }
        indexRef[0] = i;
        return buf.toString();
    }

    protected NumberRule selectNumberRule(int field, int padding) {
        switch (padding) {
            case 1:
                return new UnpaddedNumberField(field);
            case 2:
                return new TwoDigitNumberField(field);
            default:
                return new PaddedNumberField(field, padding);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String format(Object obj) {
        if (obj instanceof Date) {
            return format((Date) obj);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue());
        }
        throw new IllegalArgumentException("Unknown class: " + (obj == null ? "<null>" : obj.getClass().getName()));
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public String format(long millis) {
        Calendar c = Calendar.getInstance(this.timeZone, this.locale);
        c.setTimeInMillis(millis);
        return applyRulesToString(c);
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public String format(Date date) {
        Calendar c = Calendar.getInstance(this.timeZone, this.locale);
        c.setTime(date);
        return applyRulesToString(c);
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public String format(Calendar calendar) {
        return ((StringBuilder) format(calendar, (Calendar) new StringBuilder(this.mMaxLengthEstimate))).toString();
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public <B extends Appendable> B format(long j, B b) {
        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTimeInMillis(j);
        return (B) applyRules(calendar, b);
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public <B extends Appendable> B format(Date date, B b) {
        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTime(date);
        return (B) applyRules(calendar, b);
    }

    @Override // cn.hutool.core.date.format.DatePrinter
    public <B extends Appendable> B format(Calendar calendar, B b) {
        if (!calendar.getTimeZone().equals(this.timeZone)) {
            calendar = (Calendar) calendar.clone();
            calendar.setTimeZone(this.timeZone);
        }
        return (B) applyRules(calendar, b);
    }

    private String applyRulesToString(Calendar c) {
        return ((StringBuilder) applyRules(c, new StringBuilder(this.mMaxLengthEstimate))).toString();
    }

    private <B extends Appendable> B applyRules(Calendar calendar, B buf) {
        try {
            for (Rule rule : this.rules) {
                rule.appendTo(buf, calendar);
            }
            return buf;
        } catch (IOException e) {
            throw new DateException(e);
        }
    }

    public int getMaxLengthEstimate() {
        return this.mMaxLengthEstimate;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void appendDigits(Appendable buffer, int value) throws IOException {
        buffer.append((char) ((value / 10) + 48));
        buffer.append((char) ((value % 10) + 48));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:16:0x003f. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0078  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x008f  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x009e  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00b5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void appendFullDigits(java.lang.Appendable r5, int r6, int r7) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 284
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: cn.hutool.core.date.format.FastDatePrinter.appendFullDigits(java.lang.Appendable, int, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$CharacterLiteral.class */
    public static class CharacterLiteral implements Rule {
        private final char mValue;

        CharacterLiteral(char value) {
            this.mValue = value;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return 1;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$StringLiteral.class */
    public static class StringLiteral implements Rule {
        private final String mValue;

        StringLiteral(String value) {
            this.mValue = value;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mValue.length();
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$TextField.class */
    public static class TextField implements Rule {
        private final int mField;
        private final String[] mValues;

        TextField(int field, String[] values) {
            this.mField = field;
            this.mValues = values;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            int max = 0;
            int i = this.mValues.length;
            while (true) {
                i--;
                if (i >= 0) {
                    int len = this.mValues[i].length();
                    if (len > max) {
                        max = len;
                    }
                } else {
                    return max;
                }
            }
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            buffer.append(this.mValues[calendar.get(this.mField)]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$UnpaddedNumberField.class */
    public static class UnpaddedNumberField implements NumberRule {
        private final int mField;

        UnpaddedNumberField(int field) {
            this.mField = field;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return 4;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value < 10) {
                buffer.append((char) (value + 48));
            } else if (value < 100) {
                FastDatePrinter.appendDigits(buffer, value);
            } else {
                FastDatePrinter.appendFullDigits(buffer, value, 1);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$UnpaddedMonthField.class */
    public static class UnpaddedMonthField implements NumberRule {
        static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

        UnpaddedMonthField() {
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(2) + 1);
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value >= 10) {
                FastDatePrinter.appendDigits(buffer, value);
            } else {
                buffer.append((char) (value + 48));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$PaddedNumberField.class */
    public static class PaddedNumberField implements NumberRule {
        private final int mField;
        private final int mSize;

        PaddedNumberField(int field, int size) {
            if (size < 3) {
                throw new IllegalArgumentException();
            }
            this.mField = field;
            this.mSize = size;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mSize;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendFullDigits(buffer, value, this.mSize);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$TwoDigitNumberField.class */
    public static class TwoDigitNumberField implements NumberRule {
        private final int mField;

        TwoDigitNumberField(int field) {
            this.mField = field;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(this.mField));
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            if (value < 100) {
                FastDatePrinter.appendDigits(buffer, value);
            } else {
                FastDatePrinter.appendFullDigits(buffer, value, 2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$TwoDigitYearField.class */
    public static class TwoDigitYearField implements NumberRule {
        static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

        TwoDigitYearField() {
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(1) % 100);
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendDigits(buffer, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$TwoDigitMonthField.class */
    public static class TwoDigitMonthField implements NumberRule {
        static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

        TwoDigitMonthField() {
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return 2;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(2) + 1);
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.NumberRule
        public final void appendTo(Appendable buffer, int value) throws IOException {
            FastDatePrinter.appendDigits(buffer, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$TwelveHourField.class */
    public static class TwelveHourField implements NumberRule {
        private final NumberRule mRule;

        TwelveHourField(NumberRule rule) {
            this.mRule = rule;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int value = calendar.get(10);
            if (value == 0) {
                value = calendar.getLeastMaximum(10) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.NumberRule
        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$TwentyFourHourField.class */
    public static class TwentyFourHourField implements NumberRule {
        private final NumberRule mRule;

        TwentyFourHourField(NumberRule rule) {
            this.mRule = rule;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int value = calendar.get(11);
            if (value == 0) {
                value = calendar.getMaximum(11) + 1;
            }
            this.mRule.appendTo(buffer, value);
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.NumberRule
        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$DayInWeekField.class */
    public static class DayInWeekField implements NumberRule {
        private final NumberRule mRule;

        DayInWeekField(NumberRule rule) {
            this.mRule = rule;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int value = calendar.get(7);
            this.mRule.appendTo(buffer, value != 1 ? value - 1 : 7);
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.NumberRule
        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$WeekYear.class */
    public static class WeekYear implements NumberRule {
        private final NumberRule mRule;

        WeekYear(NumberRule rule) {
            this.mRule = rule;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            this.mRule.appendTo(buffer, calendar.getWeekYear());
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.NumberRule
        public void appendTo(Appendable buffer, int value) throws IOException {
            this.mRule.appendTo(buffer, value);
        }
    }

    static String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
        TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
        String value = C_TIME_ZONE_DISPLAY_CACHE.get(key);
        if (value == null) {
            value = tz.getDisplayName(daylight, style, locale);
            String prior = C_TIME_ZONE_DISPLAY_CACHE.putIfAbsent(key, value);
            if (prior != null) {
                value = prior;
            }
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$TimeZoneNameRule.class */
    public static class TimeZoneNameRule implements Rule {
        private final Locale mLocale;
        private final int mStyle;
        private final String mStandard;
        private final String mDaylight;

        TimeZoneNameRule(TimeZone timeZone, Locale locale, int style) {
            this.mLocale = locale;
            this.mStyle = style;
            this.mStandard = FastDatePrinter.getTimeZoneDisplay(timeZone, false, style, locale);
            this.mDaylight = FastDatePrinter.getTimeZoneDisplay(timeZone, true, style, locale);
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return Math.max(this.mStandard.length(), this.mDaylight.length());
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            TimeZone zone = calendar.getTimeZone();
            if (calendar.get(16) != 0) {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, true, this.mStyle, this.mLocale));
            } else {
                buffer.append(FastDatePrinter.getTimeZoneDisplay(zone, false, this.mStyle, this.mLocale));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$TimeZoneNumberRule.class */
    public static class TimeZoneNumberRule implements Rule {
        static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
        static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
        final boolean mColon;

        TimeZoneNumberRule(boolean colon) {
            this.mColon = colon;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return 5;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            } else {
                buffer.append('+');
            }
            int hours = offset / 3600000;
            FastDatePrinter.appendDigits(buffer, hours);
            if (this.mColon) {
                buffer.append(':');
            }
            int minutes = (offset / Constants.DEFAULT_CONNECTION_TIMEOUT) - (60 * hours);
            FastDatePrinter.appendDigits(buffer, minutes);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$Iso8601_Rule.class */
    public static class Iso8601_Rule implements Rule {
        static final Iso8601_Rule ISO8601_HOURS = new Iso8601_Rule(3);
        static final Iso8601_Rule ISO8601_HOURS_MINUTES = new Iso8601_Rule(5);
        static final Iso8601_Rule ISO8601_HOURS_COLON_MINUTES = new Iso8601_Rule(6);
        final int length;

        static Iso8601_Rule getRule(int tokenLen) {
            switch (tokenLen) {
                case 1:
                    return ISO8601_HOURS;
                case 2:
                    return ISO8601_HOURS_MINUTES;
                case 3:
                    return ISO8601_HOURS_COLON_MINUTES;
                default:
                    throw new IllegalArgumentException("invalid number of X");
            }
        }

        Iso8601_Rule(int length) {
            this.length = length;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public int estimateLength() {
            return this.length;
        }

        @Override // cn.hutool.core.date.format.FastDatePrinter.Rule
        public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
            int offset = calendar.get(15) + calendar.get(16);
            if (offset == 0) {
                buffer.append("Z");
                return;
            }
            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            } else {
                buffer.append('+');
            }
            int hours = offset / 3600000;
            FastDatePrinter.appendDigits(buffer, hours);
            if (this.length < 5) {
                return;
            }
            if (this.length == 6) {
                buffer.append(':');
            }
            int minutes = (offset / Constants.DEFAULT_CONNECTION_TIMEOUT) - (60 * hours);
            FastDatePrinter.appendDigits(buffer, minutes);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/date/format/FastDatePrinter$TimeZoneDisplayKey.class */
    public static class TimeZoneDisplayKey {
        private final TimeZone mTimeZone;
        private final int mStyle;
        private final Locale mLocale;

        TimeZoneDisplayKey(TimeZone timeZone, boolean daylight, int style, Locale locale) {
            this.mTimeZone = timeZone;
            if (daylight) {
                this.mStyle = style | Integer.MIN_VALUE;
            } else {
                this.mStyle = style;
            }
            this.mLocale = locale;
        }

        public int hashCode() {
            return (((this.mStyle * 31) + this.mLocale.hashCode()) * 31) + this.mTimeZone.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof TimeZoneDisplayKey) {
                TimeZoneDisplayKey other = (TimeZoneDisplayKey) obj;
                return this.mTimeZone.equals(other.mTimeZone) && this.mStyle == other.mStyle && this.mLocale.equals(other.mLocale);
            }
            return false;
        }
    }
}
