package org.apache.logging.slf4j;

import org.apache.logging.log4j.util.ProviderActivator;
import org.osgi.annotation.bundle.Header;
import org.osgi.annotation.bundle.Headers;

@Headers({@Header(name = "Bundle-Activator", value = "${@class}"), @Header(name = "Bundle-ActivationPolicy", value = "lazy")})
/* loaded from: agent.jar:BOOT-INF/lib/log4j-to-slf4j-2.21.1.jar:org/apache/logging/slf4j/Activator.class */
public class Activator extends ProviderActivator {
    public Activator() {
        super(new SLF4JProvider());
    }
}
