package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.util.AggregationType;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/ImplicitModelData.class */
public class ImplicitModelData {
    public final PropertySetter parentBean;
    public final AggregationType aggregationType;
    public final String propertyName;
    public boolean inError;

    public ImplicitModelData(PropertySetter parentBean, AggregationType aggregationType, String propertyName) {
        this.parentBean = parentBean;
        this.aggregationType = aggregationType;
        this.propertyName = propertyName;
    }

    public AggregationType getAggregationType() {
        return this.aggregationType;
    }
}
