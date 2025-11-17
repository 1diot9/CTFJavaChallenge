package org.springframework.boot.autoconfigure.jdbc;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/PropertiesJdbcConnectionDetails.class */
final class PropertiesJdbcConnectionDetails implements JdbcConnectionDetails {
    private final DataSourceProperties properties;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PropertiesJdbcConnectionDetails(DataSourceProperties properties) {
        this.properties = properties;
    }

    @Override // org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails
    public String getUsername() {
        return this.properties.determineUsername();
    }

    @Override // org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails
    public String getPassword() {
        return this.properties.determinePassword();
    }

    @Override // org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails
    public String getJdbcUrl() {
        return this.properties.determineUrl();
    }

    @Override // org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails
    public String getDriverClassName() {
        return this.properties.determineDriverClassName();
    }

    @Override // org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails
    public String getXaDataSourceClassName() {
        if (this.properties.getXa().getDataSourceClassName() != null) {
            return this.properties.getXa().getDataSourceClassName();
        }
        return super.getXaDataSourceClassName();
    }
}
