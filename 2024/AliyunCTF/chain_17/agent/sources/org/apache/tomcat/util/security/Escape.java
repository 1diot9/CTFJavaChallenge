package org.apache.tomcat.util.security;

import ch.qos.logback.core.CoreConstants;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/security/Escape.class */
public class Escape {
    private Escape() {
    }

    public static String htmlElementContent(String content) {
        if (content == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else if (c == '\'') {
                sb.append("&#39;");
            } else if (c == '&') {
                sb.append("&amp;");
            } else if (c == '\"') {
                sb.append("&quot;");
            } else if (c == '/') {
                sb.append("&#47;");
            } else {
                sb.append(c);
            }
        }
        return sb.length() > content.length() ? sb.toString() : content;
    }

    public static String htmlElementContent(Object obj) {
        if (obj == null) {
            return CoreConstants.NA;
        }
        try {
            return htmlElementContent(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static String xml(String content) {
        return xml(null, content);
    }

    public static String xml(String ifNull, String content) {
        return xml(ifNull, false, content);
    }

    public static String xml(String ifNull, boolean escapeCRLF, String content) {
        if (content == null) {
            return ifNull;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else if (c == '\'') {
                sb.append("&apos;");
            } else if (c == '&') {
                sb.append("&amp;");
            } else if (c == '\"') {
                sb.append("&quot;");
            } else if (escapeCRLF && c == '\r') {
                sb.append("&#13;");
            } else if (escapeCRLF && c == '\n') {
                sb.append("&#10;");
            } else {
                sb.append(c);
            }
        }
        return sb.length() > content.length() ? sb.toString() : content;
    }
}
