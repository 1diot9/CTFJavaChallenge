package cn.hutool.http.server;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.GlobalThreadPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.action.Action;
import cn.hutool.http.server.action.RootAction;
import cn.hutool.http.server.filter.HttpFilter;
import cn.hutool.http.server.filter.SimpleFilter;
import cn.hutool.http.server.handler.ActionHandler;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/server/SimpleServer.class */
public class SimpleServer {
    private final HttpServer server;
    private final List<Filter> filters;

    public SimpleServer(int port) {
        this(new InetSocketAddress(port));
    }

    public SimpleServer(String hostname, int port) {
        this(new InetSocketAddress(hostname, port));
    }

    public SimpleServer(InetSocketAddress address) {
        this(address, (HttpsConfigurator) null);
    }

    public SimpleServer(InetSocketAddress address, HttpsConfigurator configurator) {
        try {
            if (null != configurator) {
                HttpsServer server = HttpsServer.create(address, 0);
                server.setHttpsConfigurator(configurator);
                this.server = server;
            } else {
                this.server = HttpServer.create(address, 0);
            }
            setExecutor(GlobalThreadPool.getExecutor());
            this.filters = new ArrayList();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public SimpleServer addFilter(Filter filter) {
        this.filters.add(filter);
        return this;
    }

    public SimpleServer addFilter(final HttpFilter filter) {
        return addFilter(new SimpleFilter() { // from class: cn.hutool.http.server.SimpleServer.1
            public void doFilter(HttpExchange httpExchange, Filter.Chain chain) throws IOException {
                filter.doFilter(new HttpServerRequest(httpExchange), new HttpServerResponse(httpExchange), chain);
            }
        });
    }

    public SimpleServer addHandler(String path, HttpHandler handler) {
        createContext(path, handler);
        return this;
    }

    public HttpContext createContext(String path, HttpHandler handler) {
        HttpContext context = this.server.createContext(StrUtil.addPrefixIfNot(path, "/"), handler);
        context.getFilters().addAll(this.filters);
        return context;
    }

    public SimpleServer setRoot(String root) {
        return setRoot(new File(root));
    }

    public SimpleServer setRoot(File root) {
        return addAction("/", new RootAction(root));
    }

    public SimpleServer addAction(String path, Action action) {
        return addHandler(path, new ActionHandler(action));
    }

    public SimpleServer setExecutor(Executor executor) {
        this.server.setExecutor(executor);
        return this;
    }

    public HttpServer getRawServer() {
        return this.server;
    }

    public InetSocketAddress getAddress() {
        return this.server.getAddress();
    }

    public void start() {
        InetSocketAddress address = getAddress();
        Console.log("Hutool Simple Http Server listen on 【{}:{}】", address.getHostName(), Integer.valueOf(address.getPort()));
        this.server.start();
    }
}
