package org.apache.catalina.loader;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/loader/JdbcLeakPrevention.class */
public class JdbcLeakPrevention {
    public List<String> clearJdbcDriverRegistrations() throws SQLException {
        List<String> driverNames = new ArrayList<>();
        Set<Driver> originalDrivers = new HashSet<>();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            originalDrivers.add(drivers.nextElement());
        }
        Enumeration<Driver> drivers2 = DriverManager.getDrivers();
        while (drivers2.hasMoreElements()) {
            Driver driver = drivers2.nextElement();
            if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
                if (originalDrivers.contains(driver)) {
                    driverNames.add(driver.getClass().getCanonicalName());
                }
                DriverManager.deregisterDriver(driver);
            }
        }
        return driverNames;
    }
}
