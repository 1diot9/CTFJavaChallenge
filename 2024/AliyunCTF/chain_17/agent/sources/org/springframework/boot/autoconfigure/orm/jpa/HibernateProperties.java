package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@ConfigurationProperties("spring.jpa.hibernate")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateProperties.class */
public class HibernateProperties {
    private static final String DISABLED_SCANNER_CLASS = "org.hibernate.boot.archive.scan.internal.DisabledScanner";
    private final Naming naming = new Naming();
    private String ddlAuto;

    public String getDdlAuto() {
        return this.ddlAuto;
    }

    public void setDdlAuto(String ddlAuto) {
        this.ddlAuto = ddlAuto;
    }

    public Naming getNaming() {
        return this.naming;
    }

    public Map<String, Object> determineHibernateProperties(Map<String, String> jpaProperties, HibernateSettings settings) {
        Assert.notNull(jpaProperties, "JpaProperties must not be null");
        Assert.notNull(settings, "Settings must not be null");
        return getAdditionalProperties(jpaProperties, settings);
    }

    private Map<String, Object> getAdditionalProperties(Map<String, String> existing, HibernateSettings settings) {
        Map<String, Object> result = new HashMap<>(existing);
        applyScanner(result);
        getNaming().applyNamingStrategies(result);
        Objects.requireNonNull(settings);
        String ddlAuto = determineDdlAuto(existing, settings::getDdlAuto);
        if (StringUtils.hasText(ddlAuto) && !"none".equals(ddlAuto)) {
            result.put("hibernate.hbm2ddl.auto", ddlAuto);
        } else {
            result.remove("hibernate.hbm2ddl.auto");
        }
        Collection<HibernatePropertiesCustomizer> customizers = settings.getHibernatePropertiesCustomizers();
        if (!ObjectUtils.isEmpty(customizers)) {
            customizers.forEach(customizer -> {
                customizer.customize(result);
            });
        }
        return result;
    }

    private void applyScanner(Map<String, Object> result) {
        if (!result.containsKey("hibernate.archive.scanner") && ClassUtils.isPresent(DISABLED_SCANNER_CLASS, null)) {
            result.put("hibernate.archive.scanner", DISABLED_SCANNER_CLASS);
        }
    }

    private String determineDdlAuto(Map<String, String> existing, Supplier<String> defaultDdlAuto) {
        String ddlAuto = existing.get("hibernate.hbm2ddl.auto");
        if (ddlAuto != null) {
            return ddlAuto;
        }
        if (this.ddlAuto != null) {
            return this.ddlAuto;
        }
        if (existing.get("jakarta.persistence.schema-generation.database.action") != null) {
            return null;
        }
        return defaultDdlAuto.get();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/orm/jpa/HibernateProperties$Naming.class */
    public static class Naming {
        private String implicitStrategy;
        private String physicalStrategy;

        public String getImplicitStrategy() {
            return this.implicitStrategy;
        }

        public void setImplicitStrategy(String implicitStrategy) {
            this.implicitStrategy = implicitStrategy;
        }

        public String getPhysicalStrategy() {
            return this.physicalStrategy;
        }

        public void setPhysicalStrategy(String physicalStrategy) {
            this.physicalStrategy = physicalStrategy;
        }

        private void applyNamingStrategies(Map<String, Object> properties) {
            applyNamingStrategy(properties, "hibernate.implicit_naming_strategy", this.implicitStrategy, () -> {
                return SpringImplicitNamingStrategy.class.getName();
            });
            applyNamingStrategy(properties, "hibernate.physical_naming_strategy", this.physicalStrategy, () -> {
                return CamelCaseToUnderscoresNamingStrategy.class.getName();
            });
        }

        private void applyNamingStrategy(Map<String, Object> properties, String key, Object strategy, Supplier<String> defaultStrategy) {
            if (strategy != null) {
                properties.put(key, strategy);
            } else {
                properties.computeIfAbsent(key, k -> {
                    return defaultStrategy.get();
                });
            }
        }
    }
}
