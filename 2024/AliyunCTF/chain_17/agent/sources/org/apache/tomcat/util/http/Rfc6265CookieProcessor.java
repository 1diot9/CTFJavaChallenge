package org.apache.tomcat.util.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.parser.Cookie;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/Rfc6265CookieProcessor.class */
public class Rfc6265CookieProcessor extends CookieProcessorBase {
    private static final Log log = LogFactory.getLog((Class<?>) Rfc6265CookieProcessor.class);
    private static final StringManager sm = StringManager.getManager(Rfc6265CookieProcessor.class.getPackage().getName());
    private static final BitSet domainValid = new BitSet(128);

    static {
        char c = '0';
        while (true) {
            char c2 = c;
            if (c2 > '9') {
                break;
            }
            domainValid.set(c2);
            c = (char) (c2 + 1);
        }
        char c3 = 'a';
        while (true) {
            char c4 = c3;
            if (c4 > 'z') {
                break;
            }
            domainValid.set(c4);
            c3 = (char) (c4 + 1);
        }
        char c5 = 'A';
        while (true) {
            char c6 = c5;
            if (c6 <= 'Z') {
                domainValid.set(c6);
                c5 = (char) (c6 + 1);
            } else {
                domainValid.set(46);
                domainValid.set(45);
                return;
            }
        }
    }

    @Override // org.apache.tomcat.util.http.CookieProcessor
    public Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override // org.apache.tomcat.util.http.CookieProcessor
    public void parseCookieHeader(MimeHeaders headers, ServerCookies serverCookies) {
        if (headers == null) {
            return;
        }
        int findHeader = headers.findHeader(HttpHeaders.COOKIE, 0);
        while (true) {
            int pos = findHeader;
            if (pos >= 0) {
                MessageBytes cookieValue = headers.getValue(pos);
                if (cookieValue != null && !cookieValue.isNull()) {
                    if (cookieValue.getType() != 2) {
                        if (log.isDebugEnabled()) {
                            Exception e = new Exception();
                            log.debug("Cookies: Parsing cookie as String. Expected bytes.", e);
                        }
                        cookieValue.toBytes();
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Cookies: Parsing b[]: " + cookieValue.toString());
                    }
                    ByteChunk bc = cookieValue.getByteChunk();
                    Cookie.parseCookie(bc.getBytes(), bc.getOffset(), bc.getLength(), serverCookies);
                }
                findHeader = headers.findHeader(HttpHeaders.COOKIE, pos + 1);
            } else {
                return;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:69:0x02a3 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0178 A[SYNTHETIC] */
    @Override // org.apache.tomcat.util.http.CookieProcessor
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String generateHeader(jakarta.servlet.http.Cookie r11, jakarta.servlet.http.HttpServletRequest r12) {
        /*
            Method dump skipped, instructions count: 752
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.Rfc6265CookieProcessor.generateHeader(jakarta.servlet.http.Cookie, jakarta.servlet.http.HttpServletRequest):java.lang.String");
    }

    private void validateCookieValue(String value) {
        int start = 0;
        int end = value.length();
        if (end > 1 && value.charAt(0) == '\"' && value.charAt(end - 1) == '\"') {
            start = 1;
            end--;
        }
        char[] chars = value.toCharArray();
        for (int i = start; i < end; i++) {
            char c = chars[i];
            if (c < '!' || c == '\"' || c == ',' || c == ';' || c == '\\' || c == 127) {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidCharInValue", Integer.toString(c)));
            }
        }
    }

    private void validateDomain(String domain) {
        int cur = -1;
        char[] chars = domain.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int prev = cur;
            cur = chars[i];
            if (!domainValid.get(cur)) {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", domain));
            }
            if ((prev == 46 || prev == -1) && (cur == 46 || cur == 45)) {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", domain));
            }
            if (prev == 45 && cur == 46) {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", domain));
            }
        }
        if (cur == 46 || cur == 45) {
            throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidDomain", domain));
        }
    }

    private void validatePath(String path) {
        char[] chars = path.toCharArray();
        for (char ch2 : chars) {
            if (ch2 < ' ' || ch2 > '~' || ch2 == ';') {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidPath", path));
            }
        }
    }

    private void validateAttribute(String name, String value) {
        if (!HttpParser.isToken(name)) {
            throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidAttributeName", name));
        }
        char[] chars = value.toCharArray();
        for (char ch2 : chars) {
            if (ch2 < ' ' || ch2 > '~' || ch2 == ';') {
                throw new IllegalArgumentException(sm.getString("rfc6265CookieProcessor.invalidAttributeValue", name, value));
            }
        }
    }
}
