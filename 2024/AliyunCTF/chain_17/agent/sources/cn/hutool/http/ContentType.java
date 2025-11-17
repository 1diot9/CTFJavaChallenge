package cn.hutool.http;

import cn.hutool.core.util.StrUtil;
import java.nio.charset.Charset;
import org.springframework.http.MediaType;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/ContentType.class */
public enum ContentType {
    FORM_URLENCODED(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
    MULTIPART("multipart/form-data"),
    JSON("application/json"),
    XML("application/xml"),
    TEXT_PLAIN("text/plain"),
    TEXT_XML("text/xml"),
    TEXT_HTML("text/html"),
    OCTET_STREAM("application/octet-stream");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override // java.lang.Enum
    public String toString() {
        return getValue();
    }

    public String toString(Charset charset) {
        return build(this.value, charset);
    }

    public static boolean isDefault(String contentType) {
        return null == contentType || isFormUrlEncode(contentType);
    }

    public static boolean isFormUrlEncode(String contentType) {
        return StrUtil.startWithIgnoreCase(contentType, FORM_URLENCODED.toString());
    }

    public static ContentType get(String body) {
        ContentType contentType = null;
        if (StrUtil.isNotBlank(body)) {
            char firstChar = body.charAt(0);
            switch (firstChar) {
                case '<':
                    contentType = XML;
                    break;
                case '[':
                case '{':
                    contentType = JSON;
                    break;
            }
        }
        return contentType;
    }

    public static String build(String contentType, Charset charset) {
        return StrUtil.format("{};charset={}", contentType, charset.name());
    }

    public static String build(ContentType contentType, Charset charset) {
        return build(contentType.getValue(), charset);
    }
}
