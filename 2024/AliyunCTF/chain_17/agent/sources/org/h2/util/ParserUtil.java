package org.h2.util;

import ch.qos.logback.core.joran.JoranConstants;
import java.util.HashMap;
import org.h2.engine.Constants;
import org.h2.table.Column;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/ParserUtil.class */
public class ParserUtil {
    public static final int KEYWORD = 1;
    public static final int IDENTIFIER = 2;
    public static final int ALL = 3;
    public static final int AND = 4;
    public static final int ANY = 5;
    public static final int ARRAY = 6;
    public static final int AS = 7;
    public static final int ASYMMETRIC = 8;
    public static final int AUTHORIZATION = 9;
    public static final int BETWEEN = 10;
    public static final int CASE = 11;
    public static final int CAST = 12;
    public static final int CHECK = 13;
    public static final int CONSTRAINT = 14;
    public static final int CROSS = 15;
    public static final int CURRENT_CATALOG = 16;
    public static final int CURRENT_DATE = 17;
    public static final int CURRENT_PATH = 18;
    public static final int CURRENT_ROLE = 19;
    public static final int CURRENT_SCHEMA = 20;
    public static final int CURRENT_TIME = 21;
    public static final int CURRENT_TIMESTAMP = 22;
    public static final int CURRENT_USER = 23;
    public static final int DAY = 24;
    public static final int DEFAULT = 25;
    public static final int DISTINCT = 26;
    public static final int ELSE = 27;
    public static final int END = 28;
    public static final int EXCEPT = 29;
    public static final int EXISTS = 30;
    public static final int FALSE = 31;
    public static final int FETCH = 32;
    public static final int FOR = 33;
    public static final int FOREIGN = 34;
    public static final int FROM = 35;
    public static final int FULL = 36;
    public static final int GROUP = 37;
    public static final int HAVING = 38;
    public static final int HOUR = 39;
    public static final int IF = 40;
    public static final int IN = 41;
    public static final int INNER = 42;
    public static final int INTERSECT = 43;
    public static final int INTERVAL = 44;
    public static final int IS = 45;
    public static final int JOIN = 46;
    public static final int KEY = 47;
    public static final int LEFT = 48;
    public static final int LIKE = 49;
    public static final int LIMIT = 50;
    public static final int LOCALTIME = 51;
    public static final int LOCALTIMESTAMP = 52;
    public static final int MINUS = 53;
    public static final int MINUTE = 54;
    public static final int MONTH = 55;
    public static final int NATURAL = 56;
    public static final int NOT = 57;
    public static final int NULL = 58;
    public static final int OFFSET = 59;
    public static final int ON = 60;
    public static final int OR = 61;
    public static final int ORDER = 62;
    public static final int PRIMARY = 63;
    public static final int QUALIFY = 64;
    public static final int RIGHT = 65;
    public static final int ROW = 66;
    public static final int ROWNUM = 67;
    public static final int SECOND = 68;
    public static final int SELECT = 69;
    public static final int SESSION_USER = 70;
    public static final int SET = 71;
    public static final int SOME = 72;
    public static final int SYMMETRIC = 73;
    public static final int SYSTEM_USER = 74;
    public static final int TABLE = 75;
    public static final int TO = 76;
    public static final int TRUE = 77;
    public static final int UESCAPE = 78;
    public static final int UNION = 79;
    public static final int UNIQUE = 80;
    public static final int UNKNOWN = 81;
    public static final int USER = 82;
    public static final int USING = 83;
    public static final int VALUE = 84;
    public static final int VALUES = 85;
    public static final int WHEN = 86;
    public static final int WHERE = 87;
    public static final int WINDOW = 88;
    public static final int WITH = 89;
    public static final int YEAR = 90;
    public static final int _ROWID_ = 91;
    public static final int FIRST_KEYWORD = 3;
    public static final int LAST_KEYWORD = 91;
    private static final HashMap<String, Integer> KEYWORDS;

    static {
        HashMap<String, Integer> hashMap = new HashMap<>(256);
        hashMap.put("ALL", 3);
        hashMap.put("AND", 4);
        hashMap.put("ANY", 5);
        hashMap.put("ARRAY", 6);
        hashMap.put("AS", 7);
        hashMap.put("ASYMMETRIC", 8);
        hashMap.put("AUTHORIZATION", 9);
        hashMap.put("BETWEEN", 10);
        hashMap.put("CASE", 11);
        hashMap.put("CAST", 12);
        hashMap.put("CHECK", 13);
        hashMap.put("CONSTRAINT", 14);
        hashMap.put("CROSS", 15);
        hashMap.put("CURRENT_CATALOG", 16);
        hashMap.put("CURRENT_DATE", 17);
        hashMap.put("CURRENT_PATH", 18);
        hashMap.put("CURRENT_ROLE", 19);
        hashMap.put("CURRENT_SCHEMA", 20);
        hashMap.put("CURRENT_TIME", 21);
        hashMap.put("CURRENT_TIMESTAMP", 22);
        hashMap.put("CURRENT_USER", 23);
        hashMap.put("DAY", 24);
        hashMap.put("DEFAULT", 25);
        hashMap.put("DISTINCT", 26);
        hashMap.put("ELSE", 27);
        hashMap.put("END", 28);
        hashMap.put("EXCEPT", 29);
        hashMap.put("EXISTS", 30);
        hashMap.put("FALSE", 31);
        hashMap.put("FETCH", 32);
        hashMap.put("FOR", 33);
        hashMap.put("FOREIGN", 34);
        hashMap.put("FROM", 35);
        hashMap.put("FULL", 36);
        hashMap.put("GROUP", 37);
        hashMap.put("HAVING", 38);
        hashMap.put("HOUR", 39);
        hashMap.put("IF", 40);
        hashMap.put("IN", 41);
        hashMap.put("INNER", 42);
        hashMap.put("INTERSECT", 43);
        hashMap.put("INTERVAL", 44);
        hashMap.put("IS", 45);
        hashMap.put("JOIN", 46);
        hashMap.put("KEY", 47);
        hashMap.put("LEFT", 48);
        hashMap.put("LIKE", 49);
        hashMap.put("LIMIT", 50);
        hashMap.put("LOCALTIME", 51);
        hashMap.put("LOCALTIMESTAMP", 52);
        hashMap.put("MINUS", 53);
        hashMap.put("MINUTE", 54);
        hashMap.put("MONTH", 55);
        hashMap.put("NATURAL", 56);
        hashMap.put("NOT", 57);
        hashMap.put(JoranConstants.NULL, 58);
        hashMap.put("OFFSET", 59);
        hashMap.put("ON", 60);
        hashMap.put("OR", 61);
        hashMap.put("ORDER", 62);
        hashMap.put("PRIMARY", 63);
        hashMap.put("QUALIFY", 64);
        hashMap.put("RIGHT", 65);
        hashMap.put("ROW", 66);
        hashMap.put("ROWNUM", 67);
        hashMap.put("SECOND", 68);
        hashMap.put("SELECT", 69);
        hashMap.put("SESSION_USER", 70);
        hashMap.put("SET", 71);
        hashMap.put("SOME", 72);
        hashMap.put("SYMMETRIC", 73);
        hashMap.put("SYSTEM_USER", 74);
        hashMap.put("TABLE", 75);
        hashMap.put("TO", 76);
        hashMap.put(Constants.CLUSTERING_ENABLED, 77);
        hashMap.put("UESCAPE", 78);
        hashMap.put("UNION", 79);
        hashMap.put("UNIQUE", 80);
        hashMap.put("UNKNOWN", 81);
        hashMap.put("USER", 82);
        hashMap.put("USING", 83);
        hashMap.put("VALUE", 84);
        hashMap.put("VALUES", 85);
        hashMap.put("WHEN", 86);
        hashMap.put("WHERE", 87);
        hashMap.put("WINDOW", 88);
        hashMap.put("WITH", 89);
        hashMap.put("YEAR", 90);
        hashMap.put(Column.ROWID, 91);
        hashMap.put("BOTH", 1);
        hashMap.put("GROUPS", 1);
        hashMap.put("ILIKE", 1);
        hashMap.put("LEADING", 1);
        hashMap.put("OVER", 1);
        hashMap.put("PARTITION", 1);
        hashMap.put("RANGE", 1);
        hashMap.put("REGEXP", 1);
        hashMap.put("ROWS", 1);
        hashMap.put("TOP", 1);
        hashMap.put("TRAILING", 1);
        KEYWORDS = hashMap;
    }

    private ParserUtil() {
    }

    public static StringBuilder quoteIdentifier(StringBuilder sb, String str, int i) {
        if (str == null) {
            return sb.append("\"\"");
        }
        if ((i & 1) != 0 && isSimpleIdentifier(str, false, false)) {
            return sb.append(str);
        }
        return StringUtils.quoteIdentifier(sb, str);
    }

    public static boolean isKeyword(String str, boolean z) {
        return getTokenType(str, z, false) != 2;
    }

    public static boolean isSimpleIdentifier(String str, boolean z, boolean z2) {
        if (z && z2) {
            throw new IllegalArgumentException("databaseToUpper && databaseToLower");
        }
        int length = str.length();
        if (length == 0 || !checkLetter(z, z2, str.charAt(0))) {
            return false;
        }
        for (int i = 1; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt != '_' && ((charAt < '0' || charAt > '9') && !checkLetter(z, z2, charAt))) {
                return false;
            }
        }
        return getTokenType(str, !z, true) == 2;
    }

    private static boolean checkLetter(boolean z, boolean z2, char c) {
        if (z) {
            if (c < 'A' || c > 'Z') {
                return false;
            }
            return true;
        }
        if (z2) {
            if (c < 'a' || c > 'z') {
                return false;
            }
            return true;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        if (c < 'a' || c > 'z') {
            return false;
        }
        return true;
    }

    public static int getTokenType(String str, boolean z, boolean z2) {
        int length = str.length();
        if (length <= 1 || length > 17) {
            return 2;
        }
        if (z) {
            str = StringUtils.toUpperEnglish(str);
        }
        Integer num = KEYWORDS.get(str);
        if (num == null) {
            return 2;
        }
        int intValue = num.intValue();
        if (intValue != 1 || z2) {
            return intValue;
        }
        return 2;
    }
}
