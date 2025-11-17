package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.spi.PropertyContainer;
import ch.qos.logback.core.util.OptionHelper;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/conditional/PropertyWrapperForScripts.class */
public class PropertyWrapperForScripts {
    PropertyContainer local;
    PropertyContainer context;

    public void setPropertyContainers(PropertyContainer local, PropertyContainer context) {
        this.local = local;
        this.context = context;
    }

    public boolean isNull(String k) {
        String val = OptionHelper.propertyLookup(k, this.local, this.context);
        return val == null;
    }

    public boolean isDefined(String k) {
        String val = OptionHelper.propertyLookup(k, this.local, this.context);
        return val != null;
    }

    public String p(String k) {
        return property(k);
    }

    public String property(String k) {
        String val = OptionHelper.propertyLookup(k, this.local, this.context);
        if (val != null) {
            return val;
        }
        return "";
    }
}
