package org.springframework.web.servlet.view.xml;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.Map;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.json.AbstractJackson2View;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/xml/MappingJackson2XmlView.class */
public class MappingJackson2XmlView extends AbstractJackson2View {
    public static final String DEFAULT_CONTENT_TYPE = "application/xml";

    @Nullable
    private String modelKey;

    public MappingJackson2XmlView() {
        super(Jackson2ObjectMapperBuilder.xml().build(), "application/xml");
    }

    public MappingJackson2XmlView(XmlMapper xmlMapper) {
        super(xmlMapper, "application/xml");
    }

    @Override // org.springframework.web.servlet.view.json.AbstractJackson2View
    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    @Override // org.springframework.web.servlet.view.json.AbstractJackson2View
    protected Object filterModel(Map<String, Object> model) {
        Object value = null;
        if (this.modelKey != null) {
            value = model.get(this.modelKey);
            if (value == null) {
                throw new IllegalStateException("Model contains no object with key [" + this.modelKey + "]");
            }
        } else {
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                if (!(entry.getValue() instanceof BindingResult) && !entry.getKey().equals(JsonView.class.getName())) {
                    if (value != null) {
                        throw new IllegalStateException("Model contains more than one object to render, only one is supported");
                    }
                    value = entry.getValue();
                }
            }
        }
        Assert.state(value != null, "Model contains no object to render");
        return value;
    }
}
