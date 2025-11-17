package org.springframework.web.accept;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/accept/ParameterContentNegotiationStrategy.class */
public class ParameterContentNegotiationStrategy extends AbstractMappingContentNegotiationStrategy {
    private String parameterName;

    public ParameterContentNegotiationStrategy(Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
        this.parameterName = "format";
    }

    public void setParameterName(String parameterName) {
        Assert.notNull(parameterName, "'parameterName' is required");
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    @Override // org.springframework.web.accept.AbstractMappingContentNegotiationStrategy
    @Nullable
    protected String getMediaTypeKey(NativeWebRequest request) {
        return request.getParameter(getParameterName());
    }
}
