package cn.hutool.http;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/HttpBase.class */
public abstract class HttpBase<T> {
    protected static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
    public static final String HTTP_1_0 = "HTTP/1.0";
    public static final String HTTP_1_1 = "HTTP/1.1";
    protected Map<String, List<String>> headers = new HashMap();
    protected Charset charset = DEFAULT_CHARSET;
    protected String httpVersion = "HTTP/1.1";
    protected Resource body;

    public String header(String name) {
        List<String> values = headerList(name);
        if (CollectionUtil.isEmpty((Collection<?>) values)) {
            return null;
        }
        return values.get(0);
    }

    public List<String> headerList(String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        CaseInsensitiveMap<String, List<String>> headersIgnoreCase = new CaseInsensitiveMap<>(this.headers);
        return headersIgnoreCase.get(name.trim());
    }

    public String header(Header name) {
        if (null == name) {
            return null;
        }
        return header(name.toString());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T header(String name, String value, boolean isOverride) {
        if (null != name && null != value) {
            List<String> values = this.headers.get(name.trim());
            if (isOverride || CollectionUtil.isEmpty((Collection<?>) values)) {
                ArrayList<String> valueList = new ArrayList<>();
                valueList.add(value);
                this.headers.put(name.trim(), valueList);
            } else {
                values.add(value.trim());
            }
        }
        return this;
    }

    public T header(Header name, String value, boolean isOverride) {
        return header(name.toString(), value, isOverride);
    }

    public T header(Header name, String value) {
        return header(name.toString(), value, true);
    }

    public T header(String name, String value) {
        return header(name, value, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T headerMap(Map<String, String> headers, boolean isOverride) {
        if (MapUtil.isEmpty(headers)) {
            return this;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            header(entry.getKey(), StrUtil.nullToEmpty(entry.getValue()), isOverride);
        }
        return this;
    }

    public T header(Map<String, List<String>> headers) {
        return header(headers, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T header(Map<String, List<String>> headers, boolean isOverride) {
        if (MapUtil.isEmpty(headers)) {
            return this;
        }
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String name = entry.getKey();
            for (String value : entry.getValue()) {
                header(name, StrUtil.nullToEmpty(value), isOverride);
            }
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T addHeaders(Map<String, String> headers) {
        if (MapUtil.isEmpty(headers)) {
            return this;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            header(entry.getKey(), StrUtil.nullToEmpty(entry.getValue()), false);
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T removeHeader(String name) {
        if (name != null) {
            this.headers.remove(name.trim());
        }
        return this;
    }

    public T removeHeader(Header name) {
        return removeHeader(name.toString());
    }

    public Map<String, List<String>> headers() {
        return Collections.unmodifiableMap(this.headers);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T clearHeaders() {
        this.headers.clear();
        return this;
    }

    public String httpVersion() {
        return this.httpVersion;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T httpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public byte[] bodyBytes() {
        if (this.body == null) {
            return null;
        }
        return this.body.readBytes();
    }

    public String charset() {
        return this.charset.name();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T charset(String charset) {
        if (StrUtil.isNotBlank(charset)) {
            charset(Charset.forName(charset));
        }
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public T charset(Charset charset) {
        if (null != charset) {
            this.charset = charset;
        }
        return this;
    }

    public String toString() {
        StringBuilder sb = StrUtil.builder();
        sb.append("Request Headers: ").append("\r\n");
        for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
            sb.append("    ").append(entry.getKey()).append(": ").append(CollUtil.join(entry.getValue(), ",")).append("\r\n");
        }
        sb.append("Request Body: ").append("\r\n");
        sb.append("    ").append(StrUtil.str(bodyBytes(), this.charset)).append("\r\n");
        return sb.toString();
    }
}
