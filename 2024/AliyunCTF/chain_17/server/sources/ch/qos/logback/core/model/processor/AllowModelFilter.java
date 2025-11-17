package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.FilterReply;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/AllowModelFilter.class */
public class AllowModelFilter implements ModelFilter {
    final Class<? extends Model> allowedModelType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AllowModelFilter(Class<? extends Model> allowedType) {
        this.allowedModelType = allowedType;
    }

    @Override // ch.qos.logback.core.model.processor.ModelFilter
    public FilterReply decide(Model model) {
        if (model.getClass() == this.allowedModelType) {
            return FilterReply.ACCEPT;
        }
        return FilterReply.NEUTRAL;
    }
}
