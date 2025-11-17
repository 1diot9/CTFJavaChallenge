package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/DefaultResourceResolverChain.class */
class DefaultResourceResolverChain implements ResourceResolverChain {

    @Nullable
    private final ResourceResolver resolver;

    @Nullable
    private final ResourceResolverChain nextChain;

    public DefaultResourceResolverChain(@Nullable List<? extends ResourceResolver> resolvers) {
        DefaultResourceResolverChain chain = initChain(new ArrayList(resolvers != null ? resolvers : Collections.emptyList()));
        this.resolver = chain.resolver;
        this.nextChain = chain.nextChain;
    }

    private static DefaultResourceResolverChain initChain(ArrayList<? extends ResourceResolver> resolvers) {
        DefaultResourceResolverChain chain = new DefaultResourceResolverChain(null, null);
        ListIterator<? extends ResourceResolver> it = resolvers.listIterator(resolvers.size());
        while (it.hasPrevious()) {
            chain = new DefaultResourceResolverChain(it.previous(), chain);
        }
        return chain;
    }

    private DefaultResourceResolverChain(@Nullable ResourceResolver resolver, @Nullable ResourceResolverChain chain) {
        Assert.isTrue((resolver == null && chain == null) || !(resolver == null || chain == null), "Both resolver and resolver chain must be null, or neither is");
        this.resolver = resolver;
        this.nextChain = chain;
    }

    @Override // org.springframework.web.servlet.resource.ResourceResolverChain
    @Nullable
    public Resource resolveResource(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations) {
        if (this.resolver == null || this.nextChain == null) {
            return null;
        }
        return this.resolver.resolveResource(request, requestPath, locations, this.nextChain);
    }

    @Override // org.springframework.web.servlet.resource.ResourceResolverChain
    @Nullable
    public String resolveUrlPath(String resourcePath, List<? extends Resource> locations) {
        if (this.resolver == null || this.nextChain == null) {
            return null;
        }
        return this.resolver.resolveUrlPath(resourcePath, locations, this.nextChain);
    }
}
