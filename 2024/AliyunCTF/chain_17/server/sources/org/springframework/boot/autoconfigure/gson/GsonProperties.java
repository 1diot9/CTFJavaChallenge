package org.springframework.boot.autoconfigure.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.LongSerializationPolicy;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.gson")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/gson/GsonProperties.class */
public class GsonProperties {
    private Boolean generateNonExecutableJson;
    private Boolean excludeFieldsWithoutExposeAnnotation;
    private Boolean serializeNulls;
    private Boolean enableComplexMapKeySerialization;
    private Boolean disableInnerClassSerialization;
    private LongSerializationPolicy longSerializationPolicy;
    private FieldNamingPolicy fieldNamingPolicy;
    private Boolean prettyPrinting;
    private Boolean lenient;
    private Boolean disableHtmlEscaping;
    private String dateFormat;

    public Boolean getGenerateNonExecutableJson() {
        return this.generateNonExecutableJson;
    }

    public void setGenerateNonExecutableJson(Boolean generateNonExecutableJson) {
        this.generateNonExecutableJson = generateNonExecutableJson;
    }

    public Boolean getExcludeFieldsWithoutExposeAnnotation() {
        return this.excludeFieldsWithoutExposeAnnotation;
    }

    public void setExcludeFieldsWithoutExposeAnnotation(Boolean excludeFieldsWithoutExposeAnnotation) {
        this.excludeFieldsWithoutExposeAnnotation = excludeFieldsWithoutExposeAnnotation;
    }

    public Boolean getSerializeNulls() {
        return this.serializeNulls;
    }

    public void setSerializeNulls(Boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }

    public Boolean getEnableComplexMapKeySerialization() {
        return this.enableComplexMapKeySerialization;
    }

    public void setEnableComplexMapKeySerialization(Boolean enableComplexMapKeySerialization) {
        this.enableComplexMapKeySerialization = enableComplexMapKeySerialization;
    }

    public Boolean getDisableInnerClassSerialization() {
        return this.disableInnerClassSerialization;
    }

    public void setDisableInnerClassSerialization(Boolean disableInnerClassSerialization) {
        this.disableInnerClassSerialization = disableInnerClassSerialization;
    }

    public LongSerializationPolicy getLongSerializationPolicy() {
        return this.longSerializationPolicy;
    }

    public void setLongSerializationPolicy(LongSerializationPolicy longSerializationPolicy) {
        this.longSerializationPolicy = longSerializationPolicy;
    }

    public FieldNamingPolicy getFieldNamingPolicy() {
        return this.fieldNamingPolicy;
    }

    public void setFieldNamingPolicy(FieldNamingPolicy fieldNamingPolicy) {
        this.fieldNamingPolicy = fieldNamingPolicy;
    }

    public Boolean getPrettyPrinting() {
        return this.prettyPrinting;
    }

    public void setPrettyPrinting(Boolean prettyPrinting) {
        this.prettyPrinting = prettyPrinting;
    }

    public Boolean getLenient() {
        return this.lenient;
    }

    public void setLenient(Boolean lenient) {
        this.lenient = lenient;
    }

    public Boolean getDisableHtmlEscaping() {
        return this.disableHtmlEscaping;
    }

    public void setDisableHtmlEscaping(Boolean disableHtmlEscaping) {
        this.disableHtmlEscaping = disableHtmlEscaping;
    }

    public String getDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
