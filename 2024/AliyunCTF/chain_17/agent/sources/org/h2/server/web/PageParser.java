package org.h2.server.web;

import ch.qos.logback.core.CoreConstants;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/web/PageParser.class */
public class PageParser {
    private static final int TAB_WIDTH = 4;
    private final String page;
    private int pos;
    private final Map<String, Object> settings;
    private final int len;
    private StringBuilder result;

    private PageParser(String str, Map<String, Object> map, int i) {
        this.page = str;
        this.pos = i;
        this.len = str.length();
        this.settings = map;
        this.result = new StringBuilder(this.len);
    }

    public static String parse(String str, Map<String, Object> map) {
        return new PageParser(str, map, 0).replaceTags();
    }

    private void setError(int i) {
        String escapeHtml = escapeHtml(this.page.substring(0, i) + "####BUG####" + this.page.substring(i));
        this.result = new StringBuilder();
        this.result.append(escapeHtml);
    }

    private String parseBlockUntil(String str) throws ParseException {
        PageParser pageParser = new PageParser(this.page, this.settings, this.pos);
        pageParser.parseAll();
        if (!pageParser.readIf(str)) {
            throw new ParseException(this.page, pageParser.pos);
        }
        this.pos = pageParser.pos;
        return pageParser.result.toString();
    }

    private String replaceTags() {
        try {
            parseAll();
            if (this.pos != this.len) {
                setError(this.pos);
            }
        } catch (ParseException e) {
            setError(this.pos);
        }
        return this.result.toString();
    }

    private void parseAll() throws ParseException {
        StringBuilder sb = this.result;
        String str = this.page;
        int i = this.pos;
        while (i < this.len) {
            char charAt = str.charAt(i);
            switch (charAt) {
                case '$':
                    if (str.length() > i + 1 && str.charAt(i + 1) == '{') {
                        int i2 = i + 2;
                        int indexOf = str.indexOf(125, i2);
                        if (indexOf < 0) {
                            setError(i2);
                            return;
                        }
                        String trimSubstring = StringUtils.trimSubstring(str, i2, indexOf);
                        i = indexOf;
                        replaceTags((String) get(trimSubstring));
                        break;
                    } else {
                        sb.append(charAt);
                        break;
                    }
                    break;
                case '<':
                    if (str.charAt(i + 3) == ':' && str.charAt(i + 1) == '/') {
                        this.pos = i;
                        return;
                    }
                    if (str.charAt(i + 2) == ':') {
                        this.pos = i;
                        if (readIf("<c:forEach")) {
                            String readParam = readParam("var");
                            String readParam2 = readParam("items");
                            read(">");
                            int i3 = this.pos;
                            List list = (List) get(readParam2);
                            if (list == null) {
                                this.result.append("?items?");
                                list = new ArrayList();
                            }
                            if (list.isEmpty()) {
                                parseBlockUntil("</c:forEach>");
                            }
                            Iterator it = list.iterator();
                            while (it.hasNext()) {
                                this.settings.put(readParam, it.next());
                                this.pos = i3;
                                this.result.append(parseBlockUntil("</c:forEach>"));
                            }
                        } else if (readIf("<c:if")) {
                            String readParam3 = readParam("test");
                            int indexOf2 = readParam3.indexOf("=='");
                            if (indexOf2 < 0) {
                                setError(i);
                                return;
                            }
                            String substring = readParam3.substring(indexOf2 + 3, readParam3.length() - 1);
                            String str2 = (String) get(readParam3.substring(0, indexOf2));
                            read(">");
                            String parseBlockUntil = parseBlockUntil("</c:if>");
                            this.pos--;
                            if (str2.equals(substring)) {
                                this.result.append(parseBlockUntil);
                            }
                        } else {
                            setError(i);
                            return;
                        }
                        i = this.pos;
                        break;
                    } else {
                        sb.append(charAt);
                        break;
                    }
                    break;
                default:
                    sb.append(charAt);
                    break;
            }
            i++;
        }
        this.pos = i;
    }

    private Object get(String str) {
        int indexOf = str.indexOf(46);
        if (indexOf >= 0) {
            String substring = str.substring(indexOf + 1);
            String substring2 = str.substring(0, indexOf);
            HashMap hashMap = (HashMap) this.settings.get(substring2);
            if (hashMap == null) {
                return CoreConstants.NA + substring2 + CoreConstants.NA;
            }
            return hashMap.get(substring);
        }
        return this.settings.get(str);
    }

    private void replaceTags(String str) {
        if (str != null) {
            this.result.append(parse(str, this.settings));
        }
    }

    private String readParam(String str) throws ParseException {
        read(str);
        read("=");
        read("\"");
        int i = this.pos;
        while (this.page.charAt(this.pos) != '\"') {
            this.pos++;
        }
        int i2 = this.pos;
        read("\"");
        return parse(this.page.substring(i, i2), this.settings);
    }

    private void skipSpaces() {
        while (this.page.charAt(this.pos) == ' ') {
            this.pos++;
        }
    }

    private void read(String str) throws ParseException {
        if (!readIf(str)) {
            throw new ParseException(str, this.pos);
        }
    }

    private boolean readIf(String str) {
        skipSpaces();
        if (this.page.regionMatches(this.pos, str, 0, str.length())) {
            this.pos += str.length();
            skipSpaces();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String escapeHtmlData(String str) {
        return escapeHtml(str, false);
    }

    public static String escapeHtml(String str) {
        return escapeHtml(str, true);
    }

    private static String escapeHtml(String str, boolean z) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (z && length == 0) {
            return "&nbsp;";
        }
        StringBuilder sb = new StringBuilder(length);
        boolean z2 = true;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < length) {
                int codePointAt = str.codePointAt(i2);
                if (codePointAt == 32 || codePointAt == 9) {
                    int i3 = 0;
                    while (true) {
                        if (i3 < (codePointAt == 32 ? 1 : 4)) {
                            if (z2 && z) {
                                sb.append("&nbsp;");
                            } else {
                                sb.append(' ');
                                z2 = true;
                            }
                            i3++;
                        }
                    }
                } else {
                    z2 = false;
                    switch (codePointAt) {
                        case 10:
                            if (z) {
                                sb.append("<br />");
                                z2 = true;
                                break;
                            } else {
                                sb.append(codePointAt);
                                break;
                            }
                        case 34:
                            sb.append("&quot;");
                            break;
                        case 36:
                            sb.append("&#36;");
                            break;
                        case 38:
                            sb.append("&amp;");
                            break;
                        case 39:
                            sb.append("&#39;");
                            break;
                        case 60:
                            sb.append("&lt;");
                            break;
                        case 62:
                            sb.append("&gt;");
                            break;
                        default:
                            if (codePointAt >= 128) {
                                sb.append("&#").append(codePointAt).append(';');
                                break;
                            } else {
                                sb.append((char) codePointAt);
                                break;
                            }
                    }
                }
                i = i2 + Character.charCount(codePointAt);
            } else {
                return sb.toString();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String escapeJavaScript(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            switch (charAt) {
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\'':
                    sb.append("\\'");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    sb.append(charAt);
                    break;
            }
        }
        return sb.toString();
    }
}
