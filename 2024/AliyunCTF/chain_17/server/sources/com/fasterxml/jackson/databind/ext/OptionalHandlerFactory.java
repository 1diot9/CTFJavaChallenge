package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.ExceptionUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ext/OptionalHandlerFactory.class */
public class OptionalHandlerFactory implements Serializable {
    private static final long serialVersionUID = 1;
    private static final String PACKAGE_PREFIX_JAVAX_XML = "javax.xml.";
    private static final String SERIALIZERS_FOR_JAVAX_XML = "com.fasterxml.jackson.databind.ext.CoreXMLSerializers";
    private static final String DESERIALIZERS_FOR_JAVAX_XML = "com.fasterxml.jackson.databind.ext.CoreXMLDeserializers";
    private static final String SERIALIZER_FOR_DOM_NODE = "com.fasterxml.jackson.databind.ext.DOMSerializer";
    private static final String DESERIALIZER_FOR_DOM_DOCUMENT = "com.fasterxml.jackson.databind.ext.DOMDeserializer$DocumentDeserializer";
    private static final String DESERIALIZER_FOR_DOM_NODE = "com.fasterxml.jackson.databind.ext.DOMDeserializer$NodeDeserializer";
    private static final Class<?> CLASS_DOM_NODE = Node.class;
    private static final Class<?> CLASS_DOM_DOCUMENT = Document.class;
    private static final Java7Handlers _jdk7Helper;
    public static final OptionalHandlerFactory instance;
    private final Map<String, String> _sqlDeserializers = new HashMap();
    private final Map<String, Object> _sqlSerializers;
    private static final String CLS_NAME_JAVA_SQL_TIMESTAMP = "java.sql.Timestamp";
    private static final String CLS_NAME_JAVA_SQL_DATE = "java.sql.Date";
    private static final String CLS_NAME_JAVA_SQL_TIME = "java.sql.Time";
    private static final String CLS_NAME_JAVA_SQL_BLOB = "java.sql.Blob";
    private static final String CLS_NAME_JAVA_SQL_SERIALBLOB = "javax.sql.rowset.serial.SerialBlob";

    /* JADX WARN: Unreachable blocks removed: 2, instructions: 3 */
    static {
        Java7Handlers x = null;
        try {
            x = Java7Handlers.instance();
        } catch (Throwable t) {
            ExceptionUtil.rethrowIfFatal(t);
        }
        _jdk7Helper = x;
        instance = new OptionalHandlerFactory();
    }

    protected OptionalHandlerFactory() {
        this._sqlDeserializers.put(CLS_NAME_JAVA_SQL_DATE, "com.fasterxml.jackson.databind.deser.std.DateDeserializers$SqlDateDeserializer");
        this._sqlDeserializers.put(CLS_NAME_JAVA_SQL_TIMESTAMP, "com.fasterxml.jackson.databind.deser.std.DateDeserializers$TimestampDeserializer");
        this._sqlSerializers = new HashMap();
        this._sqlSerializers.put(CLS_NAME_JAVA_SQL_TIMESTAMP, DateSerializer.instance);
        this._sqlSerializers.put(CLS_NAME_JAVA_SQL_DATE, "com.fasterxml.jackson.databind.ser.std.SqlDateSerializer");
        this._sqlSerializers.put(CLS_NAME_JAVA_SQL_TIME, "com.fasterxml.jackson.databind.ser.std.SqlTimeSerializer");
        this._sqlSerializers.put(CLS_NAME_JAVA_SQL_BLOB, "com.fasterxml.jackson.databind.ext.SqlBlobSerializer");
        this._sqlSerializers.put(CLS_NAME_JAVA_SQL_SERIALBLOB, "com.fasterxml.jackson.databind.ext.SqlBlobSerializer");
    }

    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        Object ob;
        JsonSerializer<?> ser;
        Class<?> rawType = type.getRawClass();
        if (_IsXOfY(rawType, CLASS_DOM_NODE)) {
            return (JsonSerializer) instantiate(SERIALIZER_FOR_DOM_NODE, type);
        }
        if (_jdk7Helper != null && (ser = _jdk7Helper.getSerializerForJavaNioFilePath(rawType)) != null) {
            return ser;
        }
        String className = rawType.getName();
        Object sqlHandler = this._sqlSerializers.get(className);
        if (sqlHandler != null) {
            if (sqlHandler instanceof JsonSerializer) {
                return (JsonSerializer) sqlHandler;
            }
            return (JsonSerializer) instantiate((String) sqlHandler, type);
        }
        if ((!className.startsWith(PACKAGE_PREFIX_JAVAX_XML) && !hasSuperClassStartingWith(rawType, PACKAGE_PREFIX_JAVAX_XML)) || (ob = instantiate(SERIALIZERS_FOR_JAVAX_XML, type)) == null) {
            return null;
        }
        return ((Serializers) ob).findSerializer(config, type, beanDesc);
    }

    public JsonDeserializer<?> findDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        Object ob;
        JsonDeserializer<?> deser;
        Class<?> rawType = type.getRawClass();
        if (_jdk7Helper != null && (deser = _jdk7Helper.getDeserializerForJavaNioFilePath(rawType)) != null) {
            return deser;
        }
        if (_IsXOfY(rawType, CLASS_DOM_NODE)) {
            return (JsonDeserializer) instantiate(DESERIALIZER_FOR_DOM_NODE, type);
        }
        if (_IsXOfY(rawType, CLASS_DOM_DOCUMENT)) {
            return (JsonDeserializer) instantiate(DESERIALIZER_FOR_DOM_DOCUMENT, type);
        }
        String className = rawType.getName();
        String deserName = this._sqlDeserializers.get(className);
        if (deserName != null) {
            return (JsonDeserializer) instantiate(deserName, type);
        }
        if ((!className.startsWith(PACKAGE_PREFIX_JAVAX_XML) && !hasSuperClassStartingWith(rawType, PACKAGE_PREFIX_JAVAX_XML)) || (ob = instantiate(DESERIALIZERS_FOR_JAVAX_XML, type)) == null) {
            return null;
        }
        return ((Deserializers) ob).findBeanDeserializer(type, config, beanDesc);
    }

    public boolean hasDeserializerFor(Class<?> valueType) {
        if (_IsXOfY(valueType, CLASS_DOM_NODE) || _IsXOfY(valueType, CLASS_DOM_DOCUMENT)) {
            return true;
        }
        String className = valueType.getName();
        if (className.startsWith(PACKAGE_PREFIX_JAVAX_XML) || hasSuperClassStartingWith(valueType, PACKAGE_PREFIX_JAVAX_XML)) {
            return true;
        }
        return this._sqlDeserializers.containsKey(className);
    }

    private boolean _IsXOfY(Class<?> valueType, Class<?> expType) {
        return expType != null && expType.isAssignableFrom(valueType);
    }

    private Object instantiate(String className, JavaType valueType) {
        try {
            return instantiate(Class.forName(className), valueType);
        } catch (Throwable e) {
            ExceptionUtil.rethrowIfFatal(e);
            throw new IllegalStateException("Failed to find class `" + className + "` for handling values of type " + ClassUtil.getTypeDescription(valueType) + ", problem: (" + e.getClass().getName() + ") " + e.getMessage());
        }
    }

    private Object instantiate(Class<?> handlerClass, JavaType valueType) {
        try {
            return ClassUtil.createInstance(handlerClass, false);
        } catch (Throwable e) {
            ExceptionUtil.rethrowIfFatal(e);
            throw new IllegalStateException("Failed to create instance of `" + handlerClass.getName() + "` for handling values of type " + ClassUtil.getTypeDescription(valueType) + ", problem: (" + e.getClass().getName() + ") " + e.getMessage());
        }
    }

    private boolean hasSuperClassStartingWith(Class<?> rawType, String prefix) {
        Class<?> superclass = rawType.getSuperclass();
        while (true) {
            Class<?> supertype = superclass;
            if (supertype == null || supertype == Object.class) {
                return false;
            }
            if (!supertype.getName().startsWith(prefix)) {
                superclass = supertype.getSuperclass();
            } else {
                return true;
            }
        }
    }
}
