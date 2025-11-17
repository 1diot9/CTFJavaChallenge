package org.springframework.http.converter.json;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/JsonbHttpMessageConverter.class */
public class JsonbHttpMessageConverter extends AbstractJsonHttpMessageConverter {
    private Jsonb jsonb;

    public JsonbHttpMessageConverter() {
        this(JsonbBuilder.create());
    }

    public JsonbHttpMessageConverter(JsonbConfig config) {
        this.jsonb = JsonbBuilder.create(config);
    }

    public JsonbHttpMessageConverter(Jsonb jsonb) {
        Assert.notNull(jsonb, "A Jsonb instance is required");
        this.jsonb = jsonb;
    }

    public void setJsonb(Jsonb jsonb) {
        Assert.notNull(jsonb, "A Jsonb instance is required");
        this.jsonb = jsonb;
    }

    public Jsonb getJsonb() {
        return this.jsonb;
    }

    @Override // org.springframework.http.converter.json.AbstractJsonHttpMessageConverter
    protected Object readInternal(Type resolvedType, Reader reader) throws Exception {
        return getJsonb().fromJson(reader, resolvedType);
    }

    @Override // org.springframework.http.converter.json.AbstractJsonHttpMessageConverter
    protected void writeInternal(Object object, @Nullable Type type, Writer writer) throws Exception {
        if (type instanceof ParameterizedType) {
            getJsonb().toJson(object, type, writer);
        } else {
            getJsonb().toJson(object, writer);
        }
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supportsRepeatableWrites(Object o) {
        return true;
    }
}
