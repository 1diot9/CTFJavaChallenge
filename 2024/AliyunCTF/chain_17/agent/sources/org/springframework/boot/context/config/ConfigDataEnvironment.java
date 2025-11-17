package org.springframework.boot.context.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.DefaultPropertiesPropertySource;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataEnvironmentContributor;
import org.springframework.boot.context.config.ConfigDataEnvironmentContributors;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.log.LogMessage;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataEnvironment.class */
class ConfigDataEnvironment {
    static final String LOCATION_PROPERTY = "spring.config.location";
    static final String ADDITIONAL_LOCATION_PROPERTY = "spring.config.additional-location";
    static final String IMPORT_PROPERTY = "spring.config.import";
    static final String ON_NOT_FOUND_PROPERTY = "spring.config.on-not-found";
    static final ConfigDataLocation[] DEFAULT_SEARCH_LOCATIONS;
    private static final ConfigDataLocation[] EMPTY_LOCATIONS;
    private static final Bindable<ConfigDataLocation[]> CONFIG_DATA_LOCATION_ARRAY;
    private static final Bindable<List<String>> STRING_LIST;
    private static final ConfigDataEnvironmentContributors.BinderOption[] ALLOW_INACTIVE_BINDING;
    private static final ConfigDataEnvironmentContributors.BinderOption[] DENY_INACTIVE_BINDING;
    private final DeferredLogFactory logFactory;
    private final Log logger;
    private final ConfigDataNotFoundAction notFoundAction;
    private final ConfigurableBootstrapContext bootstrapContext;
    private final ConfigurableEnvironment environment;
    private final ConfigDataLocationResolvers resolvers;
    private final Collection<String> additionalProfiles;
    private final ConfigDataEnvironmentUpdateListener environmentUpdateListener;
    private final ConfigDataLoaders loaders;
    private final ConfigDataEnvironmentContributors contributors;

    static {
        List<ConfigDataLocation> locations = new ArrayList<>();
        locations.add(ConfigDataLocation.of("optional:classpath:/;optional:classpath:/config/"));
        locations.add(ConfigDataLocation.of("optional:file:./;optional:file:./config/;optional:file:./config/*/"));
        DEFAULT_SEARCH_LOCATIONS = (ConfigDataLocation[]) locations.toArray(new ConfigDataLocation[0]);
        EMPTY_LOCATIONS = new ConfigDataLocation[0];
        CONFIG_DATA_LOCATION_ARRAY = Bindable.of(ConfigDataLocation[].class);
        STRING_LIST = Bindable.listOf(String.class);
        ALLOW_INACTIVE_BINDING = new ConfigDataEnvironmentContributors.BinderOption[0];
        DENY_INACTIVE_BINDING = new ConfigDataEnvironmentContributors.BinderOption[]{ConfigDataEnvironmentContributors.BinderOption.FAIL_ON_BIND_TO_INACTIVE_SOURCE};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataEnvironment(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment, ResourceLoader resourceLoader, Collection<String> additionalProfiles, ConfigDataEnvironmentUpdateListener environmentUpdateListener) {
        Binder binder = Binder.get(environment);
        this.logFactory = logFactory;
        this.logger = logFactory.getLog(getClass());
        this.notFoundAction = (ConfigDataNotFoundAction) binder.bind("spring.config.on-not-found", ConfigDataNotFoundAction.class).orElse(ConfigDataNotFoundAction.FAIL);
        this.bootstrapContext = bootstrapContext;
        this.environment = environment;
        this.resolvers = createConfigDataLocationResolvers(logFactory, bootstrapContext, binder, resourceLoader);
        this.additionalProfiles = additionalProfiles;
        this.environmentUpdateListener = environmentUpdateListener != null ? environmentUpdateListener : ConfigDataEnvironmentUpdateListener.NONE;
        this.loaders = new ConfigDataLoaders(logFactory, bootstrapContext, SpringFactoriesLoader.forDefaultResourceLocation(resourceLoader.getClassLoader()));
        this.contributors = createContributors(binder);
    }

    protected ConfigDataLocationResolvers createConfigDataLocationResolvers(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, Binder binder, ResourceLoader resourceLoader) {
        return new ConfigDataLocationResolvers(logFactory, bootstrapContext, binder, resourceLoader, SpringFactoriesLoader.forDefaultResourceLocation(resourceLoader.getClassLoader()));
    }

    private ConfigDataEnvironmentContributors createContributors(Binder binder) {
        this.logger.trace("Building config data environment contributors");
        MutablePropertySources propertySources = this.environment.getPropertySources();
        List<ConfigDataEnvironmentContributor> contributors = new ArrayList<>(propertySources.size() + 10);
        PropertySource<?> defaultPropertySource = null;
        Iterator<PropertySource<?>> it = propertySources.iterator();
        while (it.hasNext()) {
            PropertySource<?> propertySource = it.next();
            if (DefaultPropertiesPropertySource.hasMatchingName(propertySource)) {
                defaultPropertySource = propertySource;
            } else {
                this.logger.trace(LogMessage.format("Creating wrapped config data contributor for '%s'", propertySource.getName()));
                contributors.add(ConfigDataEnvironmentContributor.ofExisting(propertySource));
            }
        }
        contributors.addAll(getInitialImportContributors(binder));
        if (defaultPropertySource != null) {
            this.logger.trace("Creating wrapped config data contributor for default property source");
            contributors.add(ConfigDataEnvironmentContributor.ofExisting(defaultPropertySource));
        }
        return createContributors(contributors);
    }

    protected ConfigDataEnvironmentContributors createContributors(List<ConfigDataEnvironmentContributor> contributors) {
        return new ConfigDataEnvironmentContributors(this.logFactory, this.bootstrapContext, contributors);
    }

    ConfigDataEnvironmentContributors getContributors() {
        return this.contributors;
    }

    private List<ConfigDataEnvironmentContributor> getInitialImportContributors(Binder binder) {
        List<ConfigDataEnvironmentContributor> initialContributors = new ArrayList<>();
        addInitialImportContributors(initialContributors, bindLocations(binder, IMPORT_PROPERTY, EMPTY_LOCATIONS));
        addInitialImportContributors(initialContributors, bindLocations(binder, ADDITIONAL_LOCATION_PROPERTY, EMPTY_LOCATIONS));
        addInitialImportContributors(initialContributors, bindLocations(binder, LOCATION_PROPERTY, DEFAULT_SEARCH_LOCATIONS));
        return initialContributors;
    }

    private ConfigDataLocation[] bindLocations(Binder binder, String propertyName, ConfigDataLocation[] other) {
        return (ConfigDataLocation[]) binder.bind(propertyName, CONFIG_DATA_LOCATION_ARRAY).orElse(other);
    }

    private void addInitialImportContributors(List<ConfigDataEnvironmentContributor> initialContributors, ConfigDataLocation[] locations) {
        for (int i = locations.length - 1; i >= 0; i--) {
            initialContributors.add(createInitialImportContributor(locations[i]));
        }
    }

    private ConfigDataEnvironmentContributor createInitialImportContributor(ConfigDataLocation location) {
        this.logger.trace(LogMessage.format("Adding initial config data import from location '%s'", location));
        return ConfigDataEnvironmentContributor.ofInitialImport(location);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void processAndApply() {
        ConfigDataImporter importer = new ConfigDataImporter(this.logFactory, this.notFoundAction, this.resolvers, this.loaders);
        registerBootstrapBinder(this.contributors, null, DENY_INACTIVE_BINDING);
        ConfigDataEnvironmentContributors contributors = processInitial(this.contributors, importer);
        ConfigDataActivationContext activationContext = createActivationContext(contributors.getBinder(null, ConfigDataEnvironmentContributors.BinderOption.FAIL_ON_BIND_TO_INACTIVE_SOURCE));
        ConfigDataEnvironmentContributors contributors2 = processWithoutProfiles(contributors, importer, activationContext);
        ConfigDataActivationContext activationContext2 = withProfiles(contributors2, activationContext);
        applyToEnvironment(processWithProfiles(contributors2, importer, activationContext2), activationContext2, importer.getLoadedLocations(), importer.getOptionalLocations());
    }

    private ConfigDataEnvironmentContributors processInitial(ConfigDataEnvironmentContributors contributors, ConfigDataImporter importer) {
        this.logger.trace("Processing initial config data environment contributors without activation context");
        ConfigDataEnvironmentContributors contributors2 = contributors.withProcessedImports(importer, null);
        registerBootstrapBinder(contributors2, null, DENY_INACTIVE_BINDING);
        return contributors2;
    }

    private ConfigDataActivationContext createActivationContext(Binder initialBinder) {
        this.logger.trace("Creating config data activation context from initial contributions");
        try {
            return new ConfigDataActivationContext(this.environment, initialBinder);
        } catch (BindException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof InactiveConfigDataAccessException) {
                InactiveConfigDataAccessException inactiveException = (InactiveConfigDataAccessException) cause;
                throw inactiveException;
            }
            throw ex;
        }
    }

    private ConfigDataEnvironmentContributors processWithoutProfiles(ConfigDataEnvironmentContributors contributors, ConfigDataImporter importer, ConfigDataActivationContext activationContext) {
        this.logger.trace("Processing config data environment contributors with initial activation context");
        ConfigDataEnvironmentContributors contributors2 = contributors.withProcessedImports(importer, activationContext);
        registerBootstrapBinder(contributors2, activationContext, DENY_INACTIVE_BINDING);
        return contributors2;
    }

    private ConfigDataActivationContext withProfiles(ConfigDataEnvironmentContributors contributors, ConfigDataActivationContext activationContext) {
        this.logger.trace("Deducing profiles from current config data environment contributors");
        Binder binder = contributors.getBinder(activationContext, contributor -> {
            return !contributor.hasConfigDataOption(ConfigData.Option.IGNORE_PROFILES);
        }, ConfigDataEnvironmentContributors.BinderOption.FAIL_ON_BIND_TO_INACTIVE_SOURCE);
        try {
            Set<String> additionalProfiles = new LinkedHashSet<>(this.additionalProfiles);
            additionalProfiles.addAll(getIncludedProfiles(contributors, activationContext));
            Profiles profiles = new Profiles(this.environment, binder, additionalProfiles);
            return activationContext.withProfiles(profiles);
        } catch (BindException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof InactiveConfigDataAccessException) {
                InactiveConfigDataAccessException inactiveException = (InactiveConfigDataAccessException) cause;
                throw inactiveException;
            }
            throw ex;
        }
    }

    private Collection<? extends String> getIncludedProfiles(ConfigDataEnvironmentContributors contributors, ConfigDataActivationContext activationContext) {
        PlaceholdersResolver placeholdersResolver = new ConfigDataEnvironmentContributorPlaceholdersResolver(contributors, activationContext, null, true);
        Set<String> result = new LinkedHashSet<>();
        Iterator<ConfigDataEnvironmentContributor> it = contributors.iterator();
        while (it.hasNext()) {
            ConfigDataEnvironmentContributor contributor = it.next();
            ConfigurationPropertySource source = contributor.getConfigurationPropertySource();
            if (source != null && !contributor.hasConfigDataOption(ConfigData.Option.IGNORE_PROFILES)) {
                Binder binder = new Binder(Collections.singleton(source), placeholdersResolver);
                binder.bind(Profiles.INCLUDE_PROFILES, STRING_LIST).ifBound(includes -> {
                    if (!contributor.isActive(activationContext)) {
                        InactiveConfigDataAccessException.throwIfPropertyFound(contributor, Profiles.INCLUDE_PROFILES);
                        InactiveConfigDataAccessException.throwIfPropertyFound(contributor, Profiles.INCLUDE_PROFILES.append("[0]"));
                    }
                    result.addAll(includes);
                });
            }
        }
        return result;
    }

    private ConfigDataEnvironmentContributors processWithProfiles(ConfigDataEnvironmentContributors contributors, ConfigDataImporter importer, ConfigDataActivationContext activationContext) {
        this.logger.trace("Processing config data environment contributors with profile activation context");
        ConfigDataEnvironmentContributors contributors2 = contributors.withProcessedImports(importer, activationContext);
        registerBootstrapBinder(contributors2, activationContext, ALLOW_INACTIVE_BINDING);
        return contributors2;
    }

    private void registerBootstrapBinder(ConfigDataEnvironmentContributors contributors, ConfigDataActivationContext activationContext, ConfigDataEnvironmentContributors.BinderOption... binderOptions) {
        this.bootstrapContext.register(Binder.class, BootstrapRegistry.InstanceSupplier.from(() -> {
            return contributors.getBinder(activationContext, binderOptions);
        }).withScope(BootstrapRegistry.Scope.PROTOTYPE));
    }

    private void applyToEnvironment(ConfigDataEnvironmentContributors contributors, ConfigDataActivationContext activationContext, Set<ConfigDataLocation> loadedLocations, Set<ConfigDataLocation> optionalLocations) {
        checkForInvalidProperties(contributors);
        checkMandatoryLocations(contributors, activationContext, loadedLocations, optionalLocations);
        MutablePropertySources propertySources = this.environment.getPropertySources();
        applyContributor(contributors, activationContext, propertySources);
        DefaultPropertiesPropertySource.moveToEnd(propertySources);
        Profiles profiles = activationContext.getProfiles();
        this.logger.trace(LogMessage.format("Setting default profiles: %s", profiles.getDefault()));
        this.environment.setDefaultProfiles(StringUtils.toStringArray(profiles.getDefault()));
        this.logger.trace(LogMessage.format("Setting active profiles: %s", profiles.getActive()));
        this.environment.setActiveProfiles(StringUtils.toStringArray(profiles.getActive()));
        this.environmentUpdateListener.onSetProfiles(profiles);
    }

    private void applyContributor(ConfigDataEnvironmentContributors contributors, ConfigDataActivationContext activationContext, MutablePropertySources propertySources) {
        this.logger.trace("Applying config data environment contributions");
        Iterator<ConfigDataEnvironmentContributor> it = contributors.iterator();
        while (it.hasNext()) {
            ConfigDataEnvironmentContributor contributor = it.next();
            PropertySource<?> propertySource = contributor.getPropertySource();
            if (contributor.getKind() == ConfigDataEnvironmentContributor.Kind.BOUND_IMPORT && propertySource != null) {
                if (!contributor.isActive(activationContext)) {
                    this.logger.trace(LogMessage.format("Skipping inactive property source '%s'", propertySource.getName()));
                } else {
                    this.logger.trace(LogMessage.format("Adding imported property source '%s'", propertySource.getName()));
                    propertySources.addLast(propertySource);
                    this.environmentUpdateListener.onPropertySourceAdded(propertySource, contributor.getLocation(), contributor.getResource());
                }
            }
        }
    }

    private void checkForInvalidProperties(ConfigDataEnvironmentContributors contributors) {
        Iterator<ConfigDataEnvironmentContributor> it = contributors.iterator();
        while (it.hasNext()) {
            ConfigDataEnvironmentContributor contributor = it.next();
            InvalidConfigDataPropertyException.throwIfPropertyFound(contributor);
        }
    }

    private void checkMandatoryLocations(ConfigDataEnvironmentContributors contributors, ConfigDataActivationContext activationContext, Set<ConfigDataLocation> loadedLocations, Set<ConfigDataLocation> optionalLocations) {
        Set<ConfigDataLocation> mandatoryLocations = new LinkedHashSet<>();
        Iterator<ConfigDataEnvironmentContributor> it = contributors.iterator();
        while (it.hasNext()) {
            ConfigDataEnvironmentContributor contributor = it.next();
            if (contributor.isActive(activationContext)) {
                mandatoryLocations.addAll(getMandatoryImports(contributor));
            }
        }
        Iterator<ConfigDataEnvironmentContributor> it2 = contributors.iterator();
        while (it2.hasNext()) {
            ConfigDataEnvironmentContributor contributor2 = it2.next();
            if (contributor2.getLocation() != null) {
                mandatoryLocations.remove(contributor2.getLocation());
            }
        }
        mandatoryLocations.removeAll(loadedLocations);
        mandatoryLocations.removeAll(optionalLocations);
        if (!mandatoryLocations.isEmpty()) {
            for (ConfigDataLocation mandatoryLocation : mandatoryLocations) {
                this.notFoundAction.handle(this.logger, new ConfigDataLocationNotFoundException(mandatoryLocation));
            }
        }
    }

    private Set<ConfigDataLocation> getMandatoryImports(ConfigDataEnvironmentContributor contributor) {
        List<ConfigDataLocation> imports = contributor.getImports();
        Set<ConfigDataLocation> mandatoryLocations = new LinkedHashSet<>(imports.size());
        for (ConfigDataLocation location : imports) {
            if (!location.isOptional()) {
                mandatoryLocations.add(location);
            }
        }
        return mandatoryLocations;
    }
}
