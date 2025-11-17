package org.springframework.boot.autoconfigure.web.reactive;

import java.util.Arrays;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WelcomePageRouterFunctionFactory.class */
final class WelcomePageRouterFunctionFactory {
    private final String staticPathPattern;
    private final Resource welcomePage;
    private final boolean welcomePageTemplateExists;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WelcomePageRouterFunctionFactory(TemplateAvailabilityProviders templateAvailabilityProviders, ApplicationContext applicationContext, String[] staticLocations, String staticPathPattern) {
        this.staticPathPattern = staticPathPattern;
        this.welcomePage = getWelcomePage(applicationContext, staticLocations);
        this.welcomePageTemplateExists = welcomeTemplateExists(templateAvailabilityProviders, applicationContext);
    }

    private Resource getWelcomePage(ResourceLoader resourceLoader, String[] staticLocations) {
        return (Resource) Arrays.stream(staticLocations).map(location -> {
            return getIndexHtml(resourceLoader, location);
        }).filter(this::isReadable).findFirst().orElse(null);
    }

    private Resource getIndexHtml(ResourceLoader resourceLoader, String location) {
        return resourceLoader.getResource(location + "index.html");
    }

    private boolean isReadable(Resource resource) {
        try {
            if (resource.exists()) {
                if (resource.getURL() != null) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean welcomeTemplateExists(TemplateAvailabilityProviders templateAvailabilityProviders, ApplicationContext applicationContext) {
        return templateAvailabilityProviders.getProvider(BeanDefinitionParserDelegate.INDEX_ATTRIBUTE, applicationContext) != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RouterFunction<ServerResponse> createRouterFunction() {
        if (this.welcomePage != null && "/**".equals(this.staticPathPattern)) {
            return RouterFunctions.route(RequestPredicates.GET("/").and(RequestPredicates.accept(new MediaType[]{MediaType.TEXT_HTML})), req -> {
                return ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(this.welcomePage);
            });
        }
        if (this.welcomePageTemplateExists) {
            return RouterFunctions.route(RequestPredicates.GET("/").and(RequestPredicates.accept(new MediaType[]{MediaType.TEXT_HTML})), req2 -> {
                return ServerResponse.ok().render(BeanDefinitionParserDelegate.INDEX_ATTRIBUTE, new Object[0]);
            });
        }
        return null;
    }
}
