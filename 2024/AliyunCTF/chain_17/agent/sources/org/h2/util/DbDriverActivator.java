package org.h2.util;

import org.h2.Driver;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/DbDriverActivator.class */
public class DbDriverActivator implements BundleActivator {
    private static final String DATASOURCE_FACTORY_CLASS = "org.osgi.service.jdbc.DataSourceFactory";

    public void start(BundleContext bundleContext) {
        Driver load = Driver.load();
        try {
            JdbcUtils.loadUserClass(DATASOURCE_FACTORY_CLASS);
            OsgiDataSourceFactory.registerService(bundleContext, load);
        } catch (Exception e) {
        }
    }

    public void stop(BundleContext bundleContext) {
        Driver.unload();
    }
}
