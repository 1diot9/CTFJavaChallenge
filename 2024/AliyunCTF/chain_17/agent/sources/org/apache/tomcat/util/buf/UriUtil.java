package org.apache.tomcat.util.buf;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/UriUtil.class */
public final class UriUtil {
    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final Pattern PATTERN_EXCLAMATION_MARK = Pattern.compile("!/");
    private static final Pattern PATTERN_CARET = Pattern.compile("\\^/");
    private static final Pattern PATTERN_ASTERISK = Pattern.compile("\\*/");
    private static final Pattern PATTERN_CUSTOM;
    private static final String REPLACE_CUSTOM;
    private static final String WAR_SEPARATOR;

    static {
        String custom = System.getProperty("org.apache.tomcat.util.buf.UriUtil.WAR_SEPARATOR");
        if (custom == null) {
            WAR_SEPARATOR = "*/";
            PATTERN_CUSTOM = null;
            REPLACE_CUSTOM = null;
            return;
        }
        WAR_SEPARATOR = custom + "/";
        PATTERN_CUSTOM = Pattern.compile(Pattern.quote(WAR_SEPARATOR));
        StringBuilder sb = new StringBuilder(custom.length() * 3);
        byte[] ba = custom.getBytes();
        for (byte toEncode : ba) {
            sb.append('%');
            int low = toEncode & 15;
            int high = (toEncode & 240) >> 4;
            sb.append(HEX[high]);
            sb.append(HEX[low]);
        }
        REPLACE_CUSTOM = sb.toString();
    }

    private UriUtil() {
    }

    private static boolean isSchemeChar(char c) {
        return Character.isLetterOrDigit(c) || c == '+' || c == '-' || c == '.';
    }

    public static boolean hasScheme(CharSequence uri) {
        int len = uri.length();
        int i = 0;
        while (i < len) {
            char c = uri.charAt(i);
            if (c == ':') {
                return i > 0;
            }
            if (isSchemeChar(c)) {
                i++;
            } else {
                return false;
            }
        }
        return false;
    }

    public static URL buildJarUrl(File jarFile) throws MalformedURLException {
        return buildJarUrl(jarFile, (String) null);
    }

    public static URL buildJarUrl(File jarFile, String entryPath) throws MalformedURLException {
        return buildJarUrl(jarFile.toURI().toString(), entryPath);
    }

    public static URL buildJarUrl(String fileUrlString) throws MalformedURLException {
        return buildJarUrl(fileUrlString, (String) null);
    }

    public static URL buildJarUrl(String fileUrlString, String entryPath) throws MalformedURLException {
        String safeString = makeSafeForJarUrl(fileUrlString);
        StringBuilder sb = new StringBuilder();
        sb.append(safeString);
        sb.append("!/");
        if (entryPath != null) {
            sb.append(makeSafeForJarUrl(entryPath));
        }
        return new URL("jar", null, -1, sb.toString());
    }

    public static URL buildJarSafeUrl(File file) throws MalformedURLException {
        String safe = makeSafeForJarUrl(file.toURI().toString());
        return new URL(safe);
    }

    private static String makeSafeForJarUrl(String input) {
        String tmp = PATTERN_EXCLAMATION_MARK.matcher(input).replaceAll("%21/");
        String tmp2 = PATTERN_ASTERISK.matcher(PATTERN_CARET.matcher(tmp).replaceAll("%5e/")).replaceAll("%2a/");
        if (PATTERN_CUSTOM != null) {
            tmp2 = PATTERN_CUSTOM.matcher(tmp2).replaceAll(REPLACE_CUSTOM);
        }
        return tmp2;
    }

    public static URL warToJar(URL warUrl) throws MalformedURLException {
        String file = warUrl.getFile();
        if (file.contains("*/")) {
            file = file.replaceFirst("\\*/", "!/");
        } else if (file.contains("^/")) {
            file = file.replaceFirst("\\^/", "!/");
        } else if (PATTERN_CUSTOM != null) {
            file = file.replaceFirst(PATTERN_CUSTOM.pattern(), "!/");
        }
        return new URL("jar", warUrl.getHost(), warUrl.getPort(), file);
    }

    public static String getWarSeparator() {
        return WAR_SEPARATOR;
    }

    public static boolean isAbsoluteURI(String path) {
        if (path.startsWith("file:/")) {
            return true;
        }
        int i = 0;
        while (i < path.length() && isSchemeChar(path.charAt(i))) {
            i++;
        }
        if (i == 0 || i + 2 >= path.length()) {
            return false;
        }
        int i2 = i;
        int i3 = i + 1;
        if (path.charAt(i2) != ':') {
            return false;
        }
        int i4 = i3 + 1;
        if (path.charAt(i3) == '/' && path.charAt(i4) == '/') {
            return true;
        }
        return false;
    }

    public static URI resolve(URI base, String target) throws MalformedURLException, URISyntaxException {
        if (base.getScheme().equals("jar")) {
            URI fileUri = new URI(base.getSchemeSpecificPart());
            URI fileUriResolved = fileUri.resolve(target);
            return new URI("jar:" + fileUriResolved.toString());
        }
        return base.resolve(target);
    }
}
