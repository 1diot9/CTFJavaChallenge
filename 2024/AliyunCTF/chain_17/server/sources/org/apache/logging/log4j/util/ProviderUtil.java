package org.apache.logging.log4j.util;

import aQute.bnd.annotation.spi.ServiceConsumer;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

@ServiceConsumer(value = Provider.class, resolution = "optional", cardinality = "multiple")
/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/ProviderUtil.class */
public final class ProviderUtil {
    protected static final String PROVIDER_RESOURCE = "META-INF/log4j-provider.properties";
    private static final String API_VERSION = "Log4jAPIVersion";
    private static volatile ProviderUtil instance;
    protected static final Collection<Provider> PROVIDERS = new HashSet();
    protected static final Lock STARTUP_LOCK = new ReentrantLock();
    private static final String[] COMPATIBLE_API_VERSIONS = {"2.6.0"};
    private static final Logger LOGGER = StatusLogger.getLogger();

    private ProviderUtil() {
        Stream filter = ServiceLoaderUtil.loadServices(Provider.class, MethodHandles.lookup(), false).filter(provider -> {
            return validVersion(provider.getVersions());
        });
        Collection<Provider> collection = PROVIDERS;
        Objects.requireNonNull(collection);
        filter.forEach((v1) -> {
            r1.add(v1);
        });
        for (LoaderUtil.UrlResource resource : LoaderUtil.findUrlResources(PROVIDER_RESOURCE, false)) {
            loadProvider(resource.getUrl(), resource.getClassLoader());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void addProvider(final Provider provider) {
        PROVIDERS.add(provider);
        LOGGER.debug("Loaded Provider {}", provider);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void loadProvider(final URL url, final ClassLoader cl) {
        try {
            Properties props = PropertiesUtil.loadClose(url.openStream(), url);
            if (validVersion(props.getProperty(API_VERSION))) {
                Provider provider = new Provider(props, url, cl);
                PROVIDERS.add(provider);
                LOGGER.debug("Loaded Provider {}", provider);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to open {}", url, e);
        }
    }

    protected static void loadProviders(final ClassLoader classLoader) {
        Stream filter = ServiceLoaderUtil.loadClassloaderServices(Provider.class, MethodHandles.lookup(), classLoader, true).filter(provider -> {
            return validVersion(provider.getVersions());
        });
        Collection<Provider> collection = PROVIDERS;
        Objects.requireNonNull(collection);
        filter.forEach((v1) -> {
            r1.add(v1);
        });
    }

    @Deprecated
    protected static void loadProviders(final Enumeration<URL> urls, final ClassLoader cl) {
        if (urls != null) {
            while (urls.hasMoreElements()) {
                loadProvider(urls.nextElement(), cl);
            }
        }
    }

    public static Iterable<Provider> getProviders() {
        lazyInit();
        return PROVIDERS;
    }

    public static boolean hasProviders() {
        lazyInit();
        return !PROVIDERS.isEmpty();
    }

    protected static void lazyInit() {
        if (instance == null) {
            try {
                STARTUP_LOCK.lockInterruptibly();
                try {
                    if (instance == null) {
                        instance = new ProviderUtil();
                    }
                    STARTUP_LOCK.unlock();
                } catch (Throwable th) {
                    STARTUP_LOCK.unlock();
                    throw th;
                }
            } catch (InterruptedException e) {
                LOGGER.fatal("Interrupted before Log4j Providers could be loaded.", (Throwable) e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public static ClassLoader findClassLoader() {
        return LoaderUtil.getThreadContextClassLoader();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean validVersion(final String version) {
        for (String v : COMPATIBLE_API_VERSIONS) {
            if (version.startsWith(v)) {
                return true;
            }
        }
        return false;
    }
}
