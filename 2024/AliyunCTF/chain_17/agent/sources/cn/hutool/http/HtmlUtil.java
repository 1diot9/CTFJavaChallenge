package cn.hutool.http;

import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/HtmlUtil.class */
public class HtmlUtil {
    public static final String NBSP = "&nbsp;";
    public static final String AMP = "&amp;";
    public static final String QUOTE = "&quot;";
    public static final String APOS = "&apos;";
    public static final String LT = "&lt;";
    public static final String GT = "&gt;";
    public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";
    public static final String RE_SCRIPT = "<[\\s]*?script[^>]*?>.*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
    private static final char[][] TEXT = new char[256];

    /* JADX WARN: Type inference failed for: r0v1, types: [char[], char[][]] */
    static {
        for (int i = 0; i < 256; i++) {
            char[] cArr = new char[1];
            cArr[0] = (char) i;
            TEXT[i] = cArr;
        }
        TEXT[39] = "&#039;".toCharArray();
        TEXT[34] = "&quot;".toCharArray();
        TEXT[38] = "&amp;".toCharArray();
        TEXT[60] = "&lt;".toCharArray();
        TEXT[62] = "&gt;".toCharArray();
        TEXT[160] = "&nbsp;".toCharArray();
    }

    public static String escape(String text) {
        return encode(text);
    }

    public static String unescape(String htmlStr) {
        if (StrUtil.isBlank(htmlStr)) {
            return htmlStr;
        }
        return EscapeUtil.unescapeHtml4(htmlStr);
    }

    public static String cleanHtmlTag(String content) {
        return content.replaceAll(RE_HTML_MARK, "");
    }

    public static String removeHtmlTag(String content, String... tagNames) {
        return removeHtmlTag(content, true, tagNames);
    }

    public static String unwrapHtmlTag(String content, String... tagNames) {
        return removeHtmlTag(content, false, tagNames);
    }

    public static String removeHtmlTag(String content, boolean withTagContent, String... tagNames) {
        String regex;
        for (String tagName : tagNames) {
            if (!StrUtil.isBlank(tagName)) {
                String tagName2 = tagName.trim();
                if (withTagContent) {
                    regex = StrUtil.format("(?i)<{}(\\s+[^>]*?)?/?>(.*?</{}>)?", tagName2, tagName2);
                } else {
                    regex = StrUtil.format("(?i)<{}(\\s+[^>]*?)?/?>|</?{}>", tagName2, tagName2);
                }
                content = ReUtil.delAll(regex, content);
            }
        }
        return content;
    }

    public static String removeHtmlAttr(String content, String... attrs) {
        for (String attr : attrs) {
            String regex = StrUtil.format("(?i)(\\s*{}\\s*=[^>]+?\\s+(?=>))|(\\s*{}\\s*=[^>]+?(?=\\s|>))", attr, attr);
            content = content.replaceAll(regex, "");
        }
        return content;
    }

    public static String removeAllHtmlAttr(String content, String... tagNames) {
        for (String tagName : tagNames) {
            String regex = StrUtil.format("(?i)<{}[^>]*?>", tagName);
            content = content.replaceAll(regex, StrUtil.format("<{}>", tagName));
        }
        return content;
    }

    private static String encode(String text) {
        int len;
        if (text == null || (len = text.length()) == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(len + (len >> 2));
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (c < 256) {
                buffer.append(TEXT[c]);
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    public static String filter(String htmlContent) {
        return new HTMLFilter().filter(htmlContent);
    }
}
