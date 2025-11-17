package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.util.AggregationType;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/action/ImplicitModelDataForComplexProperty.class */
public class ImplicitModelDataForComplexProperty extends ImplicitModelData {
    private Object nestedComplexProperty;

    public ImplicitModelDataForComplexProperty(PropertySetter parentBean, AggregationType aggregationType, String propertyName) {
        super(parentBean, aggregationType, propertyName);
    }

    public Object getNestedComplexProperty() {
        return this.nestedComplexProperty;
    }

    public void setNestedComplexProperty(Object nestedComplexProperty) {
        this.nestedComplexProperty = nestedComplexProperty;
    }
}
