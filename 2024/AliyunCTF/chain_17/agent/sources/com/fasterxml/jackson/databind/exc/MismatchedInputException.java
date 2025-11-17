package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.ClassUtil;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/exc/MismatchedInputException.class */
public class MismatchedInputException extends JsonMappingException {
    protected Class<?> _targetType;

    /* JADX INFO: Access modifiers changed from: protected */
    public MismatchedInputException(JsonParser p, String msg) {
        this(p, msg, (JavaType) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MismatchedInputException(JsonParser p, String msg, JsonLocation loc) {
        super(p, msg, loc);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MismatchedInputException(JsonParser p, String msg, Class<?> targetType) {
        super(p, msg);
        this._targetType = targetType;
    }

    protected MismatchedInputException(JsonParser p, String msg, JavaType targetType) {
        super(p, msg);
        this._targetType = ClassUtil.rawClass(targetType);
    }

    @Deprecated
    public static MismatchedInputException from(JsonParser p, String msg) {
        return from(p, (Class<?>) null, msg);
    }

    public static MismatchedInputException from(JsonParser p, JavaType targetType, String msg) {
        return new MismatchedInputException(p, msg, targetType);
    }

    public static MismatchedInputException from(JsonParser p, Class<?> targetType, String msg) {
        return new MismatchedInputException(p, msg, targetType);
    }

    public MismatchedInputException setTargetType(JavaType t) {
        this._targetType = t.getRawClass();
        return this;
    }

    public Class<?> getTargetType() {
        return this._targetType;
    }
}
