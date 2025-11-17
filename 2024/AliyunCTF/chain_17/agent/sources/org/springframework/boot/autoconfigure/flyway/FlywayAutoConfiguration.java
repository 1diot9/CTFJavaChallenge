package org.springframework.boot.autoconfigure.flyway;

import cn.hutool.core.text.StrPool;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.migration.JavaMigration;
import org.flywaydb.core.extensibility.ConfigurationExtension;
import org.flywaydb.core.internal.database.postgresql.PostgreSQLConfigurationExtension;
import org.flywaydb.database.oracle.OracleConfigurationExtension;
import org.flywaydb.database.sqlserver.SQLServerConfigurationExtension;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.function.SingletonSupplier;

@AutoConfiguration(after = {DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ConditionalOnClass({Flyway.class})
@ImportRuntimeHints({FlywayAutoConfigurationRuntimeHints.class})
@Conditional({FlywayDataSourceCondition.class})
@ConditionalOnProperty(prefix = "spring.flyway", name = {"enabled"}, matchIfMissing = true)
@Import({DatabaseInitializationDependencyConfigurer.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration.class */
public class FlywayAutoConfiguration {
    @ConfigurationPropertiesBinding
    @Bean
    public StringOrNumberToMigrationVersionConverter stringOrNumberMigrationVersionConverter() {
        return new StringOrNumberToMigrationVersionConverter();
    }

    @Bean
    public FlywaySchemaManagementProvider flywayDefaultDdlModeProvider(ObjectProvider<Flyway> flyways) {
        return new FlywaySchemaManagementProvider(flyways);
    }

    @EnableConfigurationProperties({FlywayProperties.class})
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({JdbcUtils.class})
    @ConditionalOnMissingBean({Flyway.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayConfiguration.class */
    public static class FlywayConfiguration {
        private final FlywayProperties properties;

        FlywayConfiguration(FlywayProperties properties) {
            this.properties = properties;
        }

        @Bean
        ResourceProviderCustomizer resourceProviderCustomizer() {
            return new ResourceProviderCustomizer();
        }

        @ConditionalOnMissingBean({FlywayConnectionDetails.class})
        @Bean
        PropertiesFlywayConnectionDetails flywayConnectionDetails() {
            return new PropertiesFlywayConnectionDetails(this.properties);
        }

        @ConditionalOnClass(name = {"org.flywaydb.database.sqlserver.SQLServerConfigurationExtension"})
        @Bean
        SqlServerFlywayConfigurationCustomizer sqlServerFlywayConfigurationCustomizer() {
            return new SqlServerFlywayConfigurationCustomizer(this.properties);
        }

        @ConditionalOnClass(name = {"org.flywaydb.database.oracle.OracleConfigurationExtension"})
        @Bean
        OracleFlywayConfigurationCustomizer oracleFlywayConfigurationCustomizer() {
            return new OracleFlywayConfigurationCustomizer(this.properties);
        }

        @ConditionalOnClass(name = {"org.flywaydb.core.internal.database.postgresql.PostgreSQLConfigurationExtension"})
        @Bean
        PostgresqlFlywayConfigurationCustomizer postgresqlFlywayConfigurationCustomizer() {
            return new PostgresqlFlywayConfigurationCustomizer(this.properties);
        }

        @Bean
        Flyway flyway(FlywayConnectionDetails connectionDetails, ResourceLoader resourceLoader, ObjectProvider<DataSource> dataSource, @FlywayDataSource ObjectProvider<DataSource> flywayDataSource, ObjectProvider<FlywayConfigurationCustomizer> fluentConfigurationCustomizers, ObjectProvider<JavaMigration> javaMigrations, ObjectProvider<Callback> callbacks, ResourceProviderCustomizer resourceProviderCustomizer) {
            FluentConfiguration configuration = new FluentConfiguration(resourceLoader.getClassLoader());
            configureDataSource(configuration, flywayDataSource.getIfAvailable(), dataSource.getIfUnique(), connectionDetails);
            configureProperties(configuration, this.properties);
            configureCallbacks(configuration, callbacks.orderedStream().toList());
            configureJavaMigrations(configuration, javaMigrations.orderedStream().toList());
            fluentConfigurationCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(configuration);
            });
            resourceProviderCustomizer.customize(configuration);
            return configuration.load();
        }

        private void configureDataSource(FluentConfiguration configuration, DataSource flywayDataSource, DataSource dataSource, FlywayConnectionDetails connectionDetails) {
            DataSource migrationDataSource = getMigrationDataSource(flywayDataSource, dataSource, connectionDetails);
            configuration.dataSource(migrationDataSource);
        }

        /* JADX WARN: Type inference failed for: r0v18, types: [javax.sql.DataSource] */
        /* JADX WARN: Type inference failed for: r0v25, types: [javax.sql.DataSource] */
        private DataSource getMigrationDataSource(DataSource flywayDataSource, DataSource dataSource, FlywayConnectionDetails connectionDetails) {
            if (flywayDataSource != null) {
                return flywayDataSource;
            }
            String url = connectionDetails.getJdbcUrl();
            if (url != null) {
                DataSourceBuilder<?> builder = DataSourceBuilder.create().type(SimpleDriverDataSource.class);
                builder.url(url);
                applyConnectionDetails(connectionDetails, builder);
                return builder.build();
            }
            String user = connectionDetails.getUsername();
            if (user != null && dataSource != null) {
                DataSourceBuilder<?> builder2 = DataSourceBuilder.derivedFrom(dataSource).type(SimpleDriverDataSource.class);
                applyConnectionDetails(connectionDetails, builder2);
                return builder2.build();
            }
            Assert.state(dataSource != null, "Flyway migration DataSource missing");
            return dataSource;
        }

        private void applyConnectionDetails(FlywayConnectionDetails connectionDetails, DataSourceBuilder<?> builder) {
            builder.username(connectionDetails.getUsername());
            builder.password(connectionDetails.getPassword());
            String driverClassName = connectionDetails.getDriverClassName();
            if (StringUtils.hasText(driverClassName)) {
                builder.driverClassName(driverClassName);
            }
        }

        private void configureProperties(FluentConfiguration configuration, FlywayProperties properties) {
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            String[] locations = (String[]) new LocationResolver(configuration.getDataSource()).resolveLocations(properties.getLocations()).toArray(new String[0]);
            configuration.locations(locations);
            map.from((PropertyMapper) Boolean.valueOf(properties.isFailOnMissingLocations())).to(failOnMissingLocations -> {
                configuration.failOnMissingLocations(failOnMissingLocations.booleanValue());
            });
            map.from((PropertyMapper) properties.getEncoding()).to(encoding -> {
                configuration.encoding(encoding);
            });
            map.from((PropertyMapper) Integer.valueOf(properties.getConnectRetries())).to(connectRetries -> {
                configuration.connectRetries(connectRetries.intValue());
            });
            map.from((PropertyMapper) properties.getConnectRetriesInterval()).as((v0) -> {
                return v0.getSeconds();
            }).as((v0) -> {
                return v0.intValue();
            }).to(connectRetriesInterval -> {
                configuration.connectRetriesInterval(connectRetriesInterval.intValue());
            });
            map.from((PropertyMapper) Integer.valueOf(properties.getLockRetryCount())).to(lockRetryCount -> {
                configuration.lockRetryCount(lockRetryCount.intValue());
            });
            map.from((PropertyMapper) properties.getDefaultSchema()).to(schema -> {
                configuration.defaultSchema(schema);
            });
            map.from((PropertyMapper) properties.getSchemas()).as((v0) -> {
                return StringUtils.toStringArray(v0);
            }).to(schemas -> {
                configuration.schemas(schemas);
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isCreateSchemas())).to(createSchemas -> {
                configuration.createSchemas(createSchemas.booleanValue());
            });
            map.from((PropertyMapper) properties.getTable()).to(table -> {
                configuration.table(table);
            });
            map.from((PropertyMapper) properties.getTablespace()).to(tablespace -> {
                configuration.tablespace(tablespace);
            });
            map.from((PropertyMapper) properties.getBaselineDescription()).to(baselineDescription -> {
                configuration.baselineDescription(baselineDescription);
            });
            map.from((PropertyMapper) properties.getBaselineVersion()).to(baselineVersion -> {
                configuration.baselineVersion(baselineVersion);
            });
            map.from((PropertyMapper) properties.getInstalledBy()).to(installedBy -> {
                configuration.installedBy(installedBy);
            });
            map.from((PropertyMapper) properties.getPlaceholders()).to(placeholders -> {
                configuration.placeholders(placeholders);
            });
            map.from((PropertyMapper) properties.getPlaceholderPrefix()).to(placeholderPrefix -> {
                configuration.placeholderPrefix(placeholderPrefix);
            });
            map.from((PropertyMapper) properties.getPlaceholderSuffix()).to(placeholderSuffix -> {
                configuration.placeholderSuffix(placeholderSuffix);
            });
            map.from((PropertyMapper) properties.getPlaceholderSeparator()).to(placeHolderSeparator -> {
                configuration.placeholderSeparator(placeHolderSeparator);
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isPlaceholderReplacement())).to(placeholderReplacement -> {
                configuration.placeholderReplacement(placeholderReplacement.booleanValue());
            });
            map.from((PropertyMapper) properties.getSqlMigrationPrefix()).to(sqlMigrationPrefix -> {
                configuration.sqlMigrationPrefix(sqlMigrationPrefix);
            });
            map.from((PropertyMapper) properties.getSqlMigrationSuffixes()).as((v0) -> {
                return StringUtils.toStringArray(v0);
            }).to(sqlMigrationSuffixes -> {
                configuration.sqlMigrationSuffixes(sqlMigrationSuffixes);
            });
            map.from((PropertyMapper) properties.getSqlMigrationSeparator()).to(sqlMigrationSeparator -> {
                configuration.sqlMigrationSeparator(sqlMigrationSeparator);
            });
            map.from((PropertyMapper) properties.getRepeatableSqlMigrationPrefix()).to(repeatableSqlMigrationPrefix -> {
                configuration.repeatableSqlMigrationPrefix(repeatableSqlMigrationPrefix);
            });
            map.from((PropertyMapper) properties.getTarget()).to(target -> {
                configuration.target(target);
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isBaselineOnMigrate())).to(baselineOnMigrate -> {
                configuration.baselineOnMigrate(baselineOnMigrate.booleanValue());
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isCleanDisabled())).to(cleanDisabled -> {
                configuration.cleanDisabled(cleanDisabled.booleanValue());
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isCleanOnValidationError())).to(cleanOnValidationError -> {
                configuration.cleanOnValidationError(cleanOnValidationError.booleanValue());
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isGroup())).to(group -> {
                configuration.group(group.booleanValue());
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isMixed())).to(mixed -> {
                configuration.mixed(mixed.booleanValue());
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isOutOfOrder())).to(outOfOrder -> {
                configuration.outOfOrder(outOfOrder.booleanValue());
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isSkipDefaultCallbacks())).to(skipDefaultCallbacks -> {
                configuration.skipDefaultCallbacks(skipDefaultCallbacks.booleanValue());
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isSkipDefaultResolvers())).to(skipDefaultResolvers -> {
                configuration.skipDefaultResolvers(skipDefaultResolvers.booleanValue());
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isValidateMigrationNaming())).to(validateMigrationNaming -> {
                configuration.validateMigrationNaming(validateMigrationNaming.booleanValue());
            });
            map.from((PropertyMapper) Boolean.valueOf(properties.isValidateOnMigrate())).to(validateOnMigrate -> {
                configuration.validateOnMigrate(validateOnMigrate.booleanValue());
            });
            map.from((PropertyMapper) properties.getInitSqls()).whenNot((v0) -> {
                return CollectionUtils.isEmpty(v0);
            }).as(initSqls -> {
                return StringUtils.collectionToDelimitedString(initSqls, StrPool.LF);
            }).to(initSql -> {
                configuration.initSql(initSql);
            });
            map.from((PropertyMapper) properties.getScriptPlaceholderPrefix()).to(prefix -> {
                configuration.scriptPlaceholderPrefix(prefix);
            });
            map.from((PropertyMapper) properties.getScriptPlaceholderSuffix()).to(suffix -> {
                configuration.scriptPlaceholderSuffix(suffix);
            });
            configureExecuteInTransaction(configuration, properties, map);
            Objects.requireNonNull(properties);
            map.from(properties::getLoggers).to(loggers -> {
                configuration.loggers(loggers);
            });
            map.from((PropertyMapper) properties.getBatch()).to(batch -> {
                configuration.batch(batch.booleanValue());
            });
            map.from((PropertyMapper) properties.getDryRunOutput()).to(dryRunOutput -> {
                configuration.dryRunOutput(dryRunOutput);
            });
            map.from((PropertyMapper) properties.getErrorOverrides()).to(errorOverrides -> {
                configuration.errorOverrides(errorOverrides);
            });
            map.from((PropertyMapper) properties.getLicenseKey()).to(licenseKey -> {
                configuration.licenseKey(licenseKey);
            });
            map.from((PropertyMapper) properties.getStream()).to(stream -> {
                configuration.stream(stream.booleanValue());
            });
            map.from((PropertyMapper) properties.getUndoSqlMigrationPrefix()).to(undoSqlMigrationPrefix -> {
                configuration.undoSqlMigrationPrefix(undoSqlMigrationPrefix);
            });
            map.from((PropertyMapper) properties.getCherryPick()).to(cherryPick -> {
                configuration.cherryPick(cherryPick);
            });
            map.from((PropertyMapper) properties.getJdbcProperties()).whenNot((v0) -> {
                return v0.isEmpty();
            }).to(jdbcProperties -> {
                configuration.jdbcProperties(jdbcProperties);
            });
            map.from((PropertyMapper) properties.getKerberosConfigFile()).to(configFile -> {
                configuration.kerberosConfigFile(configFile);
            });
            map.from((PropertyMapper) properties.getOutputQueryResults()).to(outputQueryResults -> {
                configuration.outputQueryResults(outputQueryResults.booleanValue());
            });
            map.from((PropertyMapper) properties.getSkipExecutingMigrations()).to(skipExecutingMigrations -> {
                configuration.skipExecutingMigrations(skipExecutingMigrations.booleanValue());
            });
            map.from((PropertyMapper) properties.getIgnoreMigrationPatterns()).whenNot((v0) -> {
                return v0.isEmpty();
            }).to(ignoreMigrationPatterns -> {
                configuration.ignoreMigrationPatterns((String[]) ignoreMigrationPatterns.toArray(new String[0]));
            });
            map.from((PropertyMapper) properties.getDetectEncoding()).to(detectEncoding -> {
                configuration.detectEncoding(detectEncoding.booleanValue());
            });
        }

        private void configureExecuteInTransaction(FluentConfiguration configuration, FlywayProperties properties, PropertyMapper map) {
            try {
                PropertyMapper.Source from = map.from((PropertyMapper) Boolean.valueOf(properties.isExecuteInTransaction()));
                Objects.requireNonNull(configuration);
                from.to((v1) -> {
                    r1.executeInTransaction(v1);
                });
            } catch (NoSuchMethodError e) {
            }
        }

        private void configureCallbacks(FluentConfiguration configuration, List<Callback> callbacks) {
            if (!callbacks.isEmpty()) {
                configuration.callbacks((Callback[]) callbacks.toArray(new Callback[0]));
            }
        }

        private void configureJavaMigrations(FluentConfiguration flyway, List<JavaMigration> migrations) {
            if (!migrations.isEmpty()) {
                flyway.javaMigrations((JavaMigration[]) migrations.toArray(new JavaMigration[0]));
            }
        }

        @ConditionalOnMissingBean
        @Bean
        public FlywayMigrationInitializer flywayInitializer(Flyway flyway, ObjectProvider<FlywayMigrationStrategy> migrationStrategy) {
            return new FlywayMigrationInitializer(flyway, migrationStrategy.getIfAvailable());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$LocationResolver.class */
    public static class LocationResolver {
        private static final String VENDOR_PLACEHOLDER = "{vendor}";
        private final DataSource dataSource;

        LocationResolver(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        List<String> resolveLocations(List<String> locations) {
            if (usesVendorLocation(locations)) {
                DatabaseDriver databaseDriver = getDatabaseDriver();
                return replaceVendorLocations(locations, databaseDriver);
            }
            return locations;
        }

        private List<String> replaceVendorLocations(List<String> locations, DatabaseDriver databaseDriver) {
            if (databaseDriver == DatabaseDriver.UNKNOWN) {
                return locations;
            }
            String vendor = databaseDriver.getId();
            return locations.stream().map(location -> {
                return location.replace(VENDOR_PLACEHOLDER, vendor);
            }).toList();
        }

        private DatabaseDriver getDatabaseDriver() {
            try {
                String url = (String) JdbcUtils.extractDatabaseMetaData(this.dataSource, (v0) -> {
                    return v0.getURL();
                });
                return DatabaseDriver.fromJdbcUrl(url);
            } catch (MetaDataAccessException ex) {
                throw new IllegalStateException((Throwable) ex);
            }
        }

        private boolean usesVendorLocation(Collection<String> locations) {
            for (String location : locations) {
                if (location.contains(VENDOR_PLACEHOLDER)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$StringOrNumberToMigrationVersionConverter.class */
    static class StringOrNumberToMigrationVersionConverter implements GenericConverter {
        private static final Set<GenericConverter.ConvertiblePair> CONVERTIBLE_TYPES;

        StringOrNumberToMigrationVersionConverter() {
        }

        static {
            Set<GenericConverter.ConvertiblePair> types = new HashSet<>(2);
            types.add(new GenericConverter.ConvertiblePair(String.class, MigrationVersion.class));
            types.add(new GenericConverter.ConvertiblePair(Number.class, MigrationVersion.class));
            CONVERTIBLE_TYPES = Collections.unmodifiableSet(types);
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return CONVERTIBLE_TYPES;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            String value = ObjectUtils.nullSafeToString(source);
            return MigrationVersion.fromVersion(value);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayDataSourceCondition.class */
    static final class FlywayDataSourceCondition extends AnyNestedCondition {
        FlywayDataSourceCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean({DataSource.class})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayDataSourceCondition$DataSourceBeanCondition.class */
        private static final class DataSourceBeanCondition {
            private DataSourceBeanCondition() {
            }
        }

        @ConditionalOnBean({JdbcConnectionDetails.class})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayDataSourceCondition$JdbcConnectionDetailsCondition.class */
        private static final class JdbcConnectionDetailsCondition {
            private JdbcConnectionDetailsCondition() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.flyway", name = {"url"})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayDataSourceCondition$FlywayUrlCondition.class */
        private static final class FlywayUrlCondition {
            private FlywayUrlCondition() {
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayAutoConfigurationRuntimeHints.class */
    static class FlywayAutoConfigurationRuntimeHints implements RuntimeHintsRegistrar {
        FlywayAutoConfigurationRuntimeHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("db/migration/*");
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$PropertiesFlywayConnectionDetails.class */
    static final class PropertiesFlywayConnectionDetails implements FlywayConnectionDetails {
        private final FlywayProperties properties;

        PropertiesFlywayConnectionDetails(FlywayProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.autoconfigure.flyway.FlywayConnectionDetails
        public String getUsername() {
            return this.properties.getUser();
        }

        @Override // org.springframework.boot.autoconfigure.flyway.FlywayConnectionDetails
        public String getPassword() {
            return this.properties.getPassword();
        }

        @Override // org.springframework.boot.autoconfigure.flyway.FlywayConnectionDetails
        public String getJdbcUrl() {
            return this.properties.getUrl();
        }

        @Override // org.springframework.boot.autoconfigure.flyway.FlywayConnectionDetails
        public String getDriverClassName() {
            return this.properties.getDriverClassName();
        }
    }

    @Order(Integer.MIN_VALUE)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$OracleFlywayConfigurationCustomizer.class */
    static final class OracleFlywayConfigurationCustomizer implements FlywayConfigurationCustomizer {
        private final FlywayProperties properties;

        OracleFlywayConfigurationCustomizer(FlywayProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer
        public void customize(FluentConfiguration configuration) {
            Extension<OracleConfigurationExtension> extension = new Extension<>(configuration, OracleConfigurationExtension.class, "Oracle");
            FlywayProperties.Oracle properties = this.properties.getOracle();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(properties);
            map.from(properties::getSqlplus).to(extension.via((ext, sqlplus) -> {
                ext.setSqlplus(sqlplus);
            }));
            Objects.requireNonNull(properties);
            map.from(properties::getSqlplusWarn).to(extension.via((ext2, sqlplusWarn) -> {
                ext2.setSqlplusWarn(sqlplusWarn);
            }));
            Objects.requireNonNull(properties);
            map.from(properties::getWalletLocation).to(extension.via((ext3, walletLocation) -> {
                ext3.setWalletLocation(walletLocation);
            }));
            Objects.requireNonNull(properties);
            map.from(properties::getKerberosCacheFile).to(extension.via((ext4, kerberosCacheFile) -> {
                ext4.setKerberosCacheFile(kerberosCacheFile);
            }));
        }
    }

    @Order(Integer.MIN_VALUE)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$PostgresqlFlywayConfigurationCustomizer.class */
    static final class PostgresqlFlywayConfigurationCustomizer implements FlywayConfigurationCustomizer {
        private final FlywayProperties properties;

        PostgresqlFlywayConfigurationCustomizer(FlywayProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer
        public void customize(FluentConfiguration configuration) {
            Extension<PostgreSQLConfigurationExtension> extension = new Extension<>(configuration, PostgreSQLConfigurationExtension.class, "PostgreSQL");
            FlywayProperties.Postgresql properties = this.properties.getPostgresql();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(properties);
            map.from(properties::getTransactionalLock).to(extension.via((ext, transactionalLock) -> {
                ext.setTransactionalLock(transactionalLock.booleanValue());
            }));
        }
    }

    @Order(Integer.MIN_VALUE)
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$SqlServerFlywayConfigurationCustomizer.class */
    static final class SqlServerFlywayConfigurationCustomizer implements FlywayConfigurationCustomizer {
        private final FlywayProperties properties;

        SqlServerFlywayConfigurationCustomizer(FlywayProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer
        public void customize(FluentConfiguration configuration) {
            Extension<SQLServerConfigurationExtension> extension = new Extension<>(configuration, SQLServerConfigurationExtension.class, "SQL Server");
            FlywayProperties.Sqlserver properties = this.properties.getSqlserver();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(properties);
            map.from(properties::getKerberosLoginFile).to(extension.via(this::setKerberosLoginFile));
        }

        private void setKerberosLoginFile(SQLServerConfigurationExtension configuration, String file) {
            configuration.getKerberos().getLogin().setFile(file);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$Extension.class */
    static class Extension<E extends ConfigurationExtension> {
        private SingletonSupplier<E> extension;

        Extension(FluentConfiguration configuration, Class<E> type, String name) {
            this.extension = SingletonSupplier.of(() -> {
                ConfigurationExtension plugin = configuration.getPluginRegister().getPlugin(type);
                Assert.notNull(plugin, (Supplier<String>) () -> {
                    return "Flyway %s extension missing".formatted(name);
                });
                return plugin;
            });
        }

        <T> Consumer<T> via(BiConsumer<E, T> action) {
            return value -> {
                action.accept(this.extension.get(), value);
            };
        }
    }
}
