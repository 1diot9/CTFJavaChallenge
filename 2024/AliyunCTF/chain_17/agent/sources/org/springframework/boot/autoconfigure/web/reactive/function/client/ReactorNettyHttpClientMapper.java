package org.springframework.boot.autoconfigure.web.reactive.function.client;

import java.util.Collection;
import org.springframework.util.Assert;
import reactor.netty.http.client.HttpClient;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/ReactorNettyHttpClientMapper.class */
public interface ReactorNettyHttpClientMapper {
    HttpClient configure(HttpClient httpClient);

    static ReactorNettyHttpClientMapper of(Collection<ReactorNettyHttpClientMapper> mappers) {
        Assert.notNull(mappers, "Mappers must not be null");
        return of((ReactorNettyHttpClientMapper[]) mappers.toArray(x$0 -> {
            return new ReactorNettyHttpClientMapper[x$0];
        }));
    }

    static ReactorNettyHttpClientMapper of(ReactorNettyHttpClientMapper... mappers) {
        Assert.notNull(mappers, "Mappers must not be null");
        return httpClient -> {
            for (ReactorNettyHttpClientMapper mapper : mappers) {
                httpClient = mapper.configure(httpClient);
            }
            return httpClient;
        };
    }
}
