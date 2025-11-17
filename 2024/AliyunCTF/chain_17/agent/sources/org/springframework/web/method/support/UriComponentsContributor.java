package org.springframework.web.method.support;

import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.util.UriComponentsBuilder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/support/UriComponentsContributor.class */
public interface UriComponentsContributor {
    boolean supportsParameter(MethodParameter parameter);

    void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService);
}
