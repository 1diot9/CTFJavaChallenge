package org.springframework.boot.context.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.log.LogMessage;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataImporter.class */
class ConfigDataImporter {
    private final Log logger;
    private final ConfigDataLocationResolvers resolvers;
    private final ConfigDataLoaders loaders;
    private final ConfigDataNotFoundAction notFoundAction;
    private final Set<ConfigDataResource> loaded = new HashSet();
    private final Set<ConfigDataLocation> loadedLocations = new HashSet();
    private final Set<ConfigDataLocation> optionalLocations = new HashSet();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConfigDataImporter(DeferredLogFactory logFactory, ConfigDataNotFoundAction notFoundAction, ConfigDataLocationResolvers resolvers, ConfigDataLoaders loaders) {
        this.logger = logFactory.getLog(getClass());
        this.resolvers = resolvers;
        this.loaders = loaders;
        this.notFoundAction = notFoundAction;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<ConfigDataResolutionResult, ConfigData> resolveAndLoad(ConfigDataActivationContext activationContext, ConfigDataLocationResolverContext locationResolverContext, ConfigDataLoaderContext loaderContext, List<ConfigDataLocation> locations) {
        Profiles profiles;
        if (activationContext != null) {
            try {
                profiles = activationContext.getProfiles();
            } catch (IOException ex) {
                throw new IllegalStateException("IO error on loading imports from " + locations, ex);
            }
        } else {
            profiles = null;
        }
        Profiles profiles2 = profiles;
        List<ConfigDataResolutionResult> resolved = resolve(locationResolverContext, profiles2, locations);
        return load(loaderContext, resolved);
    }

    private List<ConfigDataResolutionResult> resolve(ConfigDataLocationResolverContext locationResolverContext, Profiles profiles, List<ConfigDataLocation> locations) {
        List<ConfigDataResolutionResult> resolved = new ArrayList<>(locations.size());
        for (ConfigDataLocation location : locations) {
            resolved.addAll(resolve(locationResolverContext, profiles, location));
        }
        return Collections.unmodifiableList(resolved);
    }

    private List<ConfigDataResolutionResult> resolve(ConfigDataLocationResolverContext locationResolverContext, Profiles profiles, ConfigDataLocation location) {
        try {
            return this.resolvers.resolve(locationResolverContext, location, profiles);
        } catch (ConfigDataNotFoundException ex) {
            handle(ex, location, null);
            return Collections.emptyList();
        }
    }

    private Map<ConfigDataResolutionResult, ConfigData> load(ConfigDataLoaderContext loaderContext, List<ConfigDataResolutionResult> candidates) throws IOException {
        Map<ConfigDataResolutionResult, ConfigData> result = new LinkedHashMap<>();
        for (int i = candidates.size() - 1; i >= 0; i--) {
            ConfigDataResolutionResult candidate = candidates.get(i);
            ConfigDataLocation location = candidate.getLocation();
            ConfigDataResource resource = candidate.getResource();
            this.logger.trace(LogMessage.format("Considering resource %s from location %s", resource, location));
            if (resource.isOptional()) {
                this.optionalLocations.add(location);
            }
            if (this.loaded.contains(resource)) {
                this.logger.trace(LogMessage.format("Already loaded resource %s ignoring location %s", resource, location));
                this.loadedLocations.add(location);
            } else {
                try {
                    ConfigData loaded = this.loaders.load(loaderContext, resource);
                    if (loaded != null) {
                        this.logger.trace(LogMessage.format("Loaded resource %s from location %s", resource, location));
                        this.loaded.add(resource);
                        this.loadedLocations.add(location);
                        result.put(candidate, loaded);
                    }
                } catch (ConfigDataNotFoundException ex) {
                    handle(ex, location, resource);
                }
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private void handle(ConfigDataNotFoundException ex, ConfigDataLocation location, ConfigDataResource resource) {
        if (ex instanceof ConfigDataResourceNotFoundException) {
            ConfigDataResourceNotFoundException notFoundException = (ConfigDataResourceNotFoundException) ex;
            ex = notFoundException.withLocation(location);
        }
        getNotFoundAction(location, resource).handle(this.logger, ex);
    }

    private ConfigDataNotFoundAction getNotFoundAction(ConfigDataLocation location, ConfigDataResource resource) {
        if (location.isOptional() || (resource != null && resource.isOptional())) {
            return ConfigDataNotFoundAction.IGNORE;
        }
        return this.notFoundAction;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<ConfigDataLocation> getLoadedLocations() {
        return this.loadedLocations;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<ConfigDataLocation> getOptionalLocations() {
        return this.optionalLocations;
    }
}
