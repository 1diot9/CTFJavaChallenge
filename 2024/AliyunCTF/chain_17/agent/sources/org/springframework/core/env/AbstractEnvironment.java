package org.springframework.core.env;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.SpringProperties;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/AbstractEnvironment.class */
public abstract class AbstractEnvironment implements ConfigurableEnvironment {
    public static final String IGNORE_GETENV_PROPERTY_NAME = "spring.getenv.ignore";
    public static final String ACTIVE_PROFILES_PROPERTY_NAME = "spring.profiles.active";
    public static final String DEFAULT_PROFILES_PROPERTY_NAME = "spring.profiles.default";
    public static final String RESERVED_DEFAULT_PROFILE_NAME = "default";
    protected final Log logger;
    private final Set<String> activeProfiles;
    private final Set<String> defaultProfiles;
    private final MutablePropertySources propertySources;
    private final ConfigurablePropertyResolver propertyResolver;

    public AbstractEnvironment() {
        this(new MutablePropertySources());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractEnvironment(MutablePropertySources propertySources) {
        this.logger = LogFactory.getLog(getClass());
        this.activeProfiles = new LinkedHashSet();
        this.defaultProfiles = new LinkedHashSet(getReservedDefaultProfiles());
        this.propertySources = propertySources;
        this.propertyResolver = createPropertyResolver(propertySources);
        customizePropertySources(propertySources);
    }

    protected ConfigurablePropertyResolver createPropertyResolver(MutablePropertySources propertySources) {
        return new PropertySourcesPropertyResolver(propertySources);
    }

    protected final ConfigurablePropertyResolver getPropertyResolver() {
        return this.propertyResolver;
    }

    protected void customizePropertySources(MutablePropertySources propertySources) {
    }

    protected Set<String> getReservedDefaultProfiles() {
        return Collections.singleton("default");
    }

    @Override // org.springframework.core.env.Environment
    public String[] getActiveProfiles() {
        return StringUtils.toStringArray(doGetActiveProfiles());
    }

    protected Set<String> doGetActiveProfiles() {
        Set<String> set;
        synchronized (this.activeProfiles) {
            if (this.activeProfiles.isEmpty()) {
                String profiles = doGetActiveProfilesProperty();
                if (StringUtils.hasText(profiles)) {
                    setActiveProfiles(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(profiles)));
                }
            }
            set = this.activeProfiles;
        }
        return set;
    }

    @Nullable
    protected String doGetActiveProfilesProperty() {
        return getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public void setActiveProfiles(String... profiles) {
        Assert.notNull(profiles, "Profile array must not be null");
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Activating profiles " + Arrays.toString(profiles));
        }
        synchronized (this.activeProfiles) {
            this.activeProfiles.clear();
            for (String profile : profiles) {
                validateProfile(profile);
                this.activeProfiles.add(profile);
            }
        }
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public void addActiveProfile(String profile) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Activating profile '" + profile + "'");
        }
        validateProfile(profile);
        doGetActiveProfiles();
        synchronized (this.activeProfiles) {
            this.activeProfiles.add(profile);
        }
    }

    @Override // org.springframework.core.env.Environment
    public String[] getDefaultProfiles() {
        return StringUtils.toStringArray(doGetDefaultProfiles());
    }

    protected Set<String> doGetDefaultProfiles() {
        Set<String> set;
        synchronized (this.defaultProfiles) {
            if (this.defaultProfiles.equals(getReservedDefaultProfiles())) {
                String profiles = doGetDefaultProfilesProperty();
                if (StringUtils.hasText(profiles)) {
                    setDefaultProfiles(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(profiles)));
                }
            }
            set = this.defaultProfiles;
        }
        return set;
    }

    @Nullable
    protected String doGetDefaultProfilesProperty() {
        return getProperty(DEFAULT_PROFILES_PROPERTY_NAME);
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public void setDefaultProfiles(String... profiles) {
        Assert.notNull(profiles, "Profile array must not be null");
        synchronized (this.defaultProfiles) {
            this.defaultProfiles.clear();
            for (String profile : profiles) {
                validateProfile(profile);
                this.defaultProfiles.add(profile);
            }
        }
    }

    @Override // org.springframework.core.env.Environment
    @Deprecated
    public boolean acceptsProfiles(String... profiles) {
        Assert.notEmpty(profiles, "Must specify at least one profile");
        for (String profile : profiles) {
            if (StringUtils.hasLength(profile) && profile.charAt(0) == '!') {
                if (!isProfileActive(profile.substring(1))) {
                    return true;
                }
            } else if (isProfileActive(profile)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.core.env.Environment
    public boolean acceptsProfiles(Profiles profiles) {
        Assert.notNull(profiles, "Profiles must not be null");
        return profiles.matches(this::isProfileActive);
    }

    protected boolean isProfileActive(String profile) {
        validateProfile(profile);
        Set<String> currentActiveProfiles = doGetActiveProfiles();
        return currentActiveProfiles.contains(profile) || (currentActiveProfiles.isEmpty() && doGetDefaultProfiles().contains(profile));
    }

    protected void validateProfile(String profile) {
        if (!StringUtils.hasText(profile)) {
            throw new IllegalArgumentException("Invalid profile [" + profile + "]: must contain text");
        }
        if (profile.charAt(0) == '!') {
            throw new IllegalArgumentException("Invalid profile [" + profile + "]: must not begin with ! operator");
        }
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public MutablePropertySources getPropertySources() {
        return this.propertySources;
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public Map<String, Object> getSystemProperties() {
        return System.getProperties();
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public Map<String, Object> getSystemEnvironment() {
        if (suppressGetenvAccess()) {
            return Collections.emptyMap();
        }
        return System.getenv();
    }

    protected boolean suppressGetenvAccess() {
        return SpringProperties.getFlag(IGNORE_GETENV_PROPERTY_NAME);
    }

    @Override // org.springframework.core.env.ConfigurableEnvironment
    public void merge(ConfigurableEnvironment parent) {
        Iterator<PropertySource<?>> it = parent.getPropertySources().iterator();
        while (it.hasNext()) {
            PropertySource<?> ps = it.next();
            if (!this.propertySources.contains(ps.getName())) {
                this.propertySources.addLast(ps);
            }
        }
        String[] parentActiveProfiles = parent.getActiveProfiles();
        if (!ObjectUtils.isEmpty((Object[]) parentActiveProfiles)) {
            synchronized (this.activeProfiles) {
                Collections.addAll(this.activeProfiles, parentActiveProfiles);
            }
        }
        String[] parentDefaultProfiles = parent.getDefaultProfiles();
        if (!ObjectUtils.isEmpty((Object[]) parentDefaultProfiles)) {
            synchronized (this.defaultProfiles) {
                this.defaultProfiles.remove("default");
                Collections.addAll(this.defaultProfiles, parentDefaultProfiles);
            }
        }
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public ConfigurableConversionService getConversionService() {
        return this.propertyResolver.getConversionService();
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setConversionService(ConfigurableConversionService conversionService) {
        this.propertyResolver.setConversionService(conversionService);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.propertyResolver.setPlaceholderPrefix(placeholderPrefix);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.propertyResolver.setPlaceholderSuffix(placeholderSuffix);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setValueSeparator(@Nullable String valueSeparator) {
        this.propertyResolver.setValueSeparator(valueSeparator);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
        this.propertyResolver.setIgnoreUnresolvableNestedPlaceholders(ignoreUnresolvableNestedPlaceholders);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setRequiredProperties(String... requiredProperties) {
        this.propertyResolver.setRequiredProperties(requiredProperties);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void validateRequiredProperties() throws MissingRequiredPropertiesException {
        this.propertyResolver.validateRequiredProperties();
    }

    @Override // org.springframework.core.env.PropertyResolver
    public boolean containsProperty(String key) {
        return this.propertyResolver.containsProperty(key);
    }

    @Override // org.springframework.core.env.PropertyResolver
    @Nullable
    public String getProperty(String key) {
        return this.propertyResolver.getProperty(key);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String getProperty(String key, String defaultValue) {
        return this.propertyResolver.getProperty(key, defaultValue);
    }

    @Override // org.springframework.core.env.PropertyResolver
    @Nullable
    public <T> T getProperty(String str, Class<T> cls) {
        return (T) this.propertyResolver.getProperty(str, cls);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public <T> T getProperty(String str, Class<T> cls, T t) {
        return (T) this.propertyResolver.getProperty(str, cls, t);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String getRequiredProperty(String key) throws IllegalStateException {
        return this.propertyResolver.getRequiredProperty(key);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public <T> T getRequiredProperty(String str, Class<T> cls) throws IllegalStateException {
        return (T) this.propertyResolver.getRequiredProperty(str, cls);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String resolvePlaceholders(String text) {
        return this.propertyResolver.resolvePlaceholders(text);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        return this.propertyResolver.resolveRequiredPlaceholders(text);
    }

    public String toString() {
        return getClass().getSimpleName() + " {activeProfiles=" + this.activeProfiles + ", defaultProfiles=" + this.defaultProfiles + ", propertySources=" + this.propertySources + "}";
    }
}
