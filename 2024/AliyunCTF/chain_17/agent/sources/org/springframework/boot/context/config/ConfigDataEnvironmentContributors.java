package org.springframework.boot.context.config;

import cn.hutool.core.text.CharSequenceUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.logging.Log;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.config.ConfigDataEnvironmentContributor;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.util.ObjectUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataEnvironmentContributors.class */
public class ConfigDataEnvironmentContributors implements Iterable<ConfigDataEnvironmentContributor> {
    private static final Predicate<ConfigDataEnvironmentContributor> NO_CONTRIBUTOR_FILTER = contributor -> {
        return true;
    };
    private final Log logger;
    private final ConfigDataEnvironmentContributor root;
    private final ConfigurableBootstrapContext bootstrapContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataEnvironmentContributors$BinderOption.class */
    public enum BinderOption {
        FAIL_ON_BIND_TO_INACTIVE_SOURCE
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataEnvironmentContributors(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, List<ConfigDataEnvironmentContributor> contributors) {
        this.logger = logFactory.getLog(getClass());
        this.bootstrapContext = bootstrapContext;
        this.root = ConfigDataEnvironmentContributor.of(contributors);
    }

    private ConfigDataEnvironmentContributors(Log logger, ConfigurableBootstrapContext bootstrapContext, ConfigDataEnvironmentContributor root) {
        this.logger = logger;
        this.bootstrapContext = bootstrapContext;
        this.root = root;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataEnvironmentContributors withProcessedImports(ConfigDataImporter importer, ConfigDataActivationContext activationContext) {
        ConfigDataEnvironmentContributor.ImportPhase importPhase = ConfigDataEnvironmentContributor.ImportPhase.get(activationContext);
        this.logger.trace(LogMessage.format("Processing imports for phase %s. %s", importPhase, activationContext != null ? activationContext : "no activation context"));
        ConfigDataEnvironmentContributors result = this;
        int processed = 0;
        while (true) {
            ConfigDataEnvironmentContributor contributor = getNextToProcess(result, activationContext, importPhase);
            if (contributor == null) {
                this.logger.trace(LogMessage.format("Processed imports for of %d contributors", Integer.valueOf(processed)));
                return result;
            }
            if (contributor.getKind() == ConfigDataEnvironmentContributor.Kind.UNBOUND_IMPORT) {
                ConfigDataEnvironmentContributor bound = contributor.withBoundProperties(result, activationContext);
                result = new ConfigDataEnvironmentContributors(this.logger, this.bootstrapContext, result.getRoot().withReplacement(contributor, bound));
            } else {
                ConfigDataLocationResolverContext locationResolverContext = new ContributorConfigDataLocationResolverContext(result, contributor, activationContext);
                ConfigDataLoaderContext loaderContext = new ContributorDataLoaderContext(this);
                List<ConfigDataLocation> imports = contributor.getImports();
                this.logger.trace(LogMessage.format("Processing imports %s", imports));
                Map<ConfigDataResolutionResult, ConfigData> imported = importer.resolveAndLoad(activationContext, locationResolverContext, loaderContext, imports);
                this.logger.trace(LogMessage.of(() -> {
                    return getImportedMessage(imported.keySet());
                }));
                ConfigDataEnvironmentContributor contributorAndChildren = contributor.withChildren(importPhase, asContributors(imported));
                result = new ConfigDataEnvironmentContributors(this.logger, this.bootstrapContext, result.getRoot().withReplacement(contributor, contributorAndChildren));
                processed++;
            }
        }
    }

    private CharSequence getImportedMessage(Set<ConfigDataResolutionResult> results) {
        if (results.isEmpty()) {
            return "Nothing imported";
        }
        StringBuilder message = new StringBuilder();
        message.append("Imported " + results.size() + " resource" + (results.size() != 1 ? "s " : CharSequenceUtil.SPACE));
        message.append(results.stream().map((v0) -> {
            return v0.getResource();
        }).toList());
        return message;
    }

    protected final ConfigurableBootstrapContext getBootstrapContext() {
        return this.bootstrapContext;
    }

    private ConfigDataEnvironmentContributor getNextToProcess(ConfigDataEnvironmentContributors contributors, ConfigDataActivationContext activationContext, ConfigDataEnvironmentContributor.ImportPhase importPhase) {
        Iterator<ConfigDataEnvironmentContributor> it = contributors.getRoot().iterator();
        while (it.hasNext()) {
            ConfigDataEnvironmentContributor contributor = it.next();
            if (contributor.getKind() == ConfigDataEnvironmentContributor.Kind.UNBOUND_IMPORT || isActiveWithUnprocessedImports(activationContext, importPhase, contributor)) {
                return contributor;
            }
        }
        return null;
    }

    private boolean isActiveWithUnprocessedImports(ConfigDataActivationContext activationContext, ConfigDataEnvironmentContributor.ImportPhase importPhase, ConfigDataEnvironmentContributor contributor) {
        return contributor.isActive(activationContext) && contributor.hasUnprocessedImports(importPhase);
    }

    private List<ConfigDataEnvironmentContributor> asContributors(Map<ConfigDataResolutionResult, ConfigData> imported) {
        List<ConfigDataEnvironmentContributor> contributors = new ArrayList<>(imported.size() * 5);
        imported.forEach((resolutionResult, data) -> {
            ConfigDataLocation location = resolutionResult.getLocation();
            ConfigDataResource resource = resolutionResult.getResource();
            boolean profileSpecific = resolutionResult.isProfileSpecific();
            if (data.getPropertySources().isEmpty()) {
                contributors.add(ConfigDataEnvironmentContributor.ofEmptyLocation(location, profileSpecific));
                return;
            }
            for (int i = data.getPropertySources().size() - 1; i >= 0; i--) {
                contributors.add(ConfigDataEnvironmentContributor.ofUnboundImport(location, resource, profileSpecific, data, i));
            }
        });
        return Collections.unmodifiableList(contributors);
    }

    ConfigDataEnvironmentContributor getRoot() {
        return this.root;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Binder getBinder(ConfigDataActivationContext activationContext, BinderOption... options) {
        return getBinder(activationContext, NO_CONTRIBUTOR_FILTER, options);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Binder getBinder(ConfigDataActivationContext activationContext, Predicate<ConfigDataEnvironmentContributor> filter, BinderOption... options) {
        return getBinder(activationContext, filter, asBinderOptionsSet(options));
    }

    private Set<BinderOption> asBinderOptionsSet(BinderOption... options) {
        return ObjectUtils.isEmpty((Object[]) options) ? EnumSet.noneOf(BinderOption.class) : EnumSet.copyOf((Collection) Arrays.asList(options));
    }

    private Binder getBinder(ConfigDataActivationContext activationContext, Predicate<ConfigDataEnvironmentContributor> filter, Set<BinderOption> options) {
        boolean failOnInactiveSource = options.contains(BinderOption.FAIL_ON_BIND_TO_INACTIVE_SOURCE);
        Iterable<ConfigurationPropertySource> sources = () -> {
            return getBinderSources(filter.and(contributor -> {
                return failOnInactiveSource || contributor.isActive(activationContext);
            }));
        };
        PlaceholdersResolver placeholdersResolver = new ConfigDataEnvironmentContributorPlaceholdersResolver(this.root, activationContext, null, failOnInactiveSource);
        BindHandler bindHandler = !failOnInactiveSource ? null : new InactiveSourceChecker(activationContext);
        return new Binder(sources, placeholdersResolver, null, null, bindHandler);
    }

    private Iterator<ConfigurationPropertySource> getBinderSources(Predicate<ConfigDataEnvironmentContributor> filter) {
        return this.root.stream().filter(this::hasConfigurationPropertySource).filter(filter).map((v0) -> {
            return v0.getConfigurationPropertySource();
        }).iterator();
    }

    private boolean hasConfigurationPropertySource(ConfigDataEnvironmentContributor contributor) {
        return contributor.getConfigurationPropertySource() != null;
    }

    @Override // java.lang.Iterable
    public Iterator<ConfigDataEnvironmentContributor> iterator() {
        return this.root.iterator();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataEnvironmentContributors$ContributorDataLoaderContext.class */
    private static class ContributorDataLoaderContext implements ConfigDataLoaderContext {
        private final ConfigDataEnvironmentContributors contributors;

        ContributorDataLoaderContext(ConfigDataEnvironmentContributors contributors) {
            this.contributors = contributors;
        }

        @Override // org.springframework.boot.context.config.ConfigDataLoaderContext
        public ConfigurableBootstrapContext getBootstrapContext() {
            return this.contributors.getBootstrapContext();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataEnvironmentContributors$ContributorConfigDataLocationResolverContext.class */
    private static class ContributorConfigDataLocationResolverContext implements ConfigDataLocationResolverContext {
        private final ConfigDataEnvironmentContributors contributors;
        private final ConfigDataEnvironmentContributor contributor;
        private final ConfigDataActivationContext activationContext;
        private volatile Binder binder;

        ContributorConfigDataLocationResolverContext(ConfigDataEnvironmentContributors contributors, ConfigDataEnvironmentContributor contributor, ConfigDataActivationContext activationContext) {
            this.contributors = contributors;
            this.contributor = contributor;
            this.activationContext = activationContext;
        }

        @Override // org.springframework.boot.context.config.ConfigDataLocationResolverContext
        public Binder getBinder() {
            Binder binder = this.binder;
            if (binder == null) {
                binder = this.contributors.getBinder(this.activationContext, new BinderOption[0]);
                this.binder = binder;
            }
            return binder;
        }

        @Override // org.springframework.boot.context.config.ConfigDataLocationResolverContext
        public ConfigDataResource getParent() {
            return this.contributor.getResource();
        }

        @Override // org.springframework.boot.context.config.ConfigDataLocationResolverContext
        public ConfigurableBootstrapContext getBootstrapContext() {
            return this.contributors.getBootstrapContext();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataEnvironmentContributors$InactiveSourceChecker.class */
    public class InactiveSourceChecker implements BindHandler {
        private final ConfigDataActivationContext activationContext;

        InactiveSourceChecker(ConfigDataActivationContext activationContext) {
            this.activationContext = activationContext;
        }

        @Override // org.springframework.boot.context.properties.bind.BindHandler
        public Object onSuccess(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) {
            Iterator<ConfigDataEnvironmentContributor> it = ConfigDataEnvironmentContributors.this.iterator();
            while (it.hasNext()) {
                ConfigDataEnvironmentContributor contributor = it.next();
                if (!contributor.isActive(this.activationContext)) {
                    InactiveConfigDataAccessException.throwIfPropertyFound(contributor, name);
                }
            }
            return result;
        }
    }
}
