package org.springframework.boot.jdbc.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/init/PlatformPlaceholderDatabaseDriverResolver.class */
public class PlatformPlaceholderDatabaseDriverResolver {
    private final String placeholder;
    private final Map<DatabaseDriver, String> driverMappings;

    public PlatformPlaceholderDatabaseDriverResolver() {
        this("@@platform@@");
    }

    public PlatformPlaceholderDatabaseDriverResolver(String placeholder) {
        this(placeholder, Collections.emptyMap());
    }

    private PlatformPlaceholderDatabaseDriverResolver(String placeholder, Map<DatabaseDriver, String> driverMappings) {
        this.placeholder = placeholder;
        this.driverMappings = driverMappings;
    }

    public PlatformPlaceholderDatabaseDriverResolver withDriverPlatform(DatabaseDriver driver, String platform) {
        Map<DatabaseDriver, String> driverMappings = new LinkedHashMap<>(this.driverMappings);
        driverMappings.put(driver, platform);
        return new PlatformPlaceholderDatabaseDriverResolver(this.placeholder, driverMappings);
    }

    public List<String> resolveAll(DataSource dataSource, String... values) {
        Assert.notNull(dataSource, "DataSource must not be null");
        return resolveAll(() -> {
            return determinePlatform(dataSource);
        }, values);
    }

    public List<String> resolveAll(String platform, String... values) {
        Assert.notNull(platform, "Platform must not be null");
        return resolveAll(() -> {
            return platform;
        }, values);
    }

    private List<String> resolveAll(Supplier<String> platformProvider, String... values) {
        if (ObjectUtils.isEmpty((Object[]) values)) {
            return Collections.emptyList();
        }
        List<String> resolved = new ArrayList<>(values.length);
        String platform = null;
        for (String value : values) {
            if (StringUtils.hasLength(value) && value.contains(this.placeholder)) {
                platform = platform != null ? platform : platformProvider.get();
                value = value.replace(this.placeholder, platform);
            }
            resolved.add(value);
        }
        return Collections.unmodifiableList(resolved);
    }

    private String determinePlatform(DataSource dataSource) {
        DatabaseDriver databaseDriver = getDatabaseDriver(dataSource);
        Assert.state(databaseDriver != DatabaseDriver.UNKNOWN, "Unable to detect database type");
        return this.driverMappings.getOrDefault(databaseDriver, databaseDriver.getId());
    }

    DatabaseDriver getDatabaseDriver(DataSource dataSource) {
        try {
            String productName = JdbcUtils.commonDatabaseName((String) JdbcUtils.extractDatabaseMetaData(dataSource, (v0) -> {
                return v0.getDatabaseProductName();
            }));
            return DatabaseDriver.fromProductName(productName);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to determine DatabaseDriver", ex);
        }
    }
}
