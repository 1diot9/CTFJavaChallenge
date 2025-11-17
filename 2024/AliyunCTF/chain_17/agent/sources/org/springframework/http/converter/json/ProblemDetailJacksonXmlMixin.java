package org.springframework.http.converter.json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.net.URI;
import java.util.Map;
import org.springframework.lang.Nullable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(localName = "problem", namespace = ProblemDetailJacksonXmlMixin.RFC_7807_NAMESPACE)
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/ProblemDetailJacksonXmlMixin.class */
public interface ProblemDetailJacksonXmlMixin {
    public static final String RFC_7807_NAMESPACE = "urn:ietf:rfc:7807";

    @JacksonXmlProperty(namespace = RFC_7807_NAMESPACE)
    URI getType();

    @JacksonXmlProperty(namespace = RFC_7807_NAMESPACE)
    String getTitle();

    @JacksonXmlProperty(namespace = RFC_7807_NAMESPACE)
    int getStatus();

    @JacksonXmlProperty(namespace = RFC_7807_NAMESPACE)
    String getDetail();

    @JacksonXmlProperty(namespace = RFC_7807_NAMESPACE)
    URI getInstance();

    @JsonAnySetter
    void setProperty(String name, @Nullable Object value);

    @JsonAnyGetter
    @JacksonXmlProperty(namespace = RFC_7807_NAMESPACE)
    Map<String, Object> getProperties();
}
