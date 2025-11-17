package org.springframework.boot.web.reactive.filter;

import org.springframework.core.Ordered;
import org.springframework.web.server.WebFilter;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/reactive/filter/OrderedWebFilter.class */
public interface OrderedWebFilter extends WebFilter, Ordered {
    public static final int REQUEST_WRAPPER_FILTER_MAX_ORDER = 0;
}
