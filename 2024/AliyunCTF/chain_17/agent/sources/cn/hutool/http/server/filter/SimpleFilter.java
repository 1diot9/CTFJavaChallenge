package cn.hutool.http.server.filter;

import com.sun.net.httpserver.Filter;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/server/filter/SimpleFilter.class */
public abstract class SimpleFilter extends Filter {
    public String description() {
        return "Anonymous Filter";
    }
}
