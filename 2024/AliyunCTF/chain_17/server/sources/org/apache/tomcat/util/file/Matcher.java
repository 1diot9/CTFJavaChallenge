package org.apache.tomcat.util.file;

import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/file/Matcher.class */
public final class Matcher {
    public static boolean matchName(Set<String> patternSet, String fileName) {
        char[] fileNameArray = fileName.toCharArray();
        for (String pattern : patternSet) {
            if (match(pattern, fileNameArray, true)) {
                return true;
            }
        }
        return false;
    }

    public static boolean match(String pattern, String str, boolean caseSensitive) {
        return match(pattern, str.toCharArray(), caseSensitive);
    }

    private static boolean match(String pattern, char[] strArr, boolean caseSensitive) {
        int j;
        char[] patArr = pattern.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        boolean containsStar = false;
        int length = patArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            char c = patArr[i];
            if (c != '*') {
                i++;
            } else {
                containsStar = true;
                break;
            }
        }
        if (!containsStar) {
            if (patIdxEnd != strIdxEnd) {
                return false;
            }
            for (int i2 = 0; i2 <= patIdxEnd; i2++) {
                char ch2 = patArr[i2];
                if (ch2 != '?' && different(caseSensitive, ch2, strArr[i2])) {
                    return false;
                }
            }
            return true;
        }
        if (patIdxEnd == 0) {
            return true;
        }
        while (true) {
            char ch3 = patArr[patIdxStart];
            if (ch3 == '*' || strIdxStart > strIdxEnd) {
                break;
            }
            if (ch3 != '?' && different(caseSensitive, ch3, strArr[strIdxStart])) {
                return false;
            }
            patIdxStart++;
            strIdxStart++;
        }
        if (strIdxStart > strIdxEnd) {
            return allStars(patArr, patIdxStart, patIdxEnd);
        }
        while (true) {
            char ch4 = patArr[patIdxEnd];
            if (ch4 == '*' || strIdxStart > strIdxEnd) {
                break;
            }
            if (ch4 != '?' && different(caseSensitive, ch4, strArr[strIdxEnd])) {
                return false;
            }
            patIdxEnd--;
            strIdxEnd--;
        }
        if (strIdxStart > strIdxEnd) {
            return allStars(patArr, patIdxStart, patIdxEnd);
        }
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            int i3 = patIdxStart + 1;
            while (true) {
                if (i3 > patIdxEnd) {
                    break;
                }
                if (patArr[i3] != '*') {
                    i3++;
                } else {
                    patIdxTmp = i3;
                    break;
                }
            }
            if (patIdxTmp == patIdxStart + 1) {
                patIdxStart++;
            } else {
                int patLength = (patIdxTmp - patIdxStart) - 1;
                int strLength = (strIdxEnd - strIdxStart) + 1;
                int foundIdx = -1;
                int i4 = 0;
                while (true) {
                    if (i4 > strLength - patLength) {
                        break;
                    }
                    for (0; j < patLength; j + 1) {
                        char ch5 = patArr[patIdxStart + j + 1];
                        j = (ch5 == '?' || !different(caseSensitive, ch5, strArr[(strIdxStart + i4) + j])) ? j + 1 : 0;
                    }
                    foundIdx = strIdxStart + i4;
                    break;
                    i4++;
                }
                if (foundIdx == -1) {
                    return false;
                }
                patIdxStart = patIdxTmp;
                strIdxStart = foundIdx + patLength;
            }
        }
        return allStars(patArr, patIdxStart, patIdxEnd);
    }

    private static boolean allStars(char[] chars, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (chars[i] != '*') {
                return false;
            }
        }
        return true;
    }

    private static boolean different(boolean caseSensitive, char ch2, char other) {
        return caseSensitive ? ch2 != other : Character.toUpperCase(ch2) != Character.toUpperCase(other);
    }
}
