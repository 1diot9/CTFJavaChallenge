package ch.qos.logback.core.property;

import ch.qos.logback.core.PropertyDefinerBase;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.net.URL;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/property/ResourceExistsPropertyDefiner.class */
public class ResourceExistsPropertyDefiner extends PropertyDefinerBase {
    String resourceStr;

    public String getResource() {
        return this.resourceStr;
    }

    public void setResource(String resource) {
        this.resourceStr = resource;
    }

    @Override // ch.qos.logback.core.spi.PropertyDefiner
    public String getPropertyValue() {
        if (OptionHelper.isNullOrEmpty(this.resourceStr)) {
            addError("The \"resource\" property must be set.");
            return null;
        }
        URL resourceURL = Loader.getResourceBySelfClassLoader(this.resourceStr);
        return booleanAsStr(resourceURL != null);
    }
}
