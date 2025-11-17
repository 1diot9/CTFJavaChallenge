package cn.hutool.core.net.url;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.RFC3986;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/net/url/UrlBuilder.class */
public final class UrlBuilder implements Builder<String> {
    private static final long serialVersionUID = 1;
    private static final String DEFAULT_SCHEME = "http";
    private String scheme;
    private String host;
    private int port;
    private UrlPath path;
    private UrlQuery query;
    private String fragment;
    private Charset charset;
    private boolean needEncodePercent;

    public static UrlBuilder of(URI uri, Charset charset) {
        return of(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getRawQuery(), uri.getFragment(), charset);
    }

    public static UrlBuilder ofHttpWithoutEncode(String httpUrl) {
        return ofHttp(httpUrl, null);
    }

    public static UrlBuilder ofHttp(String httpUrl) {
        return ofHttp(httpUrl, CharsetUtil.CHARSET_UTF_8);
    }

    public static UrlBuilder ofHttp(String httpUrl, Charset charset) {
        Assert.notBlank(httpUrl, "Http url must be not blank!", new Object[0]);
        String httpUrl2 = StrUtil.trimStart(httpUrl);
        if (false == StrUtil.startWithAnyIgnoreCase(httpUrl2, "http://", "https://")) {
            httpUrl2 = "http://" + httpUrl2;
        }
        return of(httpUrl2, charset);
    }

    public static UrlBuilder of(String url) {
        return of(url, CharsetUtil.CHARSET_UTF_8);
    }

    public static UrlBuilder of(String url, Charset charset) {
        Assert.notBlank(url, "Url must be not blank!", new Object[0]);
        return of(URLUtil.url(StrUtil.trim(url)), charset);
    }

    public static UrlBuilder of(URL url, Charset charset) {
        return of(url.getProtocol(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef(), charset);
    }

    public static UrlBuilder of(String scheme, String host, int port, String path, String query, String fragment, Charset charset) {
        return of(scheme, host, port, UrlPath.of(path, charset), UrlQuery.of(query, charset, false), fragment, charset);
    }

    public static UrlBuilder of(String scheme, String host, int port, UrlPath path, UrlQuery query, String fragment, Charset charset) {
        return new UrlBuilder(scheme, host, port, path, query, fragment, charset);
    }

    @Deprecated
    public static UrlBuilder create() {
        return new UrlBuilder();
    }

    public static UrlBuilder of() {
        return new UrlBuilder();
    }

    public UrlBuilder() {
        this.port = -1;
        this.charset = CharsetUtil.CHARSET_UTF_8;
    }

    public UrlBuilder(String scheme, String host, int port, UrlPath path, UrlQuery query, String fragment, Charset charset) {
        this.port = -1;
        this.charset = charset;
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        setFragment(fragment);
        this.needEncodePercent = null != charset;
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getSchemeWithDefault() {
        return StrUtil.emptyToDefault(this.scheme, DEFAULT_SCHEME);
    }

    public UrlBuilder setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public String getHost() {
        return this.host;
    }

    public UrlBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return this.port;
    }

    public int getPortWithDefault() {
        int port = getPort();
        if (port > 0) {
            return port;
        }
        URL url = toURL();
        return url.getDefaultPort();
    }

    public UrlBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public String getAuthority() {
        return this.port < 0 ? this.host : this.host + ":" + this.port;
    }

    public UrlPath getPath() {
        return this.path;
    }

    public String getPathStr() {
        return null == this.path ? "/" : this.path.build(this.charset, this.needEncodePercent);
    }

    public UrlBuilder setWithEndTag(boolean withEngTag) {
        if (null == this.path) {
            this.path = new UrlPath();
        }
        this.path.setWithEndTag(withEngTag);
        return this;
    }

    public UrlBuilder setPath(UrlPath path) {
        this.path = path;
        return this;
    }

    public UrlBuilder addPath(CharSequence path) {
        UrlPath.of(path, this.charset).getSegments().forEach((v1) -> {
            addPathSegment(v1);
        });
        return this;
    }

    public UrlBuilder addPathSegment(CharSequence segment) {
        if (StrUtil.isEmpty(segment)) {
            return this;
        }
        if (null == this.path) {
            this.path = new UrlPath();
        }
        this.path.add(segment);
        return this;
    }

    @Deprecated
    public UrlBuilder appendPath(CharSequence path) {
        return addPath(path);
    }

    public UrlQuery getQuery() {
        return this.query;
    }

    public String getQueryStr() {
        if (null == this.query) {
            return null;
        }
        return this.query.build(this.charset, this.needEncodePercent);
    }

    public UrlBuilder setQuery(UrlQuery query) {
        this.query = query;
        return this;
    }

    public UrlBuilder addQuery(String key, Object value) {
        if (StrUtil.isEmpty(key)) {
            return this;
        }
        if (this.query == null) {
            this.query = new UrlQuery();
        }
        this.query.add(key, value);
        return this;
    }

    public String getFragment() {
        return this.fragment;
    }

    public String getFragmentEncoded() {
        char[] safeChars = this.needEncodePercent ? null : new char[]{'%'};
        return RFC3986.FRAGMENT.encode(this.fragment, this.charset, safeChars);
    }

    public UrlBuilder setFragment(String fragment) {
        if (StrUtil.isEmpty(fragment)) {
            this.fragment = null;
        }
        this.fragment = StrUtil.removePrefix(fragment, "#");
        return this;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public UrlBuilder setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.builder.Builder
    public String build() {
        return toURL().toString();
    }

    public URL toURL() {
        return toURL(null);
    }

    public URL toURL(URLStreamHandler handler) {
        StringBuilder fileBuilder = new StringBuilder();
        fileBuilder.append(getPathStr());
        String query = getQueryStr();
        if (StrUtil.isNotBlank(query)) {
            fileBuilder.append('?').append(query);
        }
        if (StrUtil.isNotBlank(this.fragment)) {
            fileBuilder.append('#').append(getFragmentEncoded());
        }
        try {
            return new URL(getSchemeWithDefault(), this.host, this.port, fileBuilder.toString(), handler);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public URI toURI() {
        try {
            return toURL().toURI();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public String toString() {
        return build();
    }
}
