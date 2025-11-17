package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.buf.UriUtil;
import org.springframework.util.ResourceUtils;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/scan/JarFactory.class */
public class JarFactory {
    private JarFactory() {
    }

    public static Jar newInstance(URL url) throws IOException {
        String urlString = url.toString();
        if (urlString.startsWith("jar:file:")) {
            if (urlString.endsWith(ResourceUtils.JAR_URL_SEPARATOR)) {
                return new JarFileUrlJar(url, true);
            }
            return new JarFileUrlNestedJar(url);
        }
        if (urlString.startsWith("war:file:")) {
            URL jarUrl = UriUtil.warToJar(url);
            return new JarFileUrlNestedJar(jarUrl);
        }
        if (urlString.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
            return new JarFileUrlJar(url, false);
        }
        return new UrlJar(url);
    }

    public static URL getJarEntryURL(URL baseUrl, String entryName) throws MalformedURLException {
        String baseExternal = baseUrl.toExternalForm();
        if (baseExternal.startsWith(ResourceUtils.URL_PROTOCOL_JAR)) {
            baseExternal = baseExternal.replaceFirst("^jar:", ResourceUtils.WAR_URL_PREFIX).replaceFirst(ResourceUtils.JAR_URL_SEPARATOR, Matcher.quoteReplacement(UriUtil.getWarSeparator()));
        }
        return new URL("jar:" + baseExternal + "!/" + entryName);
    }
}
