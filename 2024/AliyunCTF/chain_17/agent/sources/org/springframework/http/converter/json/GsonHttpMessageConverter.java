package org.springframework.http.converter.json;

import com.google.gson.Gson;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/GsonHttpMessageConverter.class */
public class GsonHttpMessageConverter extends AbstractJsonHttpMessageConverter {
    private Gson gson;

    public GsonHttpMessageConverter() {
        this.gson = new Gson();
    }

    public GsonHttpMessageConverter(Gson gson) {
        Assert.notNull(gson, "A Gson instance is required");
        this.gson = gson;
    }

    public void setGson(Gson gson) {
        Assert.notNull(gson, "A Gson instance is required");
        this.gson = gson;
    }

    public Gson getGson() {
        return this.gson;
    }

    @Override // org.springframework.http.converter.json.AbstractJsonHttpMessageConverter
    protected Object readInternal(Type resolvedType, Reader reader) throws Exception {
        return getGson().fromJson(reader, resolvedType);
    }

    @Override // org.springframework.http.converter.json.AbstractJsonHttpMessageConverter
    protected void writeInternal(Object object, @Nullable Type type, Writer writer) throws Exception {
        if (type instanceof ParameterizedType) {
            getGson().toJson(object, type, writer);
        } else {
            getGson().toJson(object, writer);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supportsRepeatableWrites(Object o) {
        return true;
    }
}
