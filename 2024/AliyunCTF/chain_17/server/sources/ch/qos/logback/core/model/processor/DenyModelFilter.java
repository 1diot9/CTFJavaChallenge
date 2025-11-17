package ch.qos.logback.core.model.processor;

import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.spi.FilterReply;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/model/processor/DenyModelFilter.class */
public class DenyModelFilter implements ModelFilter {
    final Class<? extends Model> deniedModelType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DenyModelFilter(Class<? extends Model> deniedModelType) {
        this.deniedModelType = deniedModelType;
    }

    @Override // ch.qos.logback.core.model.processor.ModelFilter
    public FilterReply decide(Model model) {
        if (model.getClass() == this.deniedModelType) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}
