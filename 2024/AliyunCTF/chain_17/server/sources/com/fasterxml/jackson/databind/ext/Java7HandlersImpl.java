package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import java.nio.file.Path;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ext/Java7HandlersImpl.class */
public class Java7HandlersImpl extends Java7Handlers {
    private final Class<?> _pathClass = Path.class;

    @Override // com.fasterxml.jackson.databind.ext.Java7Handlers
    public Class<?> getClassJavaNioFilePath() {
        return this._pathClass;
    }

    @Override // com.fasterxml.jackson.databind.ext.Java7Handlers
    public JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> rawType) {
        if (rawType == this._pathClass) {
            return new NioPathDeserializer();
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.ext.Java7Handlers
    public JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> rawType) {
        if (this._pathClass.isAssignableFrom(rawType)) {
            return new NioPathSerializer();
        }
        return null;
    }
}
