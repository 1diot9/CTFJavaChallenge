package org.apache.logging.slf4j;

import aQute.bnd.annotation.spi.ServiceProvider;
import org.apache.logging.log4j.spi.Provider;

@ServiceProvider(value = Provider.class, resolution = "optional")
/* loaded from: agent.jar:BOOT-INF/lib/log4j-to-slf4j-2.21.1.jar:org/apache/logging/slf4j/SLF4JProvider.class */
public class SLF4JProvider extends Provider {
    public SLF4JProvider() {
        super(15, "2.6.0", SLF4JLoggerContextFactory.class, MDCContextMap.class);
    }
}
