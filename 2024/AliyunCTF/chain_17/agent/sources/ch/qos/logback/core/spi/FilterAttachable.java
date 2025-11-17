package ch.qos.logback.core.spi;

import ch.qos.logback.core.filter.Filter;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/FilterAttachable.class */
public interface FilterAttachable<E> {
    void addFilter(Filter<E> filter);

    void clearAllFilters();

    List<Filter<E>> getCopyOfAttachedFiltersList();

    FilterReply getFilterChainDecision(E e);
}
