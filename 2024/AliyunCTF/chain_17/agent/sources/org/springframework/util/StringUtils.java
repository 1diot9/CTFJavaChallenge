package org.springframework.util;

import cn.hutool.core.text.CharSequenceUtil;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/StringUtils.class */
public abstract class StringUtils {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final String FOLDER_SEPARATOR = "/";
    private static final char FOLDER_SEPARATOR_CHAR = '/';
    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
    private static final String TOP_PATH = "..";
    private static final String CURRENT_PATH = ".";
    private static final char EXTENSION_SEPARATOR = '.';
    private static final int DEFAULT_TRUNCATION_THRESHOLD = 100;
    private static final String TRUNCATION_SUFFIX = " (truncated)...";

    @Deprecated
    public static boolean isEmpty(@Nullable Object str) {
        return str == null || "".equals(str);
    }

    public static boolean hasLength(@Nullable CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static boolean hasLength(@Nullable String str) {
        return (str == null || str.isEmpty()) ? false : true;
    }

    public static boolean hasText(@Nullable CharSequence str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasText(@Nullable String str) {
        return (str == null || str.isBlank()) ? false : true;
    }

    public static boolean containsWhitespace(@Nullable CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsWhitespace(@Nullable String str) {
        return containsWhitespace((CharSequence) str);
    }

    @Deprecated(since = "6.0")
    public static String trimWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        return str.strip();
    }

    public static CharSequence trimAllWhitespace(CharSequence str) {
        if (!hasLength(str)) {
            return str;
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb;
    }

    public static String trimAllWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        return trimAllWhitespace((CharSequence) str).toString();
    }

    @Deprecated(since = "6.0")
    public static String trimLeadingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        return str.stripLeading();
    }

    @Deprecated(since = "6.0")
    public static String trimTrailingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        return str.stripTrailing();
    }

    public static String trimLeadingCharacter(String str, char leadingCharacter) {
        if (!hasLength(str)) {
            return str;
        }
        int beginIdx = 0;
        while (beginIdx < str.length() && leadingCharacter == str.charAt(beginIdx)) {
            beginIdx++;
        }
        return str.substring(beginIdx);
    }

    public static String trimTrailingCharacter(String str, char trailingCharacter) {
        if (!hasLength(str)) {
            return str;
        }
        int endIdx = str.length() - 1;
        while (endIdx >= 0 && trailingCharacter == str.charAt(endIdx)) {
            endIdx--;
        }
        return str.substring(0, endIdx + 1);
    }

    public static boolean matchesCharacter(@Nullable String str, char singleCharacter) {
        return str != null && str.length() == 1 && str.charAt(0) == singleCharacter;
    }

    public static boolean startsWithIgnoreCase(@Nullable String str, @Nullable String prefix) {
        return str != null && prefix != null && str.length() >= prefix.length() && str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static boolean endsWithIgnoreCase(@Nullable String str, @Nullable String suffix) {
        return str != null && suffix != null && str.length() >= suffix.length() && str.regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length());
    }

    public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        if (index + substring.length() > str.length()) {
            return false;
        }
        for (int i = 0; i < substring.length(); i++) {
            if (str.charAt(index + i) != substring.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static int countOccurrencesOf(String str, String sub) {
        if (!hasLength(str) || !hasLength(sub)) {
            return 0;
        }
        int count = 0;
        int i = 0;
        while (true) {
            int pos = i;
            int idx = str.indexOf(sub, pos);
            if (idx != -1) {
                count++;
                i = idx + sub.length();
            } else {
                return count;
            }
        }
    }

    public static String replace(String inString, String oldPattern, @Nullable String newPattern) {
        if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
            return inString;
        }
        int index = inString.indexOf(oldPattern);
        if (index == -1) {
            return inString;
        }
        int capacity = inString.length();
        if (newPattern.length() > oldPattern.length()) {
            capacity += 16;
        }
        StringBuilder sb = new StringBuilder(capacity);
        int pos = 0;
        int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append((CharSequence) inString, pos, index);
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sb.append((CharSequence) inString, pos, inString.length());
        return sb.toString();
    }

    public static String delete(String inString, String pattern) {
        return replace(inString, pattern, "");
    }

    public static String deleteAny(String inString, @Nullable String charsToDelete) {
        if (!hasLength(inString) || !hasLength(charsToDelete)) {
            return inString;
        }
        int lastCharIndex = 0;
        char[] result = new char[inString.length()];
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                int i2 = lastCharIndex;
                lastCharIndex++;
                result[i2] = c;
            }
        }
        if (lastCharIndex == inString.length()) {
            return inString;
        }
        return new String(result, 0, lastCharIndex);
    }

    @Nullable
    public static String quote(@Nullable String str) {
        if (str != null) {
            return "'" + str + "'";
        }
        return null;
    }

    @Nullable
    public static Object quoteIfString(@Nullable Object obj) {
        if (!(obj instanceof String)) {
            return obj;
        }
        String str = (String) obj;
        return quote(str);
    }

    public static String unqualify(String qualifiedName) {
        return unqualify(qualifiedName, '.');
    }

    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }

    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    public static String uncapitalizeAsProperty(String str) {
        if (!hasLength(str) || (str.length() > 1 && Character.isUpperCase(str.charAt(0)) && Character.isUpperCase(str.charAt(1)))) {
            return str;
        }
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        char updatedChar;
        if (!hasLength(str)) {
            return str;
        }
        char baseChar = str.charAt(0);
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        } else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars);
    }

    @Nullable
    public static String getFilename(@Nullable String path) {
        if (path == null) {
            return null;
        }
        int separatorIndex = path.lastIndexOf(47);
        return separatorIndex != -1 ? path.substring(separatorIndex + 1) : path;
    }

    @Nullable
    public static String getFilenameExtension(@Nullable String path) {
        int extIndex;
        if (path == null || (extIndex = path.lastIndexOf(46)) == -1) {
            return null;
        }
        int folderIndex = path.lastIndexOf(47);
        if (folderIndex > extIndex) {
            return null;
        }
        return path.substring(extIndex + 1);
    }

    public static String stripFilenameExtension(String path) {
        int extIndex = path.lastIndexOf(46);
        if (extIndex == -1) {
            return path;
        }
        int folderIndex = path.lastIndexOf(47);
        if (folderIndex > extIndex) {
            return path;
        }
        return path.substring(0, extIndex);
    }

    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf(47);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith("/")) {
                newPath = newPath + "/";
            }
            return newPath + relativePath;
        }
        return relativePath;
    }

    public static String cleanPath(String path) {
        if (!hasLength(path)) {
            return path;
        }
        String normalizedPath = replace(path, "\\", "/");
        String pathToUse = normalizedPath;
        if (pathToUse.indexOf(46) == -1) {
            return pathToUse;
        }
        int prefixIndex = pathToUse.indexOf(58);
        String prefix = "";
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (prefix.contains("/")) {
                prefix = "";
            } else {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            }
        }
        if (pathToUse.startsWith("/")) {
            prefix = prefix + "/";
            pathToUse = pathToUse.substring(1);
        }
        String[] pathArray = delimitedListToStringArray(pathToUse, "/");
        Deque<String> pathElements = new ArrayDeque<>(pathArray.length);
        int tops = 0;
        for (int i = pathArray.length - 1; i >= 0; i--) {
            String element = pathArray[i];
            if (!".".equals(element)) {
                if ("..".equals(element)) {
                    tops++;
                } else if (tops > 0) {
                    tops--;
                } else {
                    pathElements.addFirst(element);
                }
            }
        }
        if (pathArray.length == pathElements.size()) {
            return normalizedPath;
        }
        for (int i2 = 0; i2 < tops; i2++) {
            pathElements.addFirst("..");
        }
        if (pathElements.size() == 1 && pathElements.getLast().isEmpty() && !prefix.endsWith("/")) {
            pathElements.addFirst(".");
        }
        String joined = collectionToDelimitedString(pathElements, "/");
        return prefix.isEmpty() ? joined : prefix + joined;
    }

    public static boolean pathEquals(String path1, String path2) {
        return cleanPath(path1).equals(cleanPath(path2));
    }

    public static String uriDecode(String source, Charset charset) {
        int length = source.length();
        if (length == 0) {
            return source;
        }
        Assert.notNull(charset, "Charset must not be null");
        ByteArrayOutputStream baos = new ByteArrayOutputStream(length);
        boolean changed = false;
        int i = 0;
        while (i < length) {
            int ch2 = source.charAt(i);
            if (ch2 == 37) {
                if (i + 2 < length) {
                    char hex1 = source.charAt(i + 1);
                    char hex2 = source.charAt(i + 2);
                    int u = Character.digit(hex1, 16);
                    int l = Character.digit(hex2, 16);
                    if (u == -1 || l == -1) {
                        throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                    }
                    baos.write((char) ((u << 4) + l));
                    i += 2;
                    changed = true;
                } else {
                    throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                }
            } else {
                baos.write(ch2);
            }
            i++;
        }
        return changed ? StreamUtils.copyToString(baos, charset) : source;
    }

    @Nullable
    public static Locale parseLocale(String localeValue) {
        if (!localeValue.contains("_") && !localeValue.contains(CharSequenceUtil.SPACE)) {
            validateLocalePart(localeValue);
            Locale resolved = Locale.forLanguageTag(localeValue);
            if (resolved.getLanguage().length() > 0) {
                return resolved;
            }
        }
        return parseLocaleString(localeValue);
    }

    @Nullable
    public static Locale parseLocaleString(String localeString) {
        if (localeString.equals("")) {
            return null;
        }
        String delimiter = "_";
        if (!localeString.contains("_") && localeString.contains(CharSequenceUtil.SPACE)) {
            delimiter = CharSequenceUtil.SPACE;
        }
        String[] tokens = localeString.split(delimiter, -1);
        if (tokens.length == 1) {
            String language = tokens[0];
            validateLocalePart(language);
            return new Locale(language);
        }
        if (tokens.length == 2) {
            String language2 = tokens[0];
            validateLocalePart(language2);
            String country = tokens[1];
            validateLocalePart(country);
            return new Locale(language2, country);
        }
        if (tokens.length > 2) {
            String language3 = tokens[0];
            validateLocalePart(language3);
            String country2 = tokens[1];
            validateLocalePart(country2);
            String variant = (String) Arrays.stream(tokens).skip(2L).collect(Collectors.joining(delimiter));
            return new Locale(language3, country2, variant);
        }
        throw new IllegalArgumentException("Invalid locale format: '" + localeString + "'");
    }

    private static void validateLocalePart(String localePart) {
        for (int i = 0; i < localePart.length(); i++) {
            char ch2 = localePart.charAt(i);
            if (ch2 != ' ' && ch2 != '_' && ch2 != '-' && ch2 != '#' && !Character.isLetterOrDigit(ch2)) {
                throw new IllegalArgumentException("Locale part \"" + localePart + "\" contains invalid characters");
            }
        }
    }

    public static TimeZone parseTimeZoneString(String timeZoneString) {
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
        if ("GMT".equals(timeZone.getID()) && !timeZoneString.startsWith("GMT")) {
            throw new IllegalArgumentException("Invalid time zone specification '" + timeZoneString + "'");
        }
        return timeZone;
    }

    public static String[] toStringArray(@Nullable Collection<String> collection) {
        return !CollectionUtils.isEmpty(collection) ? (String[]) collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY;
    }

    public static String[] toStringArray(@Nullable Enumeration<String> enumeration) {
        return enumeration != null ? toStringArray(Collections.list(enumeration)) : EMPTY_STRING_ARRAY;
    }

    public static String[] addStringToArray(@Nullable String[] array, String str) {
        if (ObjectUtils.isEmpty((Object[]) array)) {
            return new String[]{str};
        }
        String[] newArr = new String[array.length + 1];
        System.arraycopy(array, 0, newArr, 0, array.length);
        newArr[array.length] = str;
        return newArr;
    }

    @Nullable
    public static String[] concatenateStringArrays(@Nullable String[] array1, @Nullable String[] array2) {
        if (ObjectUtils.isEmpty((Object[]) array1)) {
            return array2;
        }
        if (ObjectUtils.isEmpty((Object[]) array2)) {
            return array1;
        }
        String[] newArr = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, newArr, 0, array1.length);
        System.arraycopy(array2, 0, newArr, array1.length, array2.length);
        return newArr;
    }

    public static String[] sortStringArray(String[] array) {
        if (ObjectUtils.isEmpty((Object[]) array)) {
            return array;
        }
        Arrays.sort(array);
        return array;
    }

    public static String[] trimArrayElements(String[] array) {
        if (ObjectUtils.isEmpty((Object[]) array)) {
            return array;
        }
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            String element = array[i];
            result[i] = element != null ? element.trim() : null;
        }
        return result;
    }

    public static String[] removeDuplicateStrings(String[] array) {
        if (ObjectUtils.isEmpty((Object[]) array)) {
            return array;
        }
        Set<String> set = new LinkedHashSet<>(Arrays.asList(array));
        return toStringArray(set);
    }

    @Nullable
    public static String[] split(@Nullable String toSplit, @Nullable String delimiter) {
        int offset;
        if (!hasLength(toSplit) || !hasLength(delimiter) || (offset = toSplit.indexOf(delimiter)) < 0) {
            return null;
        }
        String beforeDelimiter = toSplit.substring(0, offset);
        String afterDelimiter = toSplit.substring(offset + delimiter.length());
        return new String[]{beforeDelimiter, afterDelimiter};
    }

    @Nullable
    public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
        return splitArrayElementsIntoProperties(array, delimiter, null);
    }

    @Nullable
    public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter, @Nullable String charsToDelete) {
        if (ObjectUtils.isEmpty((Object[]) array)) {
            return null;
        }
        Properties result = new Properties();
        for (String element : array) {
            if (charsToDelete != null) {
                element = deleteAny(element, charsToDelete);
            }
            String[] splittedElement = split(element, delimiter);
            if (splittedElement != null) {
                result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
            }
        }
        return result;
    }

    public static String[] tokenizeToStringArray(@Nullable String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(@Nullable String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }

    public static String[] delimitedListToStringArray(@Nullable String str, @Nullable String delimiter, @Nullable String charsToDelete) {
        int pos;
        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }
        if (delimiter == null) {
            return new String[]{str};
        }
        List<String> result = new ArrayList<>();
        if (delimiter.isEmpty()) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int i2 = 0;
            while (true) {
                pos = i2;
                int delPos = str.indexOf(delimiter, pos);
                if (delPos == -1) {
                    break;
                }
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                i2 = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }

    public static String[] commaDelimitedListToStringArray(@Nullable String str) {
        return delimitedListToStringArray(str, ",");
    }

    public static Set<String> commaDelimitedListToSet(@Nullable String str) {
        String[] tokens = commaDelimitedListToStringArray(str);
        return new LinkedHashSet(Arrays.asList(tokens));
    }

    public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim, String prefix, String suffix) {
        if (CollectionUtils.isEmpty(coll)) {
            return "";
        }
        int totalLength = (coll.size() * (prefix.length() + suffix.length())) + ((coll.size() - 1) * delim.length());
        for (Object element : coll) {
            totalLength += String.valueOf(element).length();
        }
        StringBuilder sb = new StringBuilder(totalLength);
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    public static String collectionToDelimitedString(@Nullable Collection<?> coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    public static String collectionToCommaDelimitedString(@Nullable Collection<?> coll) {
        return collectionToDelimitedString(coll, ",");
    }

    public static String arrayToDelimitedString(@Nullable Object[] arr, String delim) {
        if (ObjectUtils.isEmpty(arr)) {
            return "";
        }
        if (arr.length == 1) {
            return ObjectUtils.nullSafeToString(arr[0]);
        }
        StringJoiner sj = new StringJoiner(delim);
        for (Object elem : arr) {
            sj.add(String.valueOf(elem));
        }
        return sj.toString();
    }

    public static String arrayToCommaDelimitedString(@Nullable Object[] arr) {
        return arrayToDelimitedString(arr, ",");
    }

    public static String truncate(CharSequence charSequence) {
        return truncate(charSequence, 100);
    }

    public static String truncate(CharSequence charSequence, int threshold) {
        Assert.isTrue(threshold > 0, (Supplier<String>) () -> {
            return "Truncation threshold must be a positive number: " + threshold;
        });
        if (charSequence.length() > threshold) {
            return charSequence.subSequence(0, threshold) + " (truncated)...";
        }
        return charSequence.toString();
    }
}
