package org.apache.logging.log4j.util;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.logging.log4j.status.StatusLogger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/OsgiServiceLocator.class */
public class OsgiServiceLocator {
    private static final boolean OSGI_AVAILABLE = checkOsgiAvailable();

    private static boolean checkOsgiAvailable() {
        try {
            Class<?> clazz = Class.forName("org.osgi.framework.FrameworkUtil");
            return clazz.getMethod("getBundle", Class.class).invoke(null, OsgiServiceLocator.class) != null;
        } catch (ClassNotFoundException | LinkageError | NoSuchMethodException e) {
            return false;
        } catch (Throwable e2) {
            LowLevelLogUtil.logException("Unknown error checking OSGI environment.", e2);
            return false;
        }
    }

    public static boolean isAvailable() {
        return OSGI_AVAILABLE;
    }

    public static <T> Stream<T> loadServices(final Class<T> serviceType, final MethodHandles.Lookup lookup) {
        return loadServices(serviceType, lookup, true);
    }

    public static <T> Stream<T> loadServices(final Class<T> serviceType, final MethodHandles.Lookup lookup, final boolean verbose) {
        Class<?> lookupClass = ((MethodHandles.Lookup) Objects.requireNonNull(lookup, "lookup")).lookupClass();
        Bundle bundle = FrameworkUtil.getBundle((Class) Objects.requireNonNull(lookupClass, "lookupClass"));
        if (bundle != null) {
            BundleContext ctx = bundle.getBundleContext();
            if (ctx == null) {
                if (verbose) {
                    StatusLogger.getLogger().error("Unable to load OSGI services: The bundle has no valid BundleContext for serviceType = {}, lookup = {}, lookupClass = {}, bundle = {}", serviceType, lookup, lookupClass, bundle);
                }
            } else {
                try {
                    Stream stream = ctx.getServiceReferences(serviceType, (String) null).stream();
                    Objects.requireNonNull(ctx);
                    return stream.map(ctx::getService);
                } catch (Throwable e) {
                    if (verbose) {
                        StatusLogger.getLogger().error("Unable to load OSGI services for service {}", serviceType, e);
                    }
                }
            }
        }
        return Stream.empty();
    }
}
