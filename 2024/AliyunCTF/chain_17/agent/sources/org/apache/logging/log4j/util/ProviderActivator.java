package org.apache.logging.log4j.util;

import java.util.Hashtable;
import org.apache.logging.log4j.spi.Provider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/ProviderActivator.class */
public abstract class ProviderActivator implements BundleActivator {
    public static final String API_VERSION = "APIVersion";
    private final Provider provider;
    private ServiceRegistration<Provider> providerRegistration = null;

    /* JADX INFO: Access modifiers changed from: protected */
    public ProviderActivator(final Provider provider) {
        this.provider = provider;
    }

    public void start(final BundleContext context) throws Exception {
        Hashtable<String, String> props = new Hashtable<>();
        props.put(API_VERSION, this.provider.getVersions());
        this.providerRegistration = context.registerService(Provider.class, this.provider, props);
    }

    public void stop(final BundleContext context) throws Exception {
        if (this.providerRegistration != null) {
            this.providerRegistration.unregister();
        }
    }
}
