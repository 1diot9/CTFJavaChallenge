package org.springframework.http.server.reactive;

import io.netty5.channel.Channel;
import io.netty5.handler.codec.http.HttpHeaderNames;
import io.netty5.handler.codec.http.headers.HttpCookiePair;
import io.netty5.handler.ssl.SslHandler;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.SSLSession;
import org.apache.commons.logging.Log;
import org.springframework.beans.PropertyAccessor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.Netty5DataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.http.support.Netty5HeadersAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.netty5.ChannelOperationsId;
import reactor.netty5.Connection;
import reactor.netty5.http.server.HttpServerRequest;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/reactive/ReactorNetty2ServerHttpRequest.class */
class ReactorNetty2ServerHttpRequest extends AbstractServerHttpRequest {
    private static final Log logger = HttpLogging.forLogName(ReactorNetty2ServerHttpRequest.class);
    private static final AtomicLong logPrefixIndex = new AtomicLong();
    private final HttpServerRequest request;
    private final Netty5DataBufferFactory bufferFactory;

    public ReactorNetty2ServerHttpRequest(HttpServerRequest request, Netty5DataBufferFactory bufferFactory) throws URISyntaxException {
        super(HttpMethod.valueOf(request.method().name()), initUri(request), "", new Netty5HeadersAdapter(request.requestHeaders()));
        Assert.notNull(bufferFactory, "DataBufferFactory must not be null");
        this.request = request;
        this.bufferFactory = bufferFactory;
    }

    private static URI initUri(HttpServerRequest request) throws URISyntaxException {
        Assert.notNull(request, "HttpServerRequest must not be null");
        return new URI(resolveBaseUrl(request) + resolveRequestUri(request));
    }

    private static URI resolveBaseUrl(HttpServerRequest request) throws URISyntaxException {
        int portIndex;
        String scheme = getScheme(request);
        InetSocketAddress hostAddress = request.hostAddress();
        if (hostAddress != null) {
            return new URI(scheme, null, hostAddress.getHostString(), hostAddress.getPort(), null, null, null);
        }
        CharSequence charSequence = request.requestHeaders().get(HttpHeaderNames.HOST);
        if (charSequence != null) {
            String header = charSequence.toString();
            if (header.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX)) {
                portIndex = header.indexOf(58, header.indexOf(93));
            } else {
                portIndex = header.indexOf(58);
            }
            if (portIndex != -1) {
                try {
                    return new URI(scheme, null, header.substring(0, portIndex), Integer.parseInt(header, portIndex + 1, header.length(), 10), null, null, null);
                } catch (NumberFormatException e) {
                    throw new URISyntaxException(header, "Unable to parse port", portIndex);
                }
            }
            return new URI(scheme, header, null, null);
        }
        throw new IllegalStateException("Neither local hostAddress nor HOST header available");
    }

    private static String getScheme(HttpServerRequest request) {
        return request.scheme();
    }

    private static String resolveRequestUri(HttpServerRequest request) {
        char c;
        String uri = request.uri();
        for (int i = 0; i < uri.length() && (c = uri.charAt(i)) != '/' && c != '?' && c != '#'; i++) {
            if (c == ':' && i + 2 < uri.length() && uri.charAt(i + 1) == '/' && uri.charAt(i + 2) == '/') {
                for (int j = i + 3; j < uri.length(); j++) {
                    char c2 = uri.charAt(j);
                    if (c2 == '/' || c2 == '?' || c2 == '#') {
                        return uri.substring(j);
                    }
                }
                return "";
            }
        }
        return uri;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected MultiValueMap<String, HttpCookie> initCookies() {
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        for (CharSequence name : this.request.allCookies().keySet()) {
            for (HttpCookiePair cookie : (List) this.request.allCookies().get(name)) {
                CharSequence cookieValue = cookie.value();
                HttpCookie httpCookie = new HttpCookie(name.toString(), cookieValue != null ? cookieValue.toString() : null);
                cookies.add(name.toString(), httpCookie);
            }
        }
        return cookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public InetSocketAddress getLocalAddress() {
        return this.request.hostAddress();
    }

    @Override // org.springframework.http.server.reactive.ServerHttpRequest
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return this.request.remoteAddress();
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected SslInfo initSslInfo() {
        Channel channel = this.request.channel();
        SslHandler sslHandler = channel.pipeline().get(SslHandler.class);
        if (sslHandler == null && channel.parent() != null) {
            sslHandler = (SslHandler) channel.parent().pipeline().get(SslHandler.class);
        }
        if (sslHandler != null) {
            SSLSession session = sslHandler.engine().getSession();
            return new DefaultSslInfo(session);
        }
        return null;
    }

    @Override // org.springframework.http.ReactiveHttpInputMessage
    public Flux<DataBuffer> getBody() {
        Flux transferOwnership = this.request.receive().transferOwnership();
        Netty5DataBufferFactory netty5DataBufferFactory = this.bufferFactory;
        Objects.requireNonNull(netty5DataBufferFactory);
        return transferOwnership.map(netty5DataBufferFactory::wrap);
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    public <T> T getNativeRequest() {
        return (T) this.request;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    @Nullable
    protected String initId() {
        Connection connection = this.request;
        if (connection instanceof Connection) {
            Connection connection2 = connection;
            return connection2.channel().id().asShortText() + "-" + logPrefixIndex.incrementAndGet();
        }
        return null;
    }

    @Override // org.springframework.http.server.reactive.AbstractServerHttpRequest
    protected String initLogPrefix() {
        String id = null;
        ChannelOperationsId channelOperationsId = this.request;
        if (channelOperationsId instanceof ChannelOperationsId) {
            ChannelOperationsId operationsId = channelOperationsId;
            id = logger.isDebugEnabled() ? operationsId.asLongText() : operationsId.asShortText();
        }
        if (id != null) {
            return id;
        }
        Connection connection = this.request;
        if (connection instanceof Connection) {
            Connection connection2 = connection;
            return connection2.channel().id().asShortText() + "-" + logPrefixIndex.incrementAndGet();
        }
        return getId();
    }
}
