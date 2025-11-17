package ch.qos.logback.core.util;

import java.text.DateFormatSymbols;
import org.apache.el.parser.ELParserConstants;
import org.apache.tomcat.util.codec.binary.BaseNCodec;
import org.springframework.asm.Opcodes;
import org.springframework.asm.TypeReference;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/CharSequenceToRegexMapper.class */
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
            case ELParserConstants.EMPTY /* 43 */:
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
            case Opcodes.V18 /* 62 */:
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'I':
            case 'J':
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
            case 'N':
            case Opcodes.IASTORE /* 79 */:
            case 'P':
            case Opcodes.FASTORE /* 81 */:
            case Opcodes.DASTORE /* 82 */:
            case Opcodes.BASTORE /* 84 */:
            case Opcodes.CASTORE /* 85 */:
            case Opcodes.SASTORE /* 86 */:
            case 'X':
            case Opcodes.DUP /* 89 */:
            case '[':
            case ']':
            case Opcodes.DUP2_X2 /* 94 */:
            case Opcodes.SWAP /* 95 */:
            case '`':
            case Opcodes.FADD /* 98 */:
            case 'c':
            case 'e':
            case Opcodes.FSUB /* 102 */:
            case Opcodes.DSUB /* 103 */:
            case Opcodes.LMUL /* 105 */:
            case Opcodes.FMUL /* 106 */:
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
            case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT /* 75 */:
            case 'S':
            case Opcodes.POP /* 87 */:
            case 'd':
            case 'h':
            case Opcodes.DMUL /* 107 */:
            case Opcodes.LDIV /* 109 */:
            case 's':
            case Opcodes.DNEG /* 119 */:
            case Opcodes.LSHL /* 121 */:
                return number(occurrences);
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                if (occurrences >= 4) {
                    return getRegexForLongDaysOfTheWeek();
                }
                return getRegexForShortDaysOfTheWeek();
            case TypeReference.CAST /* 71 */:
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
