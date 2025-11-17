package org.springframework.http.converter.json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import org.springframework.lang.Nullable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/ProblemDetailJacksonMixin.class */
public interface ProblemDetailJacksonMixin {
    @JsonAnySetter
    void setProperty(String name, @Nullable Object value);

    @JsonAnyGetter
    Map<String, Object> getProperties();
}
