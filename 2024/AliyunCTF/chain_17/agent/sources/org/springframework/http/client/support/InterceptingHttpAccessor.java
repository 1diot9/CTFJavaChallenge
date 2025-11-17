package org.springframework.http.client.support;

import java.util.ArrayList;
import java.util.List;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/client/support/InterceptingHttpAccessor.class */
public abstract class InterceptingHttpAccessor extends HttpAccessor {
    private final List<ClientHttpRequestInterceptor> interceptors = new ArrayList();

    @Nullable
    private volatile ClientHttpRequestFactory interceptingRequestFactory;

    public void setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
        Assert.noNullElements(interceptors, "'interceptors' must not contain null elements");
        if (this.interceptors != interceptors) {
            this.interceptors.clear();
            this.interceptors.addAll(interceptors);
            AnnotationAwareOrderComparator.sort(this.interceptors);
        }
    }

    public List<ClientHttpRequestInterceptor> getInterceptors() {
        return this.interceptors;
    }

    @Override // org.springframework.http.client.support.HttpAccessor
    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        super.setRequestFactory(requestFactory);
        this.interceptingRequestFactory = null;
    }

    @Override // org.springframework.http.client.support.HttpAccessor
    public ClientHttpRequestFactory getRequestFactory() {
        List<ClientHttpRequestInterceptor> interceptors = getInterceptors();
        if (!CollectionUtils.isEmpty(interceptors)) {
            ClientHttpRequestFactory factory = this.interceptingRequestFactory;
            if (factory == null) {
                factory = new InterceptingClientHttpRequestFactory(super.getRequestFactory(), interceptors);
                this.interceptingRequestFactory = factory;
            }
            return factory;
        }
        return super.getRequestFactory();
    }
}
