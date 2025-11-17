package org.springframework.boot.autoconfigure.web.reactive;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.web.reactive.config.ResourceChainRegistration;
import org.springframework.web.reactive.config.ResourceHandlerRegistration;
import org.springframework.web.reactive.resource.EncodedResourceResolver;
import org.springframework.web.reactive.resource.ResourceResolver;
import org.springframework.web.reactive.resource.VersionResourceResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/ResourceChainResourceHandlerRegistrationCustomizer.class */
class ResourceChainResourceHandlerRegistrationCustomizer implements ResourceHandlerRegistrationCustomizer {
    private final WebProperties.Resources resourceProperties;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResourceChainResourceHandlerRegistrationCustomizer(WebProperties.Resources resources) {
        this.resourceProperties = resources;
    }

    @Override // org.springframework.boot.autoconfigure.web.reactive.ResourceHandlerRegistrationCustomizer
    public void customize(ResourceHandlerRegistration registration) {
        WebProperties.Resources.Chain properties = this.resourceProperties.getChain();
        configureResourceChain(properties, registration.resourceChain(properties.isCache()));
    }

    private void configureResourceChain(WebProperties.Resources.Chain properties, ResourceChainRegistration chain) {
        WebProperties.Resources.Chain.Strategy strategy = properties.getStrategy();
        if (properties.isCompressed()) {
            chain.addResolver(new EncodedResourceResolver());
        }
        if (strategy.getFixed().isEnabled() || strategy.getContent().isEnabled()) {
            chain.addResolver(getVersionResourceResolver(strategy));
        }
    }

    private ResourceResolver getVersionResourceResolver(WebProperties.Resources.Chain.Strategy properties) {
        VersionResourceResolver resolver = new VersionResourceResolver();
        if (properties.getFixed().isEnabled()) {
            String version = properties.getFixed().getVersion();
            String[] paths = properties.getFixed().getPaths();
            resolver.addFixedVersionStrategy(version, paths);
        }
        if (properties.getContent().isEnabled()) {
            String[] paths2 = properties.getContent().getPaths();
            resolver.addContentVersionStrategy(paths2);
        }
        return resolver;
    }
}
