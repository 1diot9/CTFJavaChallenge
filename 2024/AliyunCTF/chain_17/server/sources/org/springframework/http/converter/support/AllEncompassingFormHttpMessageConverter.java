package org.springframework.http.converter.support;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.cbor.KotlinSerializationCborHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.KotlinSerializationProtobufHttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/support/AllEncompassingFormHttpMessageConverter.class */
public class AllEncompassingFormHttpMessageConverter extends FormHttpMessageConverter {
    private static final boolean jaxb2Present;
    private static final boolean jackson2Present;
    private static final boolean jackson2XmlPresent;
    private static final boolean jackson2SmilePresent;
    private static final boolean gsonPresent;
    private static final boolean jsonbPresent;
    private static final boolean kotlinSerializationCborPresent;
    private static final boolean kotlinSerializationJsonPresent;
    private static final boolean kotlinSerializationProtobufPresent;

    static {
        ClassLoader classLoader = AllEncompassingFormHttpMessageConverter.class.getClassLoader();
        jaxb2Present = ClassUtils.isPresent("jakarta.xml.bind.Binder", classLoader);
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
        jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", classLoader);
        jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
        gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", classLoader);
        jsonbPresent = ClassUtils.isPresent("jakarta.json.bind.Jsonb", classLoader);
        kotlinSerializationCborPresent = ClassUtils.isPresent("kotlinx.serialization.cbor.Cbor", classLoader);
        kotlinSerializationJsonPresent = ClassUtils.isPresent("kotlinx.serialization.json.Json", classLoader);
        kotlinSerializationProtobufPresent = ClassUtils.isPresent("kotlinx.serialization.protobuf.ProtoBuf", classLoader);
    }

    public AllEncompassingFormHttpMessageConverter() {
        if (jaxb2Present && !jackson2XmlPresent) {
            addPartConverter(new Jaxb2RootElementHttpMessageConverter());
        }
        if (kotlinSerializationJsonPresent) {
            addPartConverter(new KotlinSerializationJsonHttpMessageConverter());
        }
        if (jackson2Present) {
            addPartConverter(new MappingJackson2HttpMessageConverter());
        } else if (gsonPresent) {
            addPartConverter(new GsonHttpMessageConverter());
        } else if (jsonbPresent) {
            addPartConverter(new JsonbHttpMessageConverter());
        }
        if (jackson2XmlPresent) {
            addPartConverter(new MappingJackson2XmlHttpMessageConverter());
        }
        if (jackson2SmilePresent) {
            addPartConverter(new MappingJackson2SmileHttpMessageConverter());
        }
        if (kotlinSerializationCborPresent) {
            addPartConverter(new KotlinSerializationCborHttpMessageConverter());
        }
        if (kotlinSerializationProtobufPresent) {
            addPartConverter(new KotlinSerializationProtobufHttpMessageConverter());
        }
    }
}
