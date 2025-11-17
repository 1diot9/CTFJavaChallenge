package org.apache.catalina.valves.rewrite;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.buf.UDecoder;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/InternalRewriteMap.class */
public class InternalRewriteMap {
    public static RewriteMap toMap(String name) {
        if ("toupper".equals(name)) {
            return new UpperCase();
        }
        if ("tolower".equals(name)) {
            return new LowerCase();
        }
        if ("escape".equals(name)) {
            return new Escape();
        }
        if ("unescape".equals(name)) {
            return new Unescape();
        }
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/InternalRewriteMap$LowerCase.class */
    public static class LowerCase implements RewriteMap {
        private Locale locale = Locale.getDefault();

        @Override // org.apache.catalina.valves.rewrite.RewriteMap
        public String setParameters(String params) {
            this.locale = Locale.forLanguageTag(params);
            return null;
        }

        @Override // org.apache.catalina.valves.rewrite.RewriteMap
        public String lookup(String key) {
            if (key != null) {
                return key.toLowerCase(this.locale);
            }
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/InternalRewriteMap$UpperCase.class */
    public static class UpperCase implements RewriteMap {
        private Locale locale = Locale.getDefault();

        @Override // org.apache.catalina.valves.rewrite.RewriteMap
        public String setParameters(String params) {
            this.locale = Locale.forLanguageTag(params);
            return null;
        }

        @Override // org.apache.catalina.valves.rewrite.RewriteMap
        public String lookup(String key) {
            if (key != null) {
                return key.toUpperCase(this.locale);
            }
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/InternalRewriteMap$Escape.class */
    public static class Escape implements RewriteMap {
        private Charset charset = StandardCharsets.UTF_8;

        @Override // org.apache.catalina.valves.rewrite.RewriteMap
        public String setParameters(String params) {
            this.charset = Charset.forName(params);
            return null;
        }

        @Override // org.apache.catalina.valves.rewrite.RewriteMap
        public String lookup(String key) {
            if (key != null) {
                return URLEncoder.DEFAULT.encode(key, this.charset);
            }
            return null;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/InternalRewriteMap$Unescape.class */
    public static class Unescape implements RewriteMap {
        private Charset charset = StandardCharsets.UTF_8;

        @Override // org.apache.catalina.valves.rewrite.RewriteMap
        public String setParameters(String params) {
            this.charset = Charset.forName(params);
            return null;
        }

        @Override // org.apache.catalina.valves.rewrite.RewriteMap
        public String lookup(String key) {
            if (key != null) {
                return UDecoder.URLDecode(key, this.charset);
            }
            return null;
        }
    }
}
