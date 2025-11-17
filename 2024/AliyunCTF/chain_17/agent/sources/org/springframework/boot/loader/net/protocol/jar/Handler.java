package org.springframework.boot.loader.net.protocol.jar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/jar/Handler.class */
public class Handler extends URLStreamHandler {
    private static final String PROTOCOL = "jar";
    private static final String SEPARATOR = "!/";
    static final Handler INSTANCE = new Handler();

    @Override // java.net.URLStreamHandler
    protected URLConnection openConnection(URL url) throws IOException {
        return JarUrlConnection.open(url);
    }

    @Override // java.net.URLStreamHandler
    protected void parseURL(URL url, String spec, int start, int limit) {
        if (spec.regionMatches(true, start, "jar:", 0, 4)) {
            throw new IllegalStateException("Nested JAR URLs are not supported");
        }
        int anchorIndex = spec.indexOf(35, limit);
        String path = extractPath(url, spec, start, limit, anchorIndex);
        String ref = anchorIndex != -1 ? spec.substring(anchorIndex + 1) : null;
        setURL(url, "jar", "", -1, null, null, path, null, ref);
    }

    private String extractPath(URL url, String spec, int start, int limit, int anchorIndex) {
        if (anchorIndex == start) {
            return extractAnchorOnlyPath(url);
        }
        if (spec.length() >= 4 && spec.regionMatches(true, 0, "jar:", 0, 4)) {
            return extractAbsolutePath(spec, start, limit);
        }
        return extractRelativePath(url, spec, start, limit);
    }

    private String extractAnchorOnlyPath(URL url) {
        return url.getPath();
    }

    private String extractAbsolutePath(String spec, int start, int limit) {
        int indexOfSeparator = indexOfSeparator(spec, start, limit);
        if (indexOfSeparator == -1) {
            throw new IllegalStateException("no !/ in spec");
        }
        String innerUrl = spec.substring(start, indexOfSeparator);
        assertInnerUrlIsNotMalformed(spec, innerUrl);
        return spec.substring(start, limit);
    }

    private String extractRelativePath(URL url, String spec, int start, int limit) {
        String contextPath = extractContextPath(url, spec, start);
        String path = contextPath + spec.substring(start, limit);
        return Canonicalizer.canonicalizeAfter(path, indexOfSeparator(path) + 1);
    }

    private String extractContextPath(URL url, String spec, int start) {
        String contextPath = url.getPath();
        if (spec.regionMatches(false, start, "/", 0, 1)) {
            int indexOfContextPathSeparator = indexOfSeparator(contextPath);
            if (indexOfContextPathSeparator == -1) {
                throw new IllegalStateException("malformed context url:%s: no !/".formatted(url));
            }
            return contextPath.substring(0, indexOfContextPathSeparator + 1);
        }
        int lastSlash = contextPath.lastIndexOf(47);
        if (lastSlash == -1) {
            throw new IllegalStateException("malformed context url:%s".formatted(url));
        }
        return contextPath.substring(0, lastSlash + 1);
    }

    private void assertInnerUrlIsNotMalformed(String spec, String innerUrl) {
        if (innerUrl.startsWith("nested:")) {
            org.springframework.boot.loader.net.protocol.nested.Handler.assertUrlIsNotMalformed(innerUrl);
            return;
        }
        try {
            new URL(innerUrl);
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("invalid url: %s (%s)".formatted(spec, ex));
        }
    }

    @Override // java.net.URLStreamHandler
    protected int hashCode(URL url) {
        String protocol = url.getProtocol();
        int hash = protocol != null ? protocol.hashCode() : 0;
        String file = url.getFile();
        int indexOfSeparator = file.indexOf("!/");
        if (indexOfSeparator == -1) {
            return hash + file.hashCode();
        }
        String fileWithoutEntry = file.substring(0, indexOfSeparator);
        try {
            hash += new URL(fileWithoutEntry).hashCode();
        } catch (MalformedURLException e) {
            hash += fileWithoutEntry.hashCode();
        }
        String entry = file.substring(indexOfSeparator + 2);
        return hash + entry.hashCode();
    }

    @Override // java.net.URLStreamHandler
    protected boolean sameFile(URL url1, URL url2) {
        if (!url1.getProtocol().equals("jar") || !url2.getProtocol().equals("jar")) {
            return false;
        }
        String file1 = url1.getFile();
        String file2 = url2.getFile();
        int indexOfSeparator1 = file1.indexOf("!/");
        int indexOfSeparator2 = file2.indexOf("!/");
        if (indexOfSeparator1 == -1 || indexOfSeparator2 == -1) {
            return super.sameFile(url1, url2);
        }
        String entry1 = file1.substring(indexOfSeparator1 + 2);
        String entry2 = file2.substring(indexOfSeparator2 + 2);
        if (!entry1.equals(entry2)) {
            return false;
        }
        try {
            URL innerUrl1 = new URL(file1.substring(0, indexOfSeparator1));
            URL innerUrl2 = new URL(file2.substring(0, indexOfSeparator2));
            if (!super.sameFile(innerUrl1, innerUrl2)) {
                return false;
            }
            return true;
        } catch (MalformedURLException e) {
            return super.sameFile(url1, url2);
        }
    }

    static int indexOfSeparator(String spec) {
        return indexOfSeparator(spec, 0, spec.length());
    }

    static int indexOfSeparator(String spec, int start, int limit) {
        for (int i = limit - 1; i >= start; i--) {
            if (spec.charAt(i) == '!' && i + 1 < limit && spec.charAt(i + 1) == '/') {
                return i;
            }
        }
        return -1;
    }

    public static void clearCache() {
        JarFileUrlKey.clearCache();
        JarUrlConnection.clearCache();
    }
}
