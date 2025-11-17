package org.springframework.boot.web.servlet.filter;

import jakarta.servlet.Filter;
import org.springframework.core.Ordered;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/filter/OrderedFilter.class */
public interface OrderedFilter extends Filter, Ordered {
    public static final int REQUEST_WRAPPER_FILTER_MAX_ORDER = 0;
}
