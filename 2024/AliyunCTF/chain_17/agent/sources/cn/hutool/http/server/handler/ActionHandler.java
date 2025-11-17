package cn.hutool.http.server.handler;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/server/handler/ActionHandler.class */
public class ActionHandler implements HttpHandler {
    private final Action action;

    public ActionHandler(Action action) {
        this.action = action;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        this.action.doAction(new HttpServerRequest(httpExchange), new HttpServerResponse(httpExchange));
        httpExchange.close();
    }
}
