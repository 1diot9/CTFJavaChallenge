package org.springframework.beans.factory.config;

import java.util.Map;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.core.SpringProperties;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringValueResolver;

@Deprecated
/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/PropertyPlaceholderConfigurer.class */
public class PropertyPlaceholderConfigurer extends PlaceholderConfigurerSupport {
    public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;
    public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;
    public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;
    private static final Map<String, Integer> constants = Map.of("SYSTEM_PROPERTIES_MODE_NEVER", 0, "SYSTEM_PROPERTIES_MODE_FALLBACK", 1, "SYSTEM_PROPERTIES_MODE_OVERRIDE", 2);
    private int systemPropertiesMode = 1;
    private boolean searchSystemEnvironment;

    public PropertyPlaceholderConfigurer() {
        this.searchSystemEnvironment = !SpringProperties.getFlag(AbstractEnvironment.IGNORE_GETENV_PROPERTY_NAME);
    }

    public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException {
        Assert.hasText(constantName, "'constantName' must not be null or blank");
        Integer mode = constants.get(constantName);
        Assert.notNull(mode, "Only system properties mode constants allowed");
        this.systemPropertiesMode = mode.intValue();
    }

    public void setSystemPropertiesMode(int systemPropertiesMode) {
        this.systemPropertiesMode = systemPropertiesMode;
    }

    public void setSearchSystemEnvironment(boolean searchSystemEnvironment) {
        this.searchSystemEnvironment = searchSystemEnvironment;
    }

    @Nullable
    protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
        String propVal = null;
        if (systemPropertiesMode == 2) {
            propVal = resolveSystemProperty(placeholder);
        }
        if (propVal == null) {
            propVal = resolvePlaceholder(placeholder, props);
        }
        if (propVal == null && systemPropertiesMode == 1) {
            propVal = resolveSystemProperty(placeholder);
        }
        return propVal;
    }

    @Nullable
    protected String resolvePlaceholder(String placeholder, Properties props) {
        return props.getProperty(placeholder);
    }

    @Nullable
    protected String resolveSystemProperty(String key) {
        try {
            String value = System.getProperty(key);
            if (value == null && this.searchSystemEnvironment) {
                value = System.getenv(key);
            }
            return value;
        } catch (Throwable ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not access system property '" + key + "': " + ex);
                return null;
            }
            return null;
        }
    }

    @Override // org.springframework.beans.factory.config.PropertyResourceConfigurer
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
        doProcessProperties(beanFactoryToProcess, valueResolver);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/PropertyPlaceholderConfigurer$PlaceholderResolvingStringValueResolver.class */
    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {
        private final PropertyPlaceholderHelper helper;
        private final PropertyPlaceholderHelper.PlaceholderResolver resolver;

        public PlaceholderResolvingStringValueResolver(Properties props) {
            this.helper = new PropertyPlaceholderHelper(PropertyPlaceholderConfigurer.this.placeholderPrefix, PropertyPlaceholderConfigurer.this.placeholderSuffix, PropertyPlaceholderConfigurer.this.valueSeparator, PropertyPlaceholderConfigurer.this.ignoreUnresolvablePlaceholders);
            this.resolver = new PropertyPlaceholderConfigurerResolver(props);
        }

        @Override // org.springframework.util.StringValueResolver
        @Nullable
        public String resolveStringValue(String strVal) throws BeansException {
            String resolved = this.helper.replacePlaceholders(strVal, this.resolver);
            if (PropertyPlaceholderConfigurer.this.trimValues) {
                resolved = resolved.trim();
            }
            if (resolved.equals(PropertyPlaceholderConfigurer.this.nullValue)) {
                return null;
            }
            return resolved;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/PropertyPlaceholderConfigurer$PropertyPlaceholderConfigurerResolver.class */
    private final class PropertyPlaceholderConfigurerResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
        private final Properties props;

        private PropertyPlaceholderConfigurerResolver(Properties props) {
            this.props = props;
        }

        @Override // org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver
        @Nullable
        public String resolvePlaceholder(String placeholderName) {
            return PropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName, this.props, PropertyPlaceholderConfigurer.this.systemPropertiesMode);
        }
    }
}
