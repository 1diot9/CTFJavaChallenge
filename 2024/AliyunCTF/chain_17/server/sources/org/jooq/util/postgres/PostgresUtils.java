package org.jooq.util.postgres;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jooq.EnumType;
import org.jooq.Record;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.Internal;
import org.jooq.tools.StringUtils;
import org.jooq.types.DayToSecond;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/postgres/PostgresUtils.class */
public class PostgresUtils {
    private static final String POSTGRESQL_HEX_STRING_PREFIX = "\\x";

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/postgres/PostgresUtils$PGState.class */
    public enum PGState {
        PG_OBJECT_INIT,
        PG_OBJECT_BEFORE_VALUE,
        PG_OBJECT_QUOTED_VALUE,
        PG_OBJECT_UNQUOTED_VALUE,
        PG_OBJECT_NESTED_VALUE,
        PG_OBJECT_NESTED_QUOTED_VALUE,
        PG_OBJECT_AFTER_VALUE,
        PG_OBJECT_END
    }

    public static byte[] toBytes(String string) {
        if (string.startsWith(POSTGRESQL_HEX_STRING_PREFIX)) {
            return toBytesFromHexEncoding(string);
        }
        return toBytesFromOctalEncoding(string);
    }

    private static byte[] toBytesFromOctalEncoding(String string) {
        Reader reader = new StringReader(string);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            convertOctalToBytes(reader, bytes);
            return bytes.toByteArray();
        } catch (IOException x) {
            throw new DataTypeException("failed to parse octal hex string: " + x.getMessage(), x);
        }
    }

    private static void convertOctalToBytes(Reader reader, OutputStream bytes) throws IOException {
        while (true) {
            int ch2 = reader.read();
            if (ch2 != -1) {
                if (ch2 == 92) {
                    int ch3 = reader.read();
                    if (ch3 == -1) {
                        throw new DataTypeException("unexpected end of stream after initial backslash");
                    }
                    if (ch3 == 92) {
                        bytes.write(92);
                    } else {
                        int val = octalValue(ch3);
                        int ch4 = reader.read();
                        if (ch4 == -1) {
                            throw new DataTypeException("unexpected end of octal value");
                        }
                        int val2 = (val << 3) + octalValue(ch4);
                        int ch5 = reader.read();
                        if (ch5 == -1) {
                            throw new DataTypeException("unexpected end of octal value");
                        }
                        bytes.write((val2 << 3) + octalValue(ch5));
                    }
                } else {
                    bytes.write(ch2);
                }
            } else {
                return;
            }
        }
    }

    private static byte[] toBytesFromHexEncoding(String string) {
        String hex = string.substring(POSTGRESQL_HEX_STRING_PREFIX.length());
        StringReader input = new StringReader(hex);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(hex.length() / 2);
        while (true) {
            try {
                int hexDigit = input.read();
                if (hexDigit == -1) {
                    break;
                }
                int byteValue = hexValue(hexDigit) << 4;
                int hexDigit2 = input.read();
                if (hexDigit2 == -1) {
                    break;
                }
                bytes.write(byteValue + hexValue(hexDigit2));
            } catch (IOException e) {
                throw new DataTypeException("Error while decoding hex string", e);
            }
        }
        input.close();
        return bytes.toByteArray();
    }

    private static int hexValue(int hexDigit) {
        if (hexDigit >= 48 && hexDigit <= 57) {
            return hexDigit - 48;
        }
        if (hexDigit >= 97 && hexDigit <= 102) {
            return (hexDigit - 97) + 10;
        }
        if (hexDigit >= 65 && hexDigit <= 70) {
            return (hexDigit - 65) + 10;
        }
        throw new DataTypeException("unknown postgresql character format for hexValue: " + hexDigit);
    }

    private static int octalValue(int octalDigit) {
        if (octalDigit < 48 || octalDigit > 55) {
            throw new DataTypeException("unknown postgresql character format for octalValue: " + octalDigit);
        }
        return octalDigit - 48;
    }

    public static PGInterval toPGInterval(DayToSecond interval) {
        return new PGInterval(0, 0, interval.getSign() * interval.getDays(), interval.getSign() * interval.getHours(), interval.getSign() * interval.getMinutes(), (interval.getSign() * interval.getSeconds()) + ((interval.getSign() * interval.getNano()) / 1.0E9d));
    }

    public static PGInterval toPGInterval(YearToSecond interval) {
        return new PGInterval(interval.getSign() * interval.getYears(), interval.getSign() * interval.getMonths(), interval.getSign() * interval.getDays(), interval.getSign() * interval.getHours(), interval.getSign() * interval.getMinutes(), (interval.getSign() * interval.getSeconds()) + ((interval.getSign() * interval.getNano()) / 1.0E9d));
    }

    public static PGInterval toPGInterval(YearToMonth interval) {
        return new PGInterval(interval.getSign() * interval.getYears(), interval.getSign() * interval.getMonths(), 0, 0, 0, 0.0d);
    }

    public static DayToSecond toDayToSecond(Object pgInterval) {
        if (pgInterval == null) {
            return null;
        }
        if (pgInterval instanceof PGInterval) {
            PGInterval i = (PGInterval) pgInterval;
            return toDayToSecond(i);
        }
        return toDayToSecond(new PGInterval(pgInterval.toString()));
    }

    public static DayToSecond toDayToSecond(PGInterval pgInterval) {
        boolean negative = pgInterval.toString().contains("-");
        if (negative) {
            pgInterval.scale(-1);
        }
        Double seconds = Double.valueOf(pgInterval.getSeconds());
        DayToSecond result = new DayToSecond(pgInterval.getDays(), pgInterval.getHours(), pgInterval.getMinutes(), seconds.intValue(), (int) (1.0E9d * (seconds.doubleValue() - seconds.intValue())));
        if (negative) {
            result = result.neg();
        }
        return result;
    }

    public static YearToMonth toYearToMonth(Object pgInterval) {
        if (pgInterval == null) {
            return null;
        }
        if (pgInterval instanceof PGInterval) {
            PGInterval i = (PGInterval) pgInterval;
            return toYearToMonth(i);
        }
        return toYearToMonth(new PGInterval(pgInterval.toString()));
    }

    public static YearToMonth toYearToMonth(PGInterval pgInterval) {
        boolean negative = pgInterval.toString().contains("-");
        if (negative) {
            pgInterval.scale(-1);
        }
        YearToMonth result = new YearToMonth(pgInterval.getYears(), pgInterval.getMonths());
        if (negative) {
            result = result.neg();
        }
        return result;
    }

    public static YearToSecond toYearToSecond(Object pgInterval) {
        return new YearToSecond(toYearToMonth(pgInterval), toDayToSecond(pgInterval));
    }

    public static List<String> toPGArray(String input) {
        if ("{}".equals(input)) {
            return Collections.emptyList();
        }
        return toPGObjectOrArray(input, '{', '}');
    }

    public static List<String> toPGObject(String input) {
        return toPGObjectOrArray(input, '(', ')');
    }

    private static List<String> toPGObjectOrArray(String input, char open, char close) {
        List<String> values = new ArrayList<>();
        int i = 0;
        PGState state = PGState.PG_OBJECT_INIT;
        int nestLevel = 0;
        StringBuilder sb = null;
        while (i < input.length()) {
            char c = input.charAt(i);
            switch (state) {
                case PG_OBJECT_INIT:
                    if (c != open) {
                        break;
                    } else {
                        state = PGState.PG_OBJECT_BEFORE_VALUE;
                        break;
                    }
                case PG_OBJECT_BEFORE_VALUE:
                    sb = new StringBuilder();
                    if (c == ',') {
                        values.add(null);
                        state = PGState.PG_OBJECT_BEFORE_VALUE;
                        break;
                    } else if (c == close) {
                        values.add(null);
                        state = PGState.PG_OBJECT_END;
                        break;
                    } else if (c == '\"') {
                        state = PGState.PG_OBJECT_QUOTED_VALUE;
                        break;
                    } else if ((c == 'n' || c == 'N') && i + 4 < input.length() && open == '{' && input.substring(i, i + 4).equalsIgnoreCase("null")) {
                        values.add(null);
                        i += 3;
                        state = PGState.PG_OBJECT_AFTER_VALUE;
                        break;
                    } else if (c == open) {
                        sb.append(c);
                        state = PGState.PG_OBJECT_NESTED_VALUE;
                        nestLevel++;
                        break;
                    } else {
                        sb.append(c);
                        state = PGState.PG_OBJECT_UNQUOTED_VALUE;
                        break;
                    }
                case PG_OBJECT_QUOTED_VALUE:
                    if (c == '\"') {
                        if (input.charAt(i + 1) == '\"') {
                            sb.append(c);
                            i++;
                            break;
                        } else {
                            values.add(sb.toString());
                            state = PGState.PG_OBJECT_AFTER_VALUE;
                            break;
                        }
                    } else if (c == '\\') {
                        char n = input.charAt(i + 1);
                        if (n == '\\' || n == '\"') {
                            sb.append(n);
                            i++;
                            break;
                        } else {
                            sb.append(c);
                            break;
                        }
                    } else {
                        sb.append(c);
                        break;
                    }
                    break;
                case PG_OBJECT_UNQUOTED_VALUE:
                    if (c == close) {
                        values.add(sb.toString());
                        state = PGState.PG_OBJECT_END;
                        break;
                    } else if (c == ',') {
                        values.add(sb.toString());
                        state = PGState.PG_OBJECT_BEFORE_VALUE;
                        break;
                    } else {
                        sb.append(c);
                        break;
                    }
                case PG_OBJECT_NESTED_VALUE:
                    if (c == close) {
                        nestLevel--;
                        sb.append(c);
                        if (nestLevel != 0) {
                            break;
                        } else {
                            values.add(sb.toString());
                            state = PGState.PG_OBJECT_AFTER_VALUE;
                            break;
                        }
                    } else if (c == open) {
                        nestLevel++;
                        sb.append(c);
                        break;
                    } else if (c == '\"') {
                        state = PGState.PG_OBJECT_NESTED_QUOTED_VALUE;
                        sb.append(c);
                        break;
                    } else {
                        sb.append(c);
                        break;
                    }
                case PG_OBJECT_NESTED_QUOTED_VALUE:
                    if (c == '\"') {
                        if (input.charAt(i + 1) == '\"') {
                            sb.append(c);
                            sb.append(c);
                            i++;
                            break;
                        } else {
                            sb.append(c);
                            state = PGState.PG_OBJECT_NESTED_VALUE;
                            break;
                        }
                    } else if (c == '\\') {
                        char n2 = input.charAt(i + 1);
                        if (n2 == '\\' || n2 == '\"') {
                            sb.append(c);
                            sb.append(n2);
                            i++;
                            break;
                        } else {
                            sb.append(c);
                            break;
                        }
                    } else {
                        sb.append(c);
                        break;
                    }
                    break;
                case PG_OBJECT_AFTER_VALUE:
                    if (c == close) {
                        state = PGState.PG_OBJECT_END;
                        break;
                    } else if (c != ',') {
                        break;
                    } else {
                        state = PGState.PG_OBJECT_BEFORE_VALUE;
                        break;
                    }
            }
            i++;
        }
        return values;
    }

    public static String toPGArrayString(Object[] value) {
        return toPGArrayString0(value, new StringBuilder()).toString();
    }

    private static StringBuilder toPGArrayString0(Object[] value, StringBuilder sb) {
        sb.append("{");
        String separator = "";
        for (Object o : value) {
            sb.append(separator);
            if (o == null) {
                sb.append(o);
            } else if (o instanceof Number) {
                sb.append(toPGString(o));
            } else if (o instanceof byte[]) {
                toPGString0((byte[]) o, sb);
            } else if (o instanceof Object[]) {
                Object[] a = (Object[]) o;
                if (!isDeepEmpty(a)) {
                    toPGArrayString0(a, sb);
                }
            } else {
                sb.append("\"").append(StringUtils.replace(StringUtils.replace(toPGString(o), "\\", "\\\\"), "\"", "\\\"")).append("\"");
            }
            separator = ",";
        }
        sb.append("}");
        return sb;
    }

    private static boolean isDeepEmpty(Object[] a) {
        if (a.length == 0) {
            return true;
        }
        if (a.length != 1) {
            return false;
        }
        Object obj = a[0];
        if (obj instanceof Object[]) {
            Object[] b = (Object[]) obj;
            return isDeepEmpty(b);
        }
        return false;
    }

    public static String toPGString(Object o) {
        if (o instanceof byte[]) {
            return toPGString((byte[]) o);
        }
        if (o instanceof Object[]) {
            return toPGArrayString((Object[]) o);
        }
        if (o instanceof Record) {
            return toPGString((Record) o);
        }
        if (o instanceof EnumType) {
            return ((EnumType) o).getLiteral();
        }
        return String.valueOf(o);
    }

    public static String toPGString(Record r) {
        return toPGString0(r, new StringBuilder()).toString();
    }

    private static StringBuilder toPGString0(Record r, StringBuilder sb) {
        sb.append("(");
        String separator = "";
        for (int i = 0; i < r.size(); i++) {
            Object a = r.field(i).getConverter().to(r.get(i), Internal.converterContext());
            sb.append(separator);
            if (a != null) {
                if (a instanceof byte[]) {
                    toPGString0((byte[]) a, sb);
                } else {
                    sb.append("\"").append(StringUtils.replace(StringUtils.replace(toPGString(a), "\\", "\\\\"), "\"", "\\\"")).append("\"");
                }
            }
            separator = ",";
        }
        sb.append(")");
        return sb;
    }

    public static String toPGString(byte[] binary) {
        return toPGString0(binary, new StringBuilder()).toString();
    }

    private static StringBuilder toPGString0(byte[] binary, StringBuilder sb) {
        for (byte b : binary) {
            sb.append("\\\\");
            sb.append(StringUtils.leftPad(Integer.toOctalString(b & 255), 3, '0'));
        }
        return sb;
    }
}
