package org.springframework.web.client.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/client/support/RestGatewaySupport.class */
public class RestGatewaySupport {
    protected final Log logger;
    private RestTemplate restTemplate;

    public RestGatewaySupport() {
        this.logger = LogFactory.getLog(getClass());
        this.restTemplate = new RestTemplate();
    }

    public RestGatewaySupport(ClientHttpRequestFactory requestFactory) {
        this.logger = LogFactory.getLog(getClass());
        Assert.notNull(requestFactory, "'requestFactory' must not be null");
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        Assert.notNull(restTemplate, "'restTemplate' must not be null");
        this.restTemplate = restTemplate;
    }

    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }
}
