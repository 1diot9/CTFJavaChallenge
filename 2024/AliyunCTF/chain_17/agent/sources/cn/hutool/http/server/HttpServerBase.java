package cn.hutool.http.server;

import cn.hutool.core.util.CharsetUtil;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import java.io.Closeable;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/server/HttpServerBase.class */
public class HttpServerBase implements Closeable {
    static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
    final HttpExchange httpExchange;

    public HttpServerBase(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    public HttpExchange getHttpExchange() {
        return this.httpExchange;
    }

    public HttpContext getHttpContext() {
        return getHttpExchange().getHttpContext();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.httpExchange.close();
    }
}
