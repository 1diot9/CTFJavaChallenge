package cn.hutool.http;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.ssl.DefaultSSLInfo;
import cn.hutool.http.ssl.TrustAnyHostnameVerifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/HttpConnection.class */
public class HttpConnection {
    private final URL url;
    private final Proxy proxy;
    private HttpURLConnection conn;

    public static HttpConnection create(String urlStr, Proxy proxy) {
        return create(URLUtil.toUrlForHttp(urlStr), proxy);
    }

    public static HttpConnection create(URL url, Proxy proxy) {
        return new HttpConnection(url, proxy);
    }

    public HttpConnection(URL url, Proxy proxy) {
        this.url = url;
        this.proxy = proxy;
        initConn();
    }

    public HttpConnection initConn() {
        try {
            this.conn = openHttp();
            this.conn.setDoInput(true);
            return this;
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    public Method getMethod() {
        return Method.valueOf(this.conn.getRequestMethod());
    }

    public HttpConnection setMethod(Method method) {
        if (Method.POST.equals(method) || Method.PUT.equals(method) || Method.PATCH.equals(method) || Method.DELETE.equals(method)) {
            this.conn.setUseCaches(false);
            if (Method.PATCH.equals(method)) {
                try {
                    HttpGlobalConfig.allowPatch();
                } catch (Exception e) {
                }
            }
        }
        try {
            this.conn.setRequestMethod(method.toString());
        } catch (ProtocolException e2) {
            if (Method.PATCH.equals(method)) {
                reflectSetMethod(method);
            } else {
                throw new HttpException(e2);
            }
        }
        return this;
    }

    public URL getUrl() {
        return this.url;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public HttpURLConnection getHttpURLConnection() {
        return this.conn;
    }

    public HttpConnection header(String header, String value, boolean isOverride) {
        if (null != this.conn) {
            if (isOverride) {
                this.conn.setRequestProperty(header, value);
            } else {
                this.conn.addRequestProperty(header, value);
            }
        }
        return this;
    }

    public HttpConnection header(Header header, String value, boolean isOverride) {
        return header(header.toString(), value, isOverride);
    }

    public HttpConnection header(Map<String, List<String>> headerMap, boolean isOverride) {
        if (MapUtil.isNotEmpty(headerMap)) {
            for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {
                String name = entry.getKey();
                for (String value : entry.getValue()) {
                    header(name, StrUtil.nullToEmpty(value), isOverride);
                }
            }
        }
        return this;
    }

    public String header(String name) {
        return this.conn.getHeaderField(name);
    }

    public String header(Header name) {
        return header(name.toString());
    }

    public Map<String, List<String>> headers() {
        return this.conn.getHeaderFields();
    }

    public HttpConnection setHttpsInfo(HostnameVerifier hostnameVerifier, SSLSocketFactory ssf) throws HttpException {
        HttpURLConnection conn = this.conn;
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            httpsConn.setHostnameVerifier((HostnameVerifier) ObjectUtil.defaultIfNull((TrustAnyHostnameVerifier) hostnameVerifier, DefaultSSLInfo.TRUST_ANY_HOSTNAME_VERIFIER));
            httpsConn.setSSLSocketFactory((SSLSocketFactory) ObjectUtil.defaultIfNull(ssf, DefaultSSLInfo.DEFAULT_SSF));
        }
        return this;
    }

    public HttpConnection disableCache() {
        this.conn.setUseCaches(false);
        return this;
    }

    public HttpConnection setConnectTimeout(int timeout) {
        if (timeout > 0 && null != this.conn) {
            this.conn.setConnectTimeout(timeout);
        }
        return this;
    }

    public HttpConnection setReadTimeout(int timeout) {
        if (timeout > 0 && null != this.conn) {
            this.conn.setReadTimeout(timeout);
        }
        return this;
    }

    public HttpConnection setConnectionAndReadTimeout(int timeout) {
        setConnectTimeout(timeout);
        setReadTimeout(timeout);
        return this;
    }

    public HttpConnection setCookie(String cookie) {
        if (cookie != null) {
            header(Header.COOKIE, cookie, true);
        }
        return this;
    }

    public HttpConnection setChunkedStreamingMode(int blockSize) {
        if (blockSize > 0) {
            this.conn.setChunkedStreamingMode(blockSize);
        }
        return this;
    }

    public HttpConnection setInstanceFollowRedirects(boolean isInstanceFollowRedirects) {
        this.conn.setInstanceFollowRedirects(isInstanceFollowRedirects);
        return this;
    }

    public HttpConnection connect() throws IOException {
        if (null != this.conn) {
            this.conn.connect();
        }
        return this;
    }

    public HttpConnection disconnectQuietly() {
        try {
            disconnect();
        } catch (Throwable th) {
        }
        return this;
    }

    public HttpConnection disconnect() {
        if (null != this.conn) {
            this.conn.disconnect();
        }
        return this;
    }

    public InputStream getInputStream() throws IOException {
        if (null != this.conn) {
            return this.conn.getInputStream();
        }
        return null;
    }

    public InputStream getErrorStream() {
        if (null != this.conn) {
            return this.conn.getErrorStream();
        }
        return null;
    }

    public OutputStream getOutputStream() throws IOException {
        if (null == this.conn) {
            throw new IOException("HttpURLConnection has not been initialized.");
        }
        Method method = getMethod();
        this.conn.setDoOutput(true);
        OutputStream out = this.conn.getOutputStream();
        if (method == Method.GET && method != getMethod()) {
            reflectSetMethod(method);
        }
        return out;
    }

    public int responseCode() throws IOException {
        if (null != this.conn) {
            return this.conn.getResponseCode();
        }
        return 0;
    }

    public String getCharsetName() {
        return HttpUtil.getCharset(this.conn);
    }

    public Charset getCharset() {
        Charset charset = null;
        String charsetName = getCharsetName();
        if (StrUtil.isNotBlank(charsetName)) {
            try {
                charset = Charset.forName(charsetName);
            } catch (UnsupportedCharsetException e) {
            }
        }
        return charset;
    }

    public String toString() {
        StringBuilder sb = StrUtil.builder();
        sb.append("Request URL: ").append(this.url).append("\r\n");
        sb.append("Request Method: ").append(getMethod()).append("\r\n");
        return sb.toString();
    }

    private HttpURLConnection openHttp() throws IOException {
        URLConnection conn = openConnection();
        if (false == (conn instanceof HttpURLConnection)) {
            throw new HttpException("'{}' of URL [{}] is not a http connection, make sure URL is format for http.", conn.getClass().getName(), this.url);
        }
        return (HttpURLConnection) conn;
    }

    private URLConnection openConnection() throws IOException {
        return null == this.proxy ? this.url.openConnection() : this.url.openConnection(this.proxy);
    }

    private void reflectSetMethod(Method method) {
        ReflectUtil.setFieldValue(this.conn, "method", method.name());
        Object delegate = ReflectUtil.getFieldValue(this.conn, "delegate");
        if (null != delegate) {
            ReflectUtil.setFieldValue(delegate, "method", method.name());
        }
    }
}
