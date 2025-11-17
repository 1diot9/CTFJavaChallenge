package cn.hutool.http;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/GlobalHeaders.class */
public enum GlobalHeaders {
    INSTANCE;

    final Map<String, List<String>> headers = new HashMap();

    GlobalHeaders() {
        putDefault(false);
    }

    public GlobalHeaders putDefault(boolean isReset) {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        System.setProperty("jdk.tls.allowUnsafeServerCertChange", "true");
        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        if (isReset) {
            this.headers.clear();
        }
        header(Header.ACCEPT, "text/html,application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", true);
        header(Header.ACCEPT_ENCODING, "gzip, deflate", true);
        header(Header.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8", true);
        header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36 Hutool", true);
        return this;
    }

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
        return this.headers.get(name.trim());
    }

    public String header(Header name) {
        if (null == name) {
            return null;
        }
        return header(name.toString());
    }

    public synchronized GlobalHeaders header(String name, String value, boolean isOverride) {
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

    public GlobalHeaders header(Header name, String value, boolean isOverride) {
        return header(name.toString(), value, isOverride);
    }

    public GlobalHeaders header(Header name, String value) {
        return header(name.toString(), value, true);
    }

    public GlobalHeaders header(String name, String value) {
        return header(name, value, true);
    }

    public GlobalHeaders header(Map<String, List<String>> headers) {
        if (MapUtil.isEmpty(headers)) {
            return this;
        }
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String name = entry.getKey();
            for (String value : entry.getValue()) {
                header(name, StrUtil.nullToEmpty(value), false);
            }
        }
        return this;
    }

    public synchronized GlobalHeaders removeHeader(String name) {
        if (name != null) {
            this.headers.remove(name.trim());
        }
        return this;
    }

    public GlobalHeaders removeHeader(Header name) {
        return removeHeader(name.toString());
    }

    public Map<String, List<String>> headers() {
        return Collections.unmodifiableMap(this.headers);
    }

    public synchronized GlobalHeaders clearHeaders() {
        this.headers.clear();
        return this;
    }
}
