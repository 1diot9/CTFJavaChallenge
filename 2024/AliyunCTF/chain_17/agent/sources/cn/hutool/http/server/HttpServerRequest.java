package cn.hutool.http.server;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.multi.ListValueMap;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.net.multipart.MultipartFormData;
import cn.hutool.core.net.multipart.UploadSetting;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.SerializedLambda;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/server/HttpServerRequest.class */
public class HttpServerRequest extends HttpServerBase {
    private Map<String, HttpCookie> cookieCache;
    private ListValueMap<String, String> paramsCache;
    private MultipartFormData multipartFormDataCache;
    private Charset charsetCache;
    private byte[] bodyCache;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -75308287:
                if (implMethodName.equals("getName")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 5 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func1") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;)Ljava/lang/Object;") && lambda.getImplClass().equals("java/net/HttpCookie") && lambda.getImplMethodSignature().equals("()Ljava/lang/String;")) {
                    return (v0) -> {
                        return v0.getName();
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public HttpServerRequest(HttpExchange httpExchange) {
        super(httpExchange);
    }

    public String getMethod() {
        return this.httpExchange.getRequestMethod();
    }

    public boolean isGetMethod() {
        return Method.GET.name().equalsIgnoreCase(getMethod());
    }

    public boolean isPostMethod() {
        return Method.POST.name().equalsIgnoreCase(getMethod());
    }

    public URI getURI() {
        return this.httpExchange.getRequestURI();
    }

    public String getPath() {
        return getURI().getPath();
    }

    public String getQuery() {
        return getURI().getQuery();
    }

    public Headers getHeaders() {
        return this.httpExchange.getRequestHeaders();
    }

    public String getHeader(Header headerKey) {
        return getHeader(headerKey.toString());
    }

    public String getHeader(String headerKey) {
        return getHeaders().getFirst(headerKey);
    }

    public String getHeader(String headerKey, Charset charset) {
        String header = getHeader(headerKey);
        if (null != header) {
            return CharsetUtil.convert(header, CharsetUtil.CHARSET_ISO_8859_1, charset);
        }
        return null;
    }

    public String getContentType() {
        return getHeader(Header.CONTENT_TYPE);
    }

    public Charset getCharset() {
        if (null == this.charsetCache) {
            String contentType = getContentType();
            String charsetStr = HttpUtil.getCharset(contentType);
            this.charsetCache = CharsetUtil.parse(charsetStr, DEFAULT_CHARSET);
        }
        return this.charsetCache;
    }

    public String getUserAgentStr() {
        return getHeader(Header.USER_AGENT);
    }

    public UserAgent getUserAgent() {
        return UserAgentUtil.parse(getUserAgentStr());
    }

    public String getCookiesStr() {
        return getHeader(Header.COOKIE);
    }

    public Collection<HttpCookie> getCookies() {
        return getCookieMap().values();
    }

    public Map<String, HttpCookie> getCookieMap() {
        if (null == this.cookieCache) {
            this.cookieCache = Collections.unmodifiableMap(CollUtil.toMap(NetUtil.parseCookies(getCookiesStr()), new CaseInsensitiveMap(), (v0) -> {
                return v0.getName();
            }));
        }
        return this.cookieCache;
    }

    public HttpCookie getCookie(String cookieName) {
        return getCookieMap().get(cookieName);
    }

    public boolean isMultipart() {
        if (false == isPostMethod()) {
            return false;
        }
        String contentType = getContentType();
        if (StrUtil.isBlank(contentType)) {
            return false;
        }
        return contentType.toLowerCase().startsWith(FileUploadBase.MULTIPART);
    }

    public String getBody() {
        return getBody(getCharset());
    }

    public String getBody(Charset charset) {
        return StrUtil.str(getBodyBytes(), charset);
    }

    public byte[] getBodyBytes() {
        if (null == this.bodyCache) {
            this.bodyCache = IoUtil.readBytes(getBodyStream(), true);
        }
        return this.bodyCache;
    }

    public InputStream getBodyStream() {
        return this.httpExchange.getRequestBody();
    }

    public String getParam(String name) {
        return getParams().get(name, 0);
    }

    public List<String> getParams(String name) {
        return (List) getParams().get(name);
    }

    public ListValueMap<String, String> getParams() {
        if (null == this.paramsCache) {
            this.paramsCache = new ListValueMap<>();
            Charset charset = getCharset();
            String query = getQuery();
            if (StrUtil.isNotBlank(query)) {
                this.paramsCache.putAll(HttpUtil.decodeParams(query, charset, false));
            }
            if (isMultipart()) {
                this.paramsCache.putAll(getMultipart().getParamListMap());
            } else {
                String body = getBody();
                if (StrUtil.isNotBlank(body)) {
                    this.paramsCache.putAll(HttpUtil.decodeParams(body, charset, true));
                }
            }
        }
        return this.paramsCache;
    }

    /* JADX WARN: Type inference failed for: r0v7, types: [java.lang.Object[][], java.lang.String[]] */
    public String getClientIP(String... otherHeaderNames) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        if (ArrayUtil.isNotEmpty((Object[]) otherHeaderNames)) {
            headers = (String[]) ArrayUtil.addAll((Object[][]) new String[]{headers, otherHeaderNames});
        }
        return getClientIPByHeader(headers);
    }

    public String getClientIPByHeader(String... headerNames) {
        for (String header : headerNames) {
            String ip = getHeader(header);
            if (false == NetUtil.isUnknown(ip)) {
                return NetUtil.getMultistageReverseProxyIp(ip);
            }
        }
        return NetUtil.getMultistageReverseProxyIp(this.httpExchange.getRemoteAddress().getHostName());
    }

    public MultipartFormData getMultipart() throws IORuntimeException {
        if (null == this.multipartFormDataCache) {
            this.multipartFormDataCache = parseMultipart(new UploadSetting());
        }
        return this.multipartFormDataCache;
    }

    public MultipartFormData parseMultipart(UploadSetting uploadSetting) throws IORuntimeException {
        MultipartFormData formData = new MultipartFormData(uploadSetting);
        try {
            formData.parseRequestStream(getBodyStream(), getCharset());
            return formData;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
