package ch.qos.logback.core.property;

import ch.qos.logback.core.PropertyDefinerBase;
import ch.qos.logback.core.util.NetworkAddressUtil;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/property/CanonicalHostNamePropertyDefiner.class */
public class CanonicalHostNamePropertyDefiner extends PropertyDefinerBase {
    @Override // ch.qos.logback.core.spi.PropertyDefiner
    public String getPropertyValue() {
        return new NetworkAddressUtil(getContext()).safelyGetCanonicalLocalHostName();
    }
}
