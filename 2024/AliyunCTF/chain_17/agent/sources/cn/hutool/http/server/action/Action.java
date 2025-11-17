package cn.hutool.http.server.action;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import java.io.IOException;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/server/action/Action.class */
public interface Action {
    void doAction(HttpServerRequest httpServerRequest, HttpServerResponse httpServerResponse) throws IOException;
}
