package org.springframework.boot.web.servlet.filter;

import org.springframework.web.filter.HiddenHttpMethodFilter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/filter/OrderedHiddenHttpMethodFilter.class */
public class OrderedHiddenHttpMethodFilter extends HiddenHttpMethodFilter implements OrderedFilter {
    public static final int DEFAULT_ORDER = -10000;
    private int order = -10000;

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
