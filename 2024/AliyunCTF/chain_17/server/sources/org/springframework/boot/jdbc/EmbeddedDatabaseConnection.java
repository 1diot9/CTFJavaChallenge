package org.springframework.boot.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/EmbeddedDatabaseConnection.class */
public enum EmbeddedDatabaseConnection {
    NONE(null),
    H2("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"),
    DERBY("jdbc:derby:memory:%s;create=true"),
    HSQLDB("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:%s");

    private final String alternativeDriverClass;
    private final String url;

    EmbeddedDatabaseConnection(String url) {
        this(null, url);
    }

    EmbeddedDatabaseConnection(String fallbackDriverClass, String url) {
        this.alternativeDriverClass = fallbackDriverClass;
        this.url = url;
    }

    public String getDriverClassName() {
        switch (this) {
            case NONE:
                return null;
            case H2:
                return DatabaseDriver.H2.getDriverClassName();
            case DERBY:
                return DatabaseDriver.DERBY.getDriverClassName();
            case HSQLDB:
                return DatabaseDriver.HSQLDB.getDriverClassName();
            default:
                throw new IncompatibleClassChangeError();
        }
    }

    public EmbeddedDatabaseType getType() {
        switch (this) {
            case NONE:
                return null;
            case H2:
                return EmbeddedDatabaseType.H2;
            case DERBY:
                return EmbeddedDatabaseType.DERBY;
            case HSQLDB:
                return EmbeddedDatabaseType.HSQL;
            default:
                throw new IncompatibleClassChangeError();
        }
    }

    public String getUrl(String databaseName) {
        Assert.hasText(databaseName, "DatabaseName must not be empty");
        if (this.url != null) {
            return String.format(this.url, databaseName);
        }
        return null;
    }

    boolean isEmbeddedUrl(String url) {
        switch (this) {
            case NONE:
                return false;
            case H2:
                return url.contains(":h2:mem");
            case DERBY:
                return true;
            case HSQLDB:
                return url.contains(":hsqldb:mem:");
            default:
                throw new IncompatibleClassChangeError();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isDriverCompatible(String driverClass) {
        return driverClass != null && (driverClass.equals(getDriverClassName()) || driverClass.equals(this.alternativeDriverClass));
    }

    public static boolean isEmbedded(String driverClass, String url) {
        EmbeddedDatabaseConnection connection;
        if (driverClass == null || (connection = getEmbeddedDatabaseConnection(driverClass)) == NONE) {
            return false;
        }
        return url == null || connection.isEmbeddedUrl(url);
    }

    private static EmbeddedDatabaseConnection getEmbeddedDatabaseConnection(String driverClass) {
        return (EmbeddedDatabaseConnection) Stream.of((Object[]) new EmbeddedDatabaseConnection[]{H2, HSQLDB, DERBY}).filter(connection -> {
            return connection.isDriverCompatible(driverClass);
        }).findFirst().orElse(NONE);
    }

    public static boolean isEmbedded(DataSource dataSource) {
        try {
            return ((Boolean) new JdbcTemplate(dataSource).execute(new IsEmbedded())).booleanValue();
        } catch (DataAccessException e) {
            return false;
        }
    }

    public static EmbeddedDatabaseConnection get(ClassLoader classLoader) {
        for (EmbeddedDatabaseConnection candidate : values()) {
            if (candidate != NONE && ClassUtils.isPresent(candidate.getDriverClassName(), classLoader)) {
                return candidate;
            }
        }
        return NONE;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/EmbeddedDatabaseConnection$IsEmbedded.class */
    private static final class IsEmbedded implements ConnectionCallback<Boolean> {
        private IsEmbedded() {
        }

        /* renamed from: doInConnection, reason: merged with bridge method [inline-methods] */
        public Boolean m2201doInConnection(Connection connection) throws SQLException, DataAccessException {
            DatabaseMetaData metaData = connection.getMetaData();
            String productName = metaData.getDatabaseProductName();
            if (productName == null) {
                return false;
            }
            String productName2 = productName.toUpperCase(Locale.ENGLISH);
            EmbeddedDatabaseConnection[] candidates = EmbeddedDatabaseConnection.values();
            for (EmbeddedDatabaseConnection candidate : candidates) {
                if (candidate != EmbeddedDatabaseConnection.NONE && productName2.contains(candidate.getType().name())) {
                    String url = metaData.getURL();
                    return Boolean.valueOf(url == null || candidate.isEmbeddedUrl(url));
                }
            }
            return false;
        }
    }
}
