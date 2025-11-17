package org.springframework.http.converter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/MappingJackson2HttpMessageConverter.class */
public class MappingJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    private static final List<MediaType> problemDetailMediaTypes = Collections.singletonList(MediaType.APPLICATION_PROBLEM_JSON);

    @Nullable
    private String jsonPrefix;

    public MappingJackson2HttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.json().build());
    }

    public MappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = prefixJson ? ")]}', " : null;
    }

    @Override // org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
    protected List<MediaType> getMediaTypesForProblemDetail() {
        return problemDetailMediaTypes;
    }

    @Override // org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        if (this.jsonPrefix != null) {
            generator.writeRaw(this.jsonPrefix);
        }
    }
}
