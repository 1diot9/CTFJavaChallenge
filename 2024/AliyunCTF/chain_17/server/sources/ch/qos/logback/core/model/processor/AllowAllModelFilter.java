package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.FilterReply;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/AllowAllModelFilter.class */
public class AllowAllModelFilter implements ModelFilter {
    @Override // ch.qos.logback.core.model.processor.ModelFilter
    public FilterReply decide(Model model) {
        return FilterReply.ACCEPT;
    }
}
