package org.springframework.web.servlet.view.json;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/json/MappingJackson2JsonView.class */
public class MappingJackson2JsonView extends AbstractJackson2View {
    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    @Nullable
    private String jsonPrefix;

    @Nullable
    private Set<String> modelKeys;
    private boolean extractValueFromSingleKeyModel;

    public MappingJackson2JsonView() {
        super(Jackson2ObjectMapperBuilder.json().build(), "application/json");
        this.extractValueFromSingleKeyModel = false;
    }

    public MappingJackson2JsonView(ObjectMapper objectMapper) {
        super(objectMapper, "application/json");
        this.extractValueFromSingleKeyModel = false;
    }

    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = prefixJson ? ")]}', " : null;
    }

    @Override // org.springframework.web.servlet.view.json.AbstractJackson2View
    public void setModelKey(String modelKey) {
        this.modelKeys = Collections.singleton(modelKey);
    }

    public void setModelKeys(@Nullable Set<String> modelKeys) {
        this.modelKeys = modelKeys;
    }

    @Nullable
    public final Set<String> getModelKeys() {
        return this.modelKeys;
    }

    public void setExtractValueFromSingleKeyModel(boolean extractValueFromSingleKeyModel) {
        this.extractValueFromSingleKeyModel = extractValueFromSingleKeyModel;
    }

    @Override // org.springframework.web.servlet.view.json.AbstractJackson2View
    protected Object filterModel(Map<String, Object> model) {
        Map<String, Object> result = CollectionUtils.newHashMap(model.size());
        Set<String> modelKeys = !CollectionUtils.isEmpty(this.modelKeys) ? this.modelKeys : model.keySet();
        model.forEach((clazz, value) -> {
            if (!(value instanceof BindingResult) && modelKeys.contains(clazz) && !clazz.equals(JsonView.class.getName()) && !clazz.equals(FilterProvider.class.getName())) {
                result.put(clazz, value);
            }
        });
        return (this.extractValueFromSingleKeyModel && result.size() == 1) ? result.values().iterator().next() : result;
    }

    @Override // org.springframework.web.servlet.view.json.AbstractJackson2View
    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        if (this.jsonPrefix != null) {
            generator.writeRaw(this.jsonPrefix);
        }
    }
}
