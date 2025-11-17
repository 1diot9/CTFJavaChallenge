package cn.hutool.core.text;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/PasswdStrength.class */
public class PasswdStrength {
    private static final String[] DICTIONARY = {"password", "abc123", "iloveyou", "adobe123", "123123", "sunshine", "1314520", "a1b2c3", "123qwe", "aaa111", "qweasd", "admin", "passwd"};
    private static final int[] SIZE_TABLE = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/PasswdStrength$CHAR_TYPE.class */
    public enum CHAR_TYPE {
        NUM,
        SMALL_LETTER,
        CAPITAL_LETTER,
        OTHER_CHAR
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/PasswdStrength$PASSWD_LEVEL.class */
    public enum PASSWD_LEVEL {
        EASY,
        MIDIUM,
        STRONG,
        VERY_STRONG,
        EXTREMELY_STRONG
    }

    public static int check(String passwd) {
        if (null == passwd) {
            throw new IllegalArgumentException("password is empty");
        }
        int len = passwd.length();
        int level = 0;
        if (countLetter(passwd, CHAR_TYPE.NUM) > 0) {
            level = 0 + 1;
        }
        if (countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0) {
            level++;
        }
        if (len > 4 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0) {
            level++;
        }
        if (len > 6 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) {
            level++;
        }
        if ((len > 4 && countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0) || ((countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0) || ((countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) || ((countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0) || ((countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) || (countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0)))))) {
            level++;
        }
        if ((len > 6 && countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0) || ((countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) || ((countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) || (countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0)))) {
            level++;
        }
        if (len > 8 && countLetter(passwd, CHAR_TYPE.NUM) > 0 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) > 0 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) > 0) {
            level++;
        }
        if ((len > 6 && countLetter(passwd, CHAR_TYPE.NUM) >= 3 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 3) || ((countLetter(passwd, CHAR_TYPE.NUM) >= 3 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 3) || ((countLetter(passwd, CHAR_TYPE.NUM) >= 3 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2) || ((countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 3 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 3) || ((countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 3 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2) || (countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 3 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2)))))) {
            level++;
        }
        if ((len > 8 && countLetter(passwd, CHAR_TYPE.NUM) >= 2 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 2 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 2) || ((countLetter(passwd, CHAR_TYPE.NUM) >= 2 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 2 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2) || ((countLetter(passwd, CHAR_TYPE.NUM) >= 2 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 2 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2) || (countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 2 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 2 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2)))) {
            level++;
        }
        if (len > 10 && countLetter(passwd, CHAR_TYPE.NUM) >= 2 && countLetter(passwd, CHAR_TYPE.SMALL_LETTER) >= 2 && countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) >= 2 && countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 2) {
            level++;
        }
        if (countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 3) {
            level++;
        }
        if (countLetter(passwd, CHAR_TYPE.OTHER_CHAR) >= 6) {
            level++;
        }
        if (len > 12) {
            level++;
            if (len >= 16) {
                level++;
            }
        }
        if (RandomUtil.BASE_CHAR.indexOf(passwd) > 0 || "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(passwd) > 0) {
            level--;
        }
        if ("qwertyuiop".indexOf(passwd) > 0 || "asdfghjkl".indexOf(passwd) > 0 || "zxcvbnm".indexOf(passwd) > 0) {
            level--;
        }
        if (StrUtil.isNumeric(passwd) && ("01234567890".indexOf(passwd) > 0 || "09876543210".indexOf(passwd) > 0)) {
            level--;
        }
        if (countLetter(passwd, CHAR_TYPE.NUM) == len || countLetter(passwd, CHAR_TYPE.SMALL_LETTER) == len || countLetter(passwd, CHAR_TYPE.CAPITAL_LETTER) == len) {
            level--;
        }
        if (len % 2 == 0) {
            String part1 = passwd.substring(0, len / 2);
            String part2 = passwd.substring(len / 2);
            if (part1.equals(part2)) {
                level--;
            }
            if (StrUtil.isCharEquals(part1) && StrUtil.isCharEquals(part2)) {
                level--;
            }
        }
        if (len % 3 == 0) {
            String part12 = passwd.substring(0, len / 3);
            String part22 = passwd.substring(len / 3, (len / 3) * 2);
            String part3 = passwd.substring((len / 3) * 2);
            if (part12.equals(part22) && part22.equals(part3)) {
                level--;
            }
        }
        if (StrUtil.isNumeric(passwd) && len >= 6 && len <= 8) {
            int year = 0;
            if (len == 8 || len == 6) {
                year = Integer.parseInt(passwd.substring(0, len - 4));
            }
            int size = sizeOfInt(year);
            int month = Integer.parseInt(passwd.substring(size, size + 2));
            int day = Integer.parseInt(passwd.substring(size + 2, len));
            if (year >= 1950 && year < 2050 && month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                level--;
            }
        }
        for (String s : DICTIONARY) {
            if (passwd.equals(s) || s.contains(passwd)) {
                level--;
                break;
            }
        }
        if (len <= 6) {
            level--;
            if (len <= 4) {
                level--;
                if (len <= 3) {
                    level = 0;
                }
            }
        }
        if (StrUtil.isCharEquals(passwd)) {
            level = 0;
        }
        if (level < 0) {
            level = 0;
        }
        return level;
    }

    public static PASSWD_LEVEL getLevel(String passwd) {
        int level = check(passwd);
        switch (level) {
            case 0:
            case 1:
            case 2:
            case 3:
                return PASSWD_LEVEL.EASY;
            case 4:
            case 5:
            case 6:
                return PASSWD_LEVEL.MIDIUM;
            case 7:
            case 8:
            case 9:
                return PASSWD_LEVEL.STRONG;
            case 10:
            case 11:
            case 12:
                return PASSWD_LEVEL.VERY_STRONG;
            default:
                return PASSWD_LEVEL.EXTREMELY_STRONG;
        }
    }

    private static CHAR_TYPE checkCharacterType(char c) {
        if (c >= '0' && c <= '9') {
            return CHAR_TYPE.NUM;
        }
        if (c >= 'A' && c <= 'Z') {
            return CHAR_TYPE.CAPITAL_LETTER;
        }
        if (c >= 'a' && c <= 'z') {
            return CHAR_TYPE.SMALL_LETTER;
        }
        return CHAR_TYPE.OTHER_CHAR;
    }

    private static int countLetter(String passwd, CHAR_TYPE type) {
        int length;
        int count = 0;
        if (null != passwd && (length = passwd.length()) > 0) {
            for (int i = 0; i < length; i++) {
                if (checkCharacterType(passwd.charAt(i)) == type) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int sizeOfInt(int x) {
        int i = 0;
        while (x > SIZE_TABLE[i]) {
            i++;
        }
        return i + 1;
    }
}
