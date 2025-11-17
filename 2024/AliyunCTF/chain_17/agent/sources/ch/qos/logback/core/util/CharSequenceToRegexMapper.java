package ch.qos.logback.core.util;

import java.text.DateFormatSymbols;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/CharSequenceToRegexMapper.class */
class CharSequenceToRegexMapper {
    DateFormatSymbols symbols = DateFormatSymbols.getInstance();

    /* JADX INFO: Access modifiers changed from: package-private */
    public String toRegex(CharSequenceState css) {
        int occurrences = css.occurrences;
        char c = css.c;
        switch (css.c) {
            case '\'':
                if (occurrences == 1) {
                    return "";
                }
                throw new IllegalStateException("Too many single quotes");
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
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
            case 'X':
            case 'Y':
            case '[':
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
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case 'x':
            default:
                if (occurrences == 1) {
                    return c;
                }
                return c + "{" + occurrences + "}";
            case '.':
                return "\\.";
            case 'D':
            case 'F':
            case 'H':
            case 'K':
            case 'S':
            case 'W':
            case 'd':
            case 'h':
            case Opcodes.DMUL /* 107 */:
            case Opcodes.LDIV /* 109 */:
            case 's':
            case Opcodes.DNEG /* 119 */:
            case Opcodes.LSHL /* 121 */:
                return number(occurrences);
            case 'E':
                if (occurrences >= 4) {
                    return getRegexForLongDaysOfTheWeek();
                }
                return getRegexForShortDaysOfTheWeek();
            case 'G':
            case 'z':
                return ".*";
            case 'M':
                if (occurrences <= 2) {
                    return number(occurrences);
                }
                if (occurrences == 3) {
                    return getRegexForShortMonths();
                }
                return getRegexForLongMonths();
            case 'Z':
                return "(\\+|-)\\d{4}";
            case '\\':
                throw new IllegalStateException("Forward slashes are not allowed");
            case 'a':
                return getRegexForAmPms();
        }
    }

    private String number(int occurrences) {
        return "\\d{" + occurrences + "}";
    }

    private String getRegexForAmPms() {
        return symbolArrayToRegex(this.symbols.getAmPmStrings());
    }

    private String getRegexForLongDaysOfTheWeek() {
        return symbolArrayToRegex(this.symbols.getWeekdays());
    }

    private String getRegexForShortDaysOfTheWeek() {
        return symbolArrayToRegex(this.symbols.getShortWeekdays());
    }

    private String getRegexForLongMonths() {
        return symbolArrayToRegex(this.symbols.getMonths());
    }

    String getRegexForShortMonths() {
        return symbolArrayToRegex(this.symbols.getShortMonths());
    }

    private String symbolArrayToRegex(String[] symbolArray) {
        int[] minMax = findMinMaxLengthsInSymbols(symbolArray);
        return ".{" + minMax[0] + "," + minMax[1] + "}";
    }

    static int[] findMinMaxLengthsInSymbols(String[] symbols) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (String symbol : symbols) {
            int len = symbol.length();
            if (len != 0) {
                min = Math.min(min, len);
                max = Math.max(max, len);
            }
        }
        return new int[]{min, max};
    }
}
