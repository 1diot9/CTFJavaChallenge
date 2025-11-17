package cn.hutool.http;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.resource.BytesResource;
import cn.hutool.core.io.resource.FileResource;
import cn.hutool.core.io.resource.MultiFileResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.map.TableMap;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpInterceptor;
import cn.hutool.http.body.FormUrlEncodedBody;
import cn.hutool.http.body.MultipartBody;
import cn.hutool.http.body.RequestBody;
import cn.hutool.http.body.ResourceBody;
import cn.hutool.http.cookie.GlobalCookieManager;
import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.Proxy;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import org.apache.coyote.http11.Constants;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/HttpRequest.class */
public class HttpRequest extends HttpBase<HttpRequest> {
    private HttpConfig config;
    private UrlBuilder url;
    private URLStreamHandler urlHandler;
    private Method method;
    private HttpConnection httpConnection;
    private Map<String, Object> form;
    private String cookie;
    private boolean isMultiPart;
    private boolean isRest;
    private int redirectCount;

    public static HttpRequest post(String url) {
        return of(url).method(Method.POST);
    }

    public static HttpRequest get(String url) {
        return of(url).method(Method.GET);
    }

    public static HttpRequest head(String url) {
        return of(url).method(Method.HEAD);
    }

    public static HttpRequest options(String url) {
        return of(url).method(Method.OPTIONS);
    }

    public static HttpRequest put(String url) {
        return of(url).method(Method.PUT);
    }

    public static HttpRequest patch(String url) {
        return of(url).method(Method.PATCH);
    }

    public static HttpRequest delete(String url) {
        return of(url).method(Method.DELETE);
    }

    public static HttpRequest trace(String url) {
        return of(url).method(Method.TRACE);
    }

    public static HttpRequest of(String url) {
        return of(url, HttpGlobalConfig.isDecodeUrl() ? DEFAULT_CHARSET : null);
    }

    public static HttpRequest of(String url, Charset charset) {
        return of(UrlBuilder.ofHttp(url, charset));
    }

    public static HttpRequest of(UrlBuilder url) {
        return new HttpRequest(url);
    }

    public static void setGlobalTimeout(int customTimeout) {
        HttpGlobalConfig.setTimeout(customTimeout);
    }

    public static CookieManager getCookieManager() {
        return GlobalCookieManager.getCookieManager();
    }

    public static void setCookieManager(CookieManager customCookieManager) {
        GlobalCookieManager.setCookieManager(customCookieManager);
    }

    public static void closeCookie() {
        GlobalCookieManager.setCookieManager(null);
    }

    @Deprecated
    public HttpRequest(String url) {
        this(UrlBuilder.ofHttp(url));
    }

    public HttpRequest(UrlBuilder url) {
        this.config = HttpConfig.create();
        this.method = Method.GET;
        this.url = (UrlBuilder) Assert.notNull(url, "URL must be not null!", new Object[0]);
        Charset charset = url.getCharset();
        if (null != charset) {
            charset(charset);
        }
        header(GlobalHeaders.INSTANCE.headers);
    }

    public String getUrl() {
        return this.url.toString();
    }

    public HttpRequest setUrl(String url) {
        return setUrl(UrlBuilder.ofHttp(url, this.charset));
    }

    public HttpRequest setUrl(UrlBuilder urlBuilder) {
        this.url = urlBuilder;
        return this;
    }

    public HttpRequest setUrlHandler(URLStreamHandler urlHandler) {
        this.urlHandler = urlHandler;
        return this;
    }

    public Method getMethod() {
        return this.method;
    }

    public HttpRequest setMethod(Method method) {
        return method(method);
    }

    public HttpConnection getConnection() {
        return this.httpConnection;
    }

    public HttpRequest method(Method method) {
        this.method = method;
        return this;
    }

    public HttpRequest contentType(String contentType) {
        header(Header.CONTENT_TYPE, contentType);
        return this;
    }

    public HttpRequest keepAlive(boolean isKeepAlive) {
        header(Header.CONNECTION, isKeepAlive ? Constants.KEEP_ALIVE_HEADER_NAME : "Close");
        return this;
    }

    public boolean isKeepAlive() {
        String connection = header(Header.CONNECTION);
        return connection == null ? false == "HTTP/1.0".equalsIgnoreCase(this.httpVersion) : false == Constants.CLOSE.equalsIgnoreCase(connection);
    }

    public String contentLength() {
        return header(Header.CONTENT_LENGTH);
    }

    public HttpRequest contentLength(int value) {
        header(Header.CONTENT_LENGTH, String.valueOf(value));
        return this;
    }

    public HttpRequest cookie(Collection<HttpCookie> cookies) {
        return cookie(CollUtil.isEmpty((Collection<?>) cookies) ? null : (HttpCookie[]) cookies.toArray(new HttpCookie[0]));
    }

    public HttpRequest cookie(HttpCookie... cookies) {
        if (ArrayUtil.isEmpty((Object[]) cookies)) {
            return disableCookie();
        }
        return cookie(ArrayUtil.join((Object[]) cookies, (CharSequence) "; "));
    }

    public HttpRequest cookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public HttpRequest disableCookie() {
        return cookie("");
    }

    public HttpRequest enableDefaultCookie() {
        return cookie((String) null);
    }

    public HttpRequest form(String name, Object value) {
        String strValue;
        if (StrUtil.isBlank(name) || ObjectUtil.isNull(value)) {
            return this;
        }
        this.body = null;
        if (value instanceof File) {
            return form(name, (File) value);
        }
        if (value instanceof Resource) {
            return form(name, (Resource) value);
        }
        if (value instanceof Iterable) {
            strValue = CollUtil.join((Iterable) value, ",");
        } else if (ArrayUtil.isArray(value)) {
            if (File.class == ArrayUtil.getComponentType(value)) {
                return form(name, (File[]) value);
            }
            strValue = ArrayUtil.join((Object[]) value, (CharSequence) ",");
        } else {
            strValue = Convert.toStr(value, null);
        }
        return putToForm(name, strValue);
    }

    public HttpRequest form(String name, Object value, Object... parameters) {
        form(name, value);
        for (int i = 0; i < parameters.length; i += 2) {
            form(parameters[i].toString(), parameters[i + 1]);
        }
        return this;
    }

    public HttpRequest form(Map<String, Object> formMap) {
        if (MapUtil.isNotEmpty(formMap)) {
            formMap.forEach(this::form);
        }
        return this;
    }

    public HttpRequest formStr(Map<String, String> formMapStr) {
        if (MapUtil.isNotEmpty(formMapStr)) {
            formMapStr.forEach((v1, v2) -> {
                form(v1, v2);
            });
        }
        return this;
    }

    public HttpRequest form(String name, File... files) {
        if (ArrayUtil.isEmpty((Object[]) files)) {
            return this;
        }
        if (1 == files.length) {
            File file = files[0];
            return form(name, file, file.getName());
        }
        return form(name, (Resource) new MultiFileResource(files));
    }

    public HttpRequest form(String name, File file) {
        return form(name, file, file.getName());
    }

    public HttpRequest form(String name, File file, String fileName) {
        if (null != file) {
            form(name, (Resource) new FileResource(file, fileName));
        }
        return this;
    }

    public HttpRequest form(String name, byte[] fileBytes, String fileName) {
        if (null != fileBytes) {
            form(name, (Resource) new BytesResource(fileBytes, fileName));
        }
        return this;
    }

    public HttpRequest form(String name, Resource resource) {
        if (null != resource) {
            if (false == isKeepAlive()) {
                keepAlive(true);
            }
            this.isMultiPart = true;
            return putToForm(name, resource);
        }
        return this;
    }

    public Map<String, Object> form() {
        return this.form;
    }

    public Map<String, Resource> fileForm() {
        Map<String, Resource> result = MapUtil.newHashMap();
        this.form.forEach((key, value) -> {
            if (value instanceof Resource) {
                result.put(key, (Resource) value);
            }
        });
        return result;
    }

    public HttpRequest body(String body) {
        return body(body, null);
    }

    public HttpRequest body(String body, String contentType) {
        byte[] bytes = StrUtil.bytes(body, this.charset);
        body(bytes);
        this.form = null;
        if (null != contentType) {
            contentType(contentType);
        } else {
            contentType = HttpUtil.getContentTypeByRequestBody(body);
            if (null != contentType && ContentType.isDefault(header(Header.CONTENT_TYPE))) {
                if (null != this.charset) {
                    contentType = ContentType.build(contentType, this.charset);
                }
                contentType(contentType);
            }
        }
        if (StrUtil.containsAnyIgnoreCase(contentType, "json", "xml")) {
            this.isRest = true;
            contentLength(bytes.length);
        }
        return this;
    }

    public HttpRequest body(byte[] bodyBytes) {
        if (ArrayUtil.isNotEmpty(bodyBytes)) {
            return body(new BytesResource(bodyBytes));
        }
        return this;
    }

    public HttpRequest body(Resource resource) {
        if (null != resource) {
            this.body = resource;
        }
        return this;
    }

    public HttpRequest setConfig(HttpConfig config) {
        this.config = config;
        return this;
    }

    public HttpRequest timeout(int milliseconds) {
        this.config.timeout(milliseconds);
        return this;
    }

    public HttpRequest setConnectionTimeout(int milliseconds) {
        this.config.setConnectionTimeout(milliseconds);
        return this;
    }

    public HttpRequest setReadTimeout(int milliseconds) {
        this.config.setReadTimeout(milliseconds);
        return this;
    }

    public HttpRequest disableCache() {
        this.config.disableCache();
        return this;
    }

    public HttpRequest setFollowRedirects(boolean isFollowRedirects) {
        if (isFollowRedirects) {
            if (this.config.maxRedirectCount <= 0) {
                return setMaxRedirectCount(2);
            }
        } else if (this.config.maxRedirectCount < 0) {
            return setMaxRedirectCount(0);
        }
        return this;
    }

    public HttpRequest setFollowRedirectsCookie(boolean followRedirectsCookie) {
        this.config.setFollowRedirectsCookie(followRedirectsCookie);
        return this;
    }

    public HttpRequest setMaxRedirectCount(int maxRedirectCount) {
        this.config.setMaxRedirectCount(maxRedirectCount);
        return this;
    }

    public HttpRequest setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.config.setHostnameVerifier(hostnameVerifier);
        return this;
    }

    public HttpRequest setHttpProxy(String host, int port) {
        this.config.setHttpProxy(host, port);
        return this;
    }

    public HttpRequest setProxy(Proxy proxy) {
        this.config.setProxy(proxy);
        return this;
    }

    public HttpRequest setSSLSocketFactory(SSLSocketFactory ssf) {
        this.config.setSSLSocketFactory(ssf);
        return this;
    }

    public HttpRequest setSSLProtocol(String protocol) {
        this.config.setSSLProtocol(protocol);
        return this;
    }

    public HttpRequest setRest(boolean isRest) {
        this.isRest = isRest;
        return this;
    }

    public HttpRequest setChunkedStreamingMode(int blockSize) {
        this.config.setBlockSize(blockSize);
        return this;
    }

    public HttpRequest addInterceptor(HttpInterceptor<HttpRequest> interceptor) {
        return addRequestInterceptor(interceptor);
    }

    public HttpRequest addRequestInterceptor(HttpInterceptor<HttpRequest> interceptor) {
        this.config.addRequestInterceptor(interceptor);
        return this;
    }

    public HttpRequest addResponseInterceptor(HttpInterceptor<HttpResponse> interceptor) {
        this.config.addResponseInterceptor(interceptor);
        return this;
    }

    public HttpResponse execute() {
        return execute(false);
    }

    public HttpResponse executeAsync() {
        return execute(true);
    }

    public HttpResponse execute(boolean isAsync) {
        return doExecute(isAsync, this.config.requestInterceptors, this.config.responseInterceptors);
    }

    public void then(Consumer<HttpResponse> consumer) {
        HttpResponse response = execute(true);
        Throwable th = null;
        try {
            try {
                consumer.accept(response);
                if (response != null) {
                    if (0 != 0) {
                        try {
                            response.close();
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            return;
                        }
                    }
                    response.close();
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (response != null) {
                if (th != null) {
                    try {
                        response.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    response.close();
                }
            }
            throw th4;
        }
    }

    public <T> T thenFunction(Function<HttpResponse, T> function) {
        HttpResponse response = execute(true);
        Throwable th = null;
        try {
            try {
                T apply = function.apply(response);
                if (response != null) {
                    if (0 != 0) {
                        try {
                            response.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        response.close();
                    }
                }
                return apply;
            } finally {
            }
        } catch (Throwable th3) {
            if (response != null) {
                if (th != null) {
                    try {
                        response.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    response.close();
                }
            }
            throw th3;
        }
    }

    public HttpRequest basicAuth(String username, String password) {
        return auth(HttpUtil.buildBasicAuth(username, password, this.charset));
    }

    public HttpRequest basicProxyAuth(String username, String password) {
        return proxyAuth(HttpUtil.buildBasicAuth(username, password, this.charset));
    }

    public HttpRequest bearerAuth(String token) {
        return auth("Bearer " + token);
    }

    public HttpRequest auth(String content) {
        header(Header.AUTHORIZATION, content, true);
        return this;
    }

    public HttpRequest proxyAuth(String content) {
        header(Header.PROXY_AUTHORIZATION, content, true);
        return this;
    }

    @Override // cn.hutool.http.HttpBase
    public String toString() {
        StringBuilder sb = StrUtil.builder();
        sb.append("Request Url: ").append(this.url.setCharset(this.charset)).append("\r\n");
        sb.append(super.toString());
        return sb.toString();
    }

    private HttpResponse doExecute(boolean isAsync, HttpInterceptor.Chain<HttpRequest> requestInterceptors, HttpInterceptor.Chain<HttpResponse> responseInterceptors) {
        if (null != requestInterceptors) {
            Iterator<HttpInterceptor<HttpRequest>> it = requestInterceptors.iterator();
            while (it.hasNext()) {
                HttpInterceptor<HttpRequest> interceptor = it.next();
                interceptor.process(this);
            }
        }
        urlWithParamIfGet();
        initConnection();
        send();
        HttpResponse httpResponse = sendRedirectIfPossible(isAsync);
        if (null == httpResponse) {
            httpResponse = new HttpResponse(this.httpConnection, this.config, this.charset, isAsync, isIgnoreResponseBody());
        }
        if (null != responseInterceptors) {
            Iterator<HttpInterceptor<HttpResponse>> it2 = responseInterceptors.iterator();
            while (it2.hasNext()) {
                HttpInterceptor<HttpResponse> interceptor2 = it2.next();
                interceptor2.process(httpResponse);
            }
        }
        return httpResponse;
    }

    private void initConnection() {
        if (null != this.httpConnection) {
            this.httpConnection.disconnectQuietly();
        }
        this.httpConnection = HttpConnection.create(this.url.setCharset(this.charset).toURL(this.urlHandler), this.config.proxy).setConnectTimeout(this.config.connectionTimeout).setReadTimeout(this.config.readTimeout).setMethod(this.method).setHttpsInfo(this.config.hostnameVerifier, this.config.ssf).setInstanceFollowRedirects(false).setChunkedStreamingMode(this.config.blockSize).header(this.headers, true);
        if (null != this.cookie) {
            this.httpConnection.setCookie(this.cookie);
        } else {
            GlobalCookieManager.add(this.httpConnection);
        }
        if (this.config.isDisableCache) {
            this.httpConnection.disableCache();
        }
    }

    private void urlWithParamIfGet() {
        if (Method.GET.equals(this.method) && false == this.isRest && this.redirectCount <= 0) {
            UrlQuery query = this.url.getQuery();
            if (null == query) {
                query = new UrlQuery();
                this.url.setQuery(query);
            }
            if (null != this.body) {
                query.parse(StrUtil.str(this.body.readBytes(), this.charset), this.charset);
            } else {
                query.addAll(this.form);
            }
        }
    }

    private HttpResponse sendRedirectIfPossible(boolean isAsync) {
        UrlBuilder redirectUrl;
        if (this.config.maxRedirectCount > 0) {
            try {
                int responseCode = this.httpConnection.responseCode();
                if (this.config.followRedirectsCookie) {
                    GlobalCookieManager.store(this.httpConnection);
                }
                if (responseCode != 200 && HttpStatus.isRedirected(responseCode)) {
                    String location = this.httpConnection.header(Header.LOCATION);
                    if (false == HttpUtil.isHttp(location) && false == HttpUtil.isHttps(location)) {
                        if (false == location.startsWith("/")) {
                            location = StrUtil.addSuffixIfNot(this.url.getPathStr(), "/") + location;
                        }
                        redirectUrl = UrlBuilder.of(this.url.getScheme(), this.url.getHost(), this.url.getPort(), location, (String) null, (String) null, this.charset);
                    } else {
                        redirectUrl = UrlBuilder.ofHttpWithoutEncode(location);
                    }
                    setUrl(redirectUrl);
                    if (this.redirectCount < this.config.maxRedirectCount) {
                        this.redirectCount++;
                        return doExecute(isAsync, this.config.interceptorOnRedirect ? this.config.requestInterceptors : null, this.config.interceptorOnRedirect ? this.config.responseInterceptors : null);
                    }
                    return null;
                }
                return null;
            } catch (IOException e) {
                this.httpConnection.disconnectQuietly();
                throw new HttpException(e);
            }
        }
        return null;
    }

    private void send() throws IORuntimeException {
        try {
            if (Method.POST.equals(this.method) || Method.PUT.equals(this.method) || Method.DELETE.equals(this.method) || this.isRest) {
                if (isMultipart()) {
                    sendMultipart();
                } else {
                    sendFormUrlEncoded();
                }
            } else {
                this.httpConnection.connect();
            }
        } catch (IOException e) {
            this.httpConnection.disconnectQuietly();
            throw new IORuntimeException(e);
        }
    }

    private void sendFormUrlEncoded() throws IOException {
        RequestBody body;
        if (StrUtil.isBlank(header(Header.CONTENT_TYPE))) {
            this.httpConnection.header(Header.CONTENT_TYPE, ContentType.FORM_URLENCODED.toString(this.charset), true);
        }
        if (null != this.body) {
            body = ResourceBody.create(this.body);
        } else {
            body = FormUrlEncodedBody.create(this.form, this.charset);
        }
        body.writeClose(this.httpConnection.getOutputStream());
    }

    private void sendMultipart() throws IOException {
        MultipartBody multipartBody = MultipartBody.create(this.form, this.charset);
        this.httpConnection.header(Header.CONTENT_TYPE, multipartBody.getContentType(), true);
        multipartBody.writeClose(this.httpConnection.getOutputStream());
    }

    private boolean isIgnoreResponseBody() {
        return Method.HEAD == this.method || Method.CONNECT == this.method || Method.OPTIONS == this.method || Method.TRACE == this.method;
    }

    private boolean isMultipart() {
        if (this.isMultiPart) {
            return true;
        }
        String contentType = header(Header.CONTENT_TYPE);
        return StrUtil.isNotEmpty(contentType) && contentType.startsWith(ContentType.MULTIPART.getValue());
    }

    private HttpRequest putToForm(String name, Object value) {
        if (null == name || null == value) {
            return this;
        }
        if (null == this.form) {
            this.form = new TableMap(16);
        }
        this.form.put(name, value);
        return this;
    }
}
