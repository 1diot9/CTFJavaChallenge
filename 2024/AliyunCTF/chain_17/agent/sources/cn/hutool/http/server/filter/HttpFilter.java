package cn.hutool.http.server.filter;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import com.sun.net.httpserver.Filter;
import java.io.IOException;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/server/filter/HttpFilter.class */
public interface HttpFilter {
    void doFilter(HttpServerRequest httpServerRequest, HttpServerResponse httpServerResponse, Filter.Chain chain) throws IOException;
}
