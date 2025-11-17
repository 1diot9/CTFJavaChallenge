package ch.qos.logback.core.spi;

import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.util.COWArrayList;
import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/FilterAttachableImpl.class */
public final class FilterAttachableImpl<E> implements FilterAttachable<E> {
    COWArrayList<Filter<E>> filterList = new COWArrayList<>(new Filter[0]);

    @Override // ch.qos.logback.core.spi.FilterAttachable
    public void addFilter(Filter<E> newFilter) {
        this.filterList.add(newFilter);
    }

    @Override // ch.qos.logback.core.spi.FilterAttachable
    public void clearAllFilters() {
        this.filterList.clear();
    }

    @Override // ch.qos.logback.core.spi.FilterAttachable
    public FilterReply getFilterChainDecision(E event) {
        Filter<E>[] filterArrray = this.filterList.asTypedArray();
        for (Filter<E> filter : filterArrray) {
            FilterReply r = filter.decide(event);
            if (r == FilterReply.DENY || r == FilterReply.ACCEPT) {
                return r;
            }
        }
        return FilterReply.NEUTRAL;
    }

    @Override // ch.qos.logback.core.spi.FilterAttachable
    public List<Filter<E>> getCopyOfAttachedFiltersList() {
        return new ArrayList(this.filterList);
    }
}
