package org.springframework.http.converter.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/xml/MappingJackson2XmlHttpMessageConverter.class */
public class MappingJackson2XmlHttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    private static final List<MediaType> problemDetailMediaTypes = Collections.singletonList(MediaType.APPLICATION_PROBLEM_XML);

    public MappingJackson2XmlHttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.xml().build());
    }

    public MappingJackson2XmlHttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, new MediaType("application", "xml", StandardCharsets.UTF_8), new MediaType("text", "xml", StandardCharsets.UTF_8), new MediaType("application", "*+xml", StandardCharsets.UTF_8));
        Assert.isInstanceOf((Class<?>) XmlMapper.class, objectMapper, "XmlMapper required");
    }

    @Override // org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.isInstanceOf((Class<?>) XmlMapper.class, objectMapper, "XmlMapper required");
        super.setObjectMapper(objectMapper);
    }

    @Override // org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
    protected List<MediaType> getMediaTypesForProblemDetail() {
        return problemDetailMediaTypes;
    }
}
